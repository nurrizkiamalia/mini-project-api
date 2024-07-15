package com.mini_project.miniproject.dashboard.service.impl;

import com.mini_project.miniproject.dashboard.dto.RevenueByEventDTO;
import com.mini_project.miniproject.dashboard.dto.SalePerEventCategoryDTO;
import com.mini_project.miniproject.dashboard.service.DashboardService;
import com.mini_project.miniproject.events.repository.EventRepository;
import com.mini_project.miniproject.events.repository.TicketTiersRepository;
import com.mini_project.miniproject.exceptions.ApplicationException;
import com.mini_project.miniproject.orders.repository.OrderItemRepository;
import com.mini_project.miniproject.orders.repository.OrderRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class DashboardServiceImpl implements DashboardService {
    private final EventRepository eventRepository;
    private final TicketTiersRepository ticketTiersRepository;
    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;

    public DashboardServiceImpl(
            EventRepository eventRepository,
            TicketTiersRepository ticketTiersRepository,
            OrderRepository orderRepository,
            OrderItemRepository orderItemRepository){
        this.eventRepository = eventRepository;
        this.ticketTiersRepository = ticketTiersRepository;
        this.orderRepository = orderRepository;
        this.orderItemRepository = orderItemRepository;
    }

    @Override
    public SalePerEventCategoryDTO getSalePerEventCategory(Authentication authentication) {
        // Validate user from JWT
        Jwt jwt = (Jwt) authentication.getPrincipal();
        Long organizerId = jwt.getClaim("userId");
        String userRole = jwt.getClaim("role");

        // Only organizer can access this feature
        if (!"ORGANIZER".equals(userRole)) {
            throw new ApplicationException("Only organizers can access this feature");
        }

        // Get total seats of all events created by the current organizer
//        int totalSeats = ticketTiersRepository.sumTotalSeatsByOrganizerId(organizerId);

        // Get sold seats from order items
        int soldSeats = orderItemRepository.sumQuantityByOrganizerIdAndOrderStatus(organizerId, true);

        // Get sales details per event category
        List<Object[]> salesData = orderRepository.getSalesDataByOrganizerId(organizerId);

        double totalRevenue = salesData.stream()
                .mapToDouble(data -> ((Number) data[2]).doubleValue())
                .sum();

        List<SalePerEventCategoryDTO.SaleDetailDTO> saleDetails = salesData.stream()
                .map(data -> {
                    SalePerEventCategoryDTO.SaleDetailDTO detail = new SalePerEventCategoryDTO.SaleDetailDTO();
                    detail.setEventCategory((String) data[0]);
                    detail.setTotalTicketSold(((Number) data[1]).intValue());
                    double categoryRevenue = ((Number) data[2]).doubleValue();
                    detail.setRevenuePercentage(Math.round((categoryRevenue / totalRevenue) * 100.0 * 100.0) / 100.0);
                    return detail;
                })
                .collect(Collectors.toList());

        SalePerEventCategoryDTO result = new SalePerEventCategoryDTO();
//        result.setTotalSeats(totalSeats);
        result.setSoldSeats(soldSeats);
        result.setSaleDetails(saleDetails);

        return result;
    }

    @Override
    public RevenueByEventDTO getRevenueByEvent(Authentication authentication) {
        // Validate user from JWT
        Jwt jwt = (Jwt) authentication.getPrincipal();
        Long organizerId = jwt.getClaim("userId");
        String userRole = jwt.getClaim("role");

        // Only organizer can access this feature
        if (!"ORGANIZER".equals(userRole)) {
            throw new ApplicationException("Only organizers can access this feature");
        }

        List<Object[]> revenueData = orderRepository.getRevenueByEventForOrganizer(organizerId);

        RevenueByEventDTO result = new RevenueByEventDTO();
        List<RevenueByEventDTO.EventRevenueDTO> eventRevenues = new ArrayList<>();

        BigDecimal totalRevenue = BigDecimal.ZERO;
        BigDecimal otherEventsRevenue = BigDecimal.ZERO;

        for (int i = 0; i < revenueData.size(); i++) {
            Object[] data = revenueData.get(i);
            String eventName = (String) data[0];
            BigDecimal revenue = (BigDecimal) data[1];

            totalRevenue = totalRevenue.add(revenue);

            if (i < 4) {
                RevenueByEventDTO.EventRevenueDTO eventRevenue = new RevenueByEventDTO.EventRevenueDTO();
                eventRevenue.setName(eventName);
                eventRevenue.setRevenue(revenue);
                eventRevenues.add(eventRevenue);
            } else {
                otherEventsRevenue = otherEventsRevenue.add(revenue);
            }
        }

        // Add "Other Events" if there are more than 4 events
        if (otherEventsRevenue.compareTo(BigDecimal.ZERO) > 0) {
            RevenueByEventDTO.EventRevenueDTO otherEvents = new RevenueByEventDTO.EventRevenueDTO();
            otherEvents.setName("Other Events");
            otherEvents.setRevenue(otherEventsRevenue);
            eventRevenues.add(otherEvents);
        }

        // Calculate percentages
        for (RevenueByEventDTO.EventRevenueDTO eventRevenue : eventRevenues) {
            BigDecimal percentage = eventRevenue.getRevenue()
                    .divide(totalRevenue, 4, RoundingMode.HALF_UP)
                    .multiply(BigDecimal.valueOf(100))
                    .setScale(2, RoundingMode.HALF_UP);
            eventRevenue.setPercentage(percentage);
        }

        result.setTotalRevenue(totalRevenue);
        result.setEvents(eventRevenues);

        return result;
    }
}


