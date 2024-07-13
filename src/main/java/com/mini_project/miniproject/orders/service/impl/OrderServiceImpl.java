package com.mini_project.miniproject.orders.service.impl;

import com.mini_project.miniproject.events.entity.Events;
import com.mini_project.miniproject.events.entity.TicketTiers;
import com.mini_project.miniproject.events.repository.EventRepository;
import com.mini_project.miniproject.events.repository.EventVouchersRepository;
import com.mini_project.miniproject.events.repository.TicketTiersRepository;
import com.mini_project.miniproject.exceptions.ApplicationException;
import com.mini_project.miniproject.orders.dto.ConfirmPaymentRequestDTO;
import com.mini_project.miniproject.orders.dto.CreateOrderRequestDTO;
import com.mini_project.miniproject.orders.dto.CreateOrderResponseDTO;
import com.mini_project.miniproject.orders.dto.OrderDetailsDTO;
import com.mini_project.miniproject.orders.entity.OrderDiscounts;
import com.mini_project.miniproject.orders.entity.OrderItems;
import com.mini_project.miniproject.orders.entity.Orders;
import com.mini_project.miniproject.orders.repository.OrderDiscountRepository;
import com.mini_project.miniproject.orders.repository.OrderItemRepository;
import com.mini_project.miniproject.orders.repository.OrderRespository;
import com.mini_project.miniproject.orders.service.OrderService;
import com.mini_project.miniproject.user.entity.Points;
import com.mini_project.miniproject.user.repository.PointsRepository;
import com.mini_project.miniproject.user.repository.ReferralDiscountRepository;
import com.mini_project.miniproject.user.repository.UserRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class OrderServiceImpl implements OrderService {
    private final OrderRespository orderRespository;
    private final OrderItemRepository orderItemRepository;
    private final OrderDiscountRepository orderDiscountRepository;
    private final PointsRepository pointsRepository;
    private final ReferralDiscountRepository referralDiscountRepository;
    private final UserRepository userRepository;
    private final EventRepository eventRepository;
    private final TicketTiersRepository ticketTiersRepository;
    private final EventVouchersRepository eventVouchersRepository;

    public OrderServiceImpl(
            OrderRespository orderRespository,
            OrderItemRepository orderItemRepository,
            OrderDiscountRepository orderDiscountRepository,
            PointsRepository pointsRepository,
            ReferralDiscountRepository referralDiscountRepository,
            UserRepository userRepository,
            EventRepository eventRepository,
            TicketTiersRepository ticketTiersRepository,
            EventVouchersRepository eventVouchersRepository) {
        this.orderRespository = orderRespository;
        this.orderItemRepository = orderItemRepository;
        this.orderDiscountRepository = orderDiscountRepository;
        this.pointsRepository = pointsRepository;
        this.referralDiscountRepository = referralDiscountRepository;
        this.userRepository = userRepository;
        this.eventRepository = eventRepository;
        this.ticketTiersRepository = ticketTiersRepository;
        this.eventVouchersRepository = eventVouchersRepository;
    }

    @Override
    @Transactional
    public CreateOrderResponseDTO createOrder(CreateOrderRequestDTO createOrderRequestDTO, Authentication authentication) {
        // extract userId from JWT
        Jwt jwt = (Jwt) authentication.getPrincipal();
        Long userId = jwt.getClaim("userId");

        // validate userId in userRepository
        if (userRepository.findById(userId).isEmpty()) {
            throw new ApplicationException("User not found.");
        }

        // validate eventId from eventRepository
        var event = eventRepository.findById(createOrderRequestDTO.getEventId()).orElseThrow(() -> new ApplicationException("Event not found"));

        // validate if the buyer of the event is not the event's organizer
        if (Objects.equals(event.getOrganizer().getId(), userId)) {
            throw new ApplicationException("Cannot buy your own event");
        }

        // create new order
        Orders order = new Orders();
        order.setCustomerId(userId);
        order.setEventId(event.getId());
        order.setStatus(false);

        // initialize total price
        BigDecimal totalPrice = BigDecimal.ZERO;

        // get the ticket(s) & validate
        for (CreateOrderRequestDTO.TicketRequest ticketRequest : createOrderRequestDTO.getTickets()) {
            var ticketTier = ticketTiersRepository.findById(ticketRequest.getTicketId()).orElseThrow(() -> new ApplicationException("Ticket not found with id:" + ticketRequest.getTicketId()));
            BigDecimal itemPrice = ticketTier.getPrice().multiply(BigDecimal.valueOf(ticketRequest.getQuantity()));
            totalPrice = totalPrice.add(itemPrice);

            OrderItems orderItem = new OrderItems();
            orderItem.setOrder(order);
            orderItem.setTicketTierId(ticketTier.getId());
            orderItem.setQuantity(ticketRequest.getQuantity());
            orderItem.setPricePerTicket(ticketTier.getPrice());
            order.getOrderItems().add(orderItem);

        }

        // get original price and temp total price
        order.setTotalPrice(totalPrice);
        BigDecimal originalPrice = totalPrice;
        order.setOriginalPrice(originalPrice);


        // get voucher(s) & validate
        if (createOrderRequestDTO.getEventVoucherId() != null) {
            var voucher = eventVouchersRepository.findById(createOrderRequestDTO.getEventVoucherId())
                    .orElseThrow(() -> new ApplicationException("Voucher not found"));
            LocalDate now = LocalDate.now();
            if (voucher.getStartDate().compareTo(now) <= 0 && voucher.getEndDate().compareTo(now) >= 0) {
                BigDecimal discountAmount = totalPrice.multiply(voucher.getDiscountPercentage().divide(BigDecimal.valueOf(100)));

                OrderDiscounts discount = new OrderDiscounts();
                discount.setOrder(order);
                discount.setDiscountType("VOUCHER");
                discount.setDiscountAmount(discountAmount);
                order.getOrderDiscounts().add(discount);

                totalPrice = totalPrice.subtract(discountAmount);
            }
        }

        // get the disc10 & check if the buyer has the discount
        if (Boolean.TRUE.equals(createOrderRequestDTO.getUseDisc10())) {
            // check if the event has sufficient referral quota
            if (event.getReferralQuota() != null && event.getReferralQuota() > 0) {
                var referralDiscount = referralDiscountRepository.findByUserIdAndExpiryDateAfter(userId, LocalDate.now());
                if (referralDiscount != null) {
                    BigDecimal discountAmount = totalPrice.multiply(referralDiscount.getDiscountPercentage().divide(BigDecimal.valueOf(100)));

                    OrderDiscounts discount = new OrderDiscounts();
                    discount.setOrder(order);
                    discount.setDiscountType("REFERRAL");
                    discount.setDiscountAmount(discountAmount);
                    order.getOrderDiscounts().add(discount);

                    totalPrice = totalPrice.subtract(discountAmount);
                }
            } else {
                throw new ApplicationException("No referral quota available for this event.");
            }
        }

        // get the points & check if the buyer has it and the amount is sufficient
        int requestedPoints = createOrderRequestDTO.getPoints() != null ? createOrderRequestDTO.getPoints() : 0;
        if (requestedPoints > 0) {
            List<Points> userPoints = pointsRepository.findByUserIdAndExpiryDateAfterOrderByExpiryDateAsc(userId, LocalDate.now());
            int availablePoints = userPoints.stream().mapToInt(Points::getAmount).sum();
            if (availablePoints >= requestedPoints) {
                BigDecimal pointsDiscount;
                if (requestedPoints > totalPrice.intValue()) {
                    // If points exceed total price, cap the discount at the total price
                    pointsDiscount = totalPrice;
                } else {
                    pointsDiscount = BigDecimal.valueOf(requestedPoints);
                }

                OrderDiscounts discount = new OrderDiscounts();
                discount.setOrder(order);
                discount.setDiscountType("POINTS");
                discount.setDiscountAmount(pointsDiscount);
                order.getOrderDiscounts().add(discount);

                totalPrice = totalPrice.subtract(pointsDiscount);

                // Ensure totalPrice doesn't go below zero
                totalPrice = totalPrice.max(BigDecimal.ZERO);
            }
//            } else {
//                throw new ApplicationException("Insufficient points. Available points: " + availablePoints);
//            }
        }

        // finalize order
        order.setOriginalPrice(originalPrice);
        order.setTotalPrice(totalPrice);
        order = orderRespository.save(order);

        // create order response
        CreateOrderResponseDTO response = new CreateOrderResponseDTO();
        response.setOrderId(order.getId());
        response.setOriginalPrice(order.getOriginalPrice());
        response.setFinalPrice(order.getTotalPrice());
        List<OrderDiscounts> orderDiscounts = orderDiscountRepository.findAllByOrderId(order.getId());
        List<CreateOrderResponseDTO.OrderDiscountsDTO> appliedDiscounts = orderDiscounts.stream()
                .map(discount -> {
                    CreateOrderResponseDTO.OrderDiscountsDTO dto = response.new OrderDiscountsDTO();
                    dto.setDiscountType(discount.getDiscountType());
                    dto.setDiscountAmount(discount.getDiscountAmount());
                    return dto;
                })
                .collect(Collectors.toList());
        response.setAppliedDiscounts(appliedDiscounts);

        return response;
    }

    @Override
    @Transactional
    public void confirmPayment(ConfirmPaymentRequestDTO confirmPaymentRequestDTO, Authentication authentication) {
        // Extract userId from JWT
        Jwt jwt = (Jwt) authentication.getPrincipal();
        Long userId = jwt.getClaim("userId");

        // Check if the user exists
        userRepository.findById(userId)
                .orElseThrow(() -> new ApplicationException("User not found."));

        // Check if the order exists
        Orders order = orderRespository.findById(confirmPaymentRequestDTO.getOrderId())
                .orElseThrow(() -> new ApplicationException("Order not found."));

        // Validate if the order belongs to the user
        if (!order.getCustomerId().equals(userId)) {
            throw new ApplicationException("This order does not belong to the authenticated user.");
        }

        // Check order status
        if (order.isStatus()) {
            throw new ApplicationException("This order has already been paid.");
        }

        // Process points discount if used
        OrderDiscounts pointsDiscount = order.getOrderDiscounts().stream()
                .filter(d -> "POINTS".equals(d.getDiscountType()))
                .findFirst()
                .orElse(null);

        if (pointsDiscount != null) {
            int pointsUsed = pointsDiscount.getDiscountAmount().intValue();
            List<Points> userPoints = pointsRepository.findByUserIdAndExpiryDateAfterOrderByExpiryDateAsc(userId, LocalDate.now());
            int remainingPointsToUse = pointsUsed;

            for (Points points : userPoints) {
                if (remainingPointsToUse <= 0) break;
                if (points.getAmount() <= remainingPointsToUse) {
                    remainingPointsToUse -= points.getAmount();
                    pointsRepository.delete(points);
                } else {
                    points.setAmount(points.getAmount() - remainingPointsToUse);
                    pointsRepository.save(points);
                    break;
                }
            }
        }

        // Process DISC10 if used
        OrderDiscounts referralDiscount = order.getOrderDiscounts().stream()
                .filter(d -> "REFERRAL".equals(d.getDiscountType()))
                .findFirst()
                .orElse(null);

        if (referralDiscount != null) {
            // Delete DISC10 for the user
            referralDiscountRepository.deleteByUserId(userId);

            // Decrement the referral quota in the purchased event by 1
            var event = eventRepository.findById(order.getEventId())
                    .orElseThrow(() -> new ApplicationException("Event not found."));
            if (event.getReferralQuota() != null && event.getReferralQuota() > 0) {
                event.setReferralQuota(event.getReferralQuota() - 1);
                eventRepository.save(event);
            }
        }

        // Subtract total seats in ticket tiers based on the purchased ticket's quantity
        for (OrderItems item : order.getOrderItems()) {
            var ticketTier = ticketTiersRepository.findById(item.getTicketTierId())
                    .orElseThrow(() -> new ApplicationException("Ticket tier not found."));
            int remainingSeats = ticketTier.getTotalSeats() - item.getQuantity();
            if (remainingSeats < 0) {
                throw new ApplicationException("Not enough seats available for ticket: " + ticketTier.getName());
            }
            ticketTier.setTotalSeats(remainingSeats);
            ticketTiersRepository.save(ticketTier);
        }

        // Set the order's status to true (paid)
        order.setStatus(true);

        // Set the order's payment method (if inputted)
        if (confirmPaymentRequestDTO.getPaymentMethod() != null && !confirmPaymentRequestDTO.getPaymentMethod().isEmpty()) {
            order.setPaymentMethod(confirmPaymentRequestDTO.getPaymentMethod());
        }

        // Save the updated order
        orderRespository.save(order);
    }

    @Override
    public OrderDetailsDTO getOrderDetails(Long orderId, Authentication authentication) {
        // Get userId from JWT token
        Jwt jwt = (Jwt) authentication.getPrincipal();
        Long userId = jwt.getClaim("userId");

        // Get the order by its id and status
        Orders order = orderRespository.findByIdAndStatus(orderId, true)
                .orElseThrow(() -> new ApplicationException("Order not found"));

        // Check if the order belongs to the user
        if (!order.getCustomerId().equals(userId)) {
            throw new ApplicationException("You are not authorized to view this order");
        }

        OrderDetailsDTO orderDetails = new OrderDetailsDTO();
        orderDetails.setId(order.getId());
        orderDetails.setTotalPrice(order.getTotalPrice());

        // Get the order's total tickets and ticket details
        List<OrderItems> orderItems = orderItemRepository.findByOrderId(orderId);
        int totalTickets = orderItems.stream().mapToInt(OrderItems::getQuantity).sum();
        orderDetails.setTotalTickets(totalTickets);

        List<OrderDetailsDTO.TicketDetailsDTO> ticketDetailsList = orderItems.stream()
                .map(item -> {
                    OrderDetailsDTO.TicketDetailsDTO ticketDetails = orderDetails.new TicketDetailsDTO();
                    TicketTiers ticketTier = ticketTiersRepository.findById(item.getTicketTierId())
                            .orElseThrow(() -> new ApplicationException("Ticket tier not found"));
                    ticketDetails.setTicketTier(ticketTier.getName());
                    ticketDetails.setQuantity(item.getQuantity());
                    return ticketDetails;
                })
                .collect(Collectors.toList());
        orderDetails.setTicketDetails(ticketDetailsList);

        // Get the event details
        Events event = eventRepository.findById(order.getEventId())
                .orElseThrow(() -> new ApplicationException("Event not found"));

        OrderDetailsDTO.EventDetailsDTO eventDetails = orderDetails.new EventDetailsDTO();
        eventDetails.setId(event.getId());
        eventDetails.setName(event.getName());
        eventDetails.setCategory(event.getCategory());
        eventDetails.setDate(event.getDate());
        eventDetails.setTime(event.getTime());
        eventDetails.setLocation(event.getLocation());
        eventDetails.setCity(event.getCity());
        orderDetails.setEventDetail(eventDetails);

        return orderDetails;
    }

}


