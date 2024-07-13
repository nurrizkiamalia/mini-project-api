package com.mini_project.miniproject.orders.service.impl;

import com.mini_project.miniproject.events.repository.EventRepository;
import com.mini_project.miniproject.events.repository.EventVouchersRepository;
import com.mini_project.miniproject.events.repository.TicketTiersRepository;
import com.mini_project.miniproject.exceptions.ApplicationException;
import com.mini_project.miniproject.orders.dto.ConfirmPaymentRequestDTO;
import com.mini_project.miniproject.orders.dto.CreateOrderRequestDTO;
import com.mini_project.miniproject.orders.dto.CreateOrderResponseDTO;
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

@Service
public class OrderServiceImpl implements OrderService {
    private final OrderRespository orderRespository;
    private final OrderItemRepository orderItemRepository;
    private  final OrderDiscountRepository orderDiscountRepository;
    private final PointsRepository pointsRepository;
    private final ReferralDiscountRepository referralDiscountRepository;
    private final UserRepository userRepository;
    private final EventRepository eventRepository;
    private final TicketTiersRepository ticketTiersRepository;
    private final EventVouchersRepository eventVouchersRepository;

    public OrderServiceImpl (
            OrderRespository orderRespository,
            OrderItemRepository orderItemRepository,
            OrderDiscountRepository orderDiscountRepository,
            PointsRepository pointsRepository,
            ReferralDiscountRepository referralDiscountRepository,
            UserRepository userRepository,
            EventRepository eventRepository,
            TicketTiersRepository ticketTiersRepository,
            EventVouchersRepository eventVouchersRepository){
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
    public CreateOrderResponseDTO createOrder(CreateOrderRequestDTO createOrderRequestDTO, Authentication authentication){
        // extract userId from JWT
        Jwt jwt = (Jwt) authentication.getPrincipal();
        Long userId = jwt.getClaim("userId");

        // validate userId in userRepository
        if (userRepository.findById(userId).isEmpty()){
            throw  new ApplicationException("User not found.");
        }

        // validate eventId from eventRepository
        var event = eventRepository.findById(createOrderRequestDTO.getEventId()).orElseThrow(() -> new ApplicationException("Event not found"));

        // validate if the buyer of the event is not the event's organizer
        if(Objects.equals(event.getOrganizer().getId(), userId)) {
            throw new ApplicationException("Cannot buy your own event");
        }

        // create new order
        Orders order = new Orders();
        order.setCustomerId(userId);
        order.setEventId(event.getId());
        order.setStatus(false);

        // initialize original price and total price
        // BigDecimal originalPrice = BigDecimal.ZERO;
        BigDecimal totalPrice = BigDecimal.ZERO;

        // get the ticket(s) & validate
        for (CreateOrderRequestDTO.TicketRequest ticketRequest : createOrderRequestDTO.getTickets()){
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

        // get the original price and total price
        // order.setOriginalPrice(totalPrice);
        order.setTotalPrice(totalPrice);

        // get voucher(s) & validate
        if (createOrderRequestDTO.getEventVoucherId() != null) {
            var voucher = eventVouchersRepository.findById(createOrderRequestDTO.getEventVoucherId()).orElseThrow(() -> new ApplicationException("Voucher not found"));
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
        if (createOrderRequestDTO.isUseDisc10()) {
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

        }


        // get the points & check if the buyer has it and the amount is sufficient
        if (createOrderRequestDTO.getPoints() > 0){
            List<Points> userPoints = pointsRepository.findByUserIdAndExpiryDateAfterOrderByExpiryDateAsc(userId, LocalDate.now());
            int availablePoints = userPoints.stream().mapToInt(Points::getAmount).sum();
            if (availablePoints >= createOrderRequestDTO.getPoints()){
                BigDecimal pointsDiscount;
                if (createOrderRequestDTO.getPoints() > totalPrice.intValue()) {
                    // If points exceed total price, cap the discount at the total price
                    pointsDiscount = totalPrice;
                } else {
                    pointsDiscount = BigDecimal.valueOf(createOrderRequestDTO.getPoints());
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
        }

        // finalize order
        // order.setOriginalPrice(originalPrice);
        order.setTotalPrice(totalPrice);
        order = orderRespository.save(order);

        // create order response
        CreateOrderResponseDTO response = new CreateOrderResponseDTO();
        response.setOrderId(order.getId());
        response.setFinalPrice(order.getTotalPrice());

        return response;
    }

    @Override
    @Transactional
    public void confirmPayment(ConfirmPaymentRequestDTO confirmPaymentRequestDTO, Authentication authentication) {
        return;
    }
}