//    @Override
//    public OrderDetailsDTO getOrderDetails(Authentication authentication, Long orderId) {
//        // get userId from jwt token
//        // get the order by its id
//        // check if the order belongs to the user
//        // get the order's total price
//        // get the order's total ticket (get order items by its order_id in table order_items, get the quantity)
//        // get the ticket details (from the table order_items fields quantity and ticket_tier_id, from the ticket_tier_id get the name of the ticket from the table ticket_tiers)
//        // get the event details (get the event details of the order, there is a field event_id in table orders that refers to table events)
//        return null;
//    }

//    @Override
//    @Transactional
//    public void confirmPayment(ConfirmPaymentRequestDTO confirmPaymentRequestDTO, Authentication authentication) {
//        // check and validate user
//        // check if the order exists
//        // check and validate if the order belongs to the user
//        // check order status
//        // delete points id used
//        // delete disc10 if used and decrement the referral quota in the purchased event by 1
//        // subtract total seats in ticket tiers based on the purchased ticket's quantity
//        // set the order's status to true
//        // set the order's payment method (if inputted)
//        // save order
//    }




//        // get voucher(s) & validate
//        if (createOrderRequestDTO.getEventVoucherId() != null) {
//            var voucher = eventVouchersRepository.findById(createOrderRequestDTO.getEventVoucherId()).orElseThrow(() -> new ApplicationException("Voucher not found"));
//            LocalDate now = LocalDate.now();
//            if (voucher.getStartDate().compareTo(now) <= 0 && voucher.getEndDate().compareTo(now) >= 0) {
//                BigDecimal discountAmount = totalPrice.multiply(voucher.getDiscountPercentage().divide(BigDecimal.valueOf(100)));
//
//                OrderDiscounts discount = new OrderDiscounts();
//                discount.setOrder(order);
//                discount.setDiscountType("VOUCHER");
//                discount.setDiscountAmount(discountAmount);
//                order.getOrderDiscounts().add(discount);
//
//                totalPrice = totalPrice.subtract(discountAmount);
//            }
//        }
//
//        // get the disc10 & check if the buyer has the discount
//        if (createOrderRequestDTO.isUseDisc10()) {
//            var referralDiscount = referralDiscountRepository.findByUserIdAndExpiryDateAfter(userId, LocalDate.now());
//            if (referralDiscount != null) {
//                BigDecimal discountAmount = totalPrice.multiply(referralDiscount.getDiscountPercentage().divide(BigDecimal.valueOf(100)));
//
//                OrderDiscounts discount = new OrderDiscounts();
//                discount.setOrder(order);
//                discount.setDiscountType("REFERRAL");
//                discount.setDiscountAmount(discountAmount);
//                order.getOrderDiscounts().add(discount);
//
//                totalPrice = totalPrice.subtract(discountAmount);
//
//            }
//
//        }
//
//
//        // get the points & check if the buyer has it and the amount is sufficient
//        if (createOrderRequestDTO.getPoints() > 0){
//            List<Points> userPoints = pointsRepository.findByUserIdAndExpiryDateAfterOrderByExpiryDateAsc(userId, LocalDate.now());
//            int availablePoints = userPoints.stream().mapToInt(Points::getAmount).sum();
//            if (availablePoints >= createOrderRequestDTO.getPoints()){
//                BigDecimal pointsDiscount;
//                if (createOrderRequestDTO.getPoints() > totalPrice.intValue()) {
//                    // If points exceed total price, cap the discount at the total price
//                    pointsDiscount = totalPrice;
//                } else {
//                    pointsDiscount = BigDecimal.valueOf(createOrderRequestDTO.getPoints());
//                }
//
//                OrderDiscounts discount = new OrderDiscounts();
//                discount.setOrder(order);
//                discount.setDiscountType("POINTS");
//                discount.setDiscountAmount(pointsDiscount);
//                order.getOrderDiscounts().add(discount);
//
//                totalPrice = totalPrice.subtract(pointsDiscount);
//
//                // Ensure totalPrice doesn't go below zero
//                totalPrice = totalPrice.max(BigDecimal.ZERO);
//            }
//        }