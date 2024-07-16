package com.mini_project.miniproject.dashboard.service.impl;

import com.mini_project.miniproject.dashboard.dto.ComprehensiveRevenueDTO;
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
import java.sql.Date;
import java.time.LocalDate;
import java.time.Month;
import java.time.Year;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

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
            OrderItemRepository orderItemRepository) {
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

    @Override
    public ComprehensiveRevenueDTO getComprehensiveRevenue(Authentication authentication) {
        // Validate user from JWT
        Jwt jwt = (Jwt) authentication.getPrincipal();
        Long organizerId = jwt.getClaim("userId");
        String userRole = jwt.getClaim("role");

        // Only organizer can access this feature
        if (!"ORGANIZER".equals(userRole)) {
            throw new ApplicationException("Only organizers can access this feature");
        }

        ComprehensiveRevenueDTO result = new ComprehensiveRevenueDTO();

        // Fetch and set yearly revenue
        List<Object[]> yearlyData = orderRepository.getYearlyRevenueForOrganizer(organizerId);
        result.setYearlyRevenue(mapYearlyRevenue(yearlyData));
        result.setTotalYearlyRevenue(calculateTotalRevenue(yearlyData));

//        // Fetch and set monthly revenue for current year
//        int currentYear = Year.now().getValue();
//        List<Object[]> monthlyData = orderRepository.getMonthlyRevenueForOrganizer(organizerId, currentYear);
//        result.setMonthlyRevenue(mapMonthlyRevenue(monthlyData));
//        result.setTotalMonthlyRevenue(calculateTotalRevenue(monthlyData));

        // Fetch and set monthly revenue for current year
        int currentYear = Year.now().getValue();
        List<Object[]> monthlyData = orderRepository.getMonthlyRevenueForOrganizer(organizerId, currentYear);
        List<ComprehensiveRevenueDTO.MonthlyRevenueDTO> monthlyRevenue = mapMonthlyRevenue(monthlyData);
        populateMonthlyRevenue(monthlyRevenue, monthlyData);
        result.setMonthlyRevenue(monthlyRevenue);
        result.setTotalMonthlyRevenue(calculateTotalRevenue(monthlyData));


        // Fetch and set daily revenue
        List<Object[]> dailyData = orderRepository.getDailyRevenueForCurrentMonth(organizerId);
        result.setDailyRevenue(mapDailyRevenue(dailyData));
        result.setTotalDailyRevenue(calculateTotalRevenue(dailyData));


        return result;
    }

    private List<ComprehensiveRevenueDTO.DailyRevenueDTO> mapDailyRevenue(List<Object[]> data) {
        LocalDate now = LocalDate.now();
        LocalDate firstDayOfMonth = now.withDayOfMonth(1);
        LocalDate lastDayOfMonth = now.withDayOfMonth(now.lengthOfMonth());

        Map<LocalDate, BigDecimal> revenueByDate = data.stream()
                .collect(Collectors.toMap(
                        row -> ((Date) row[0]).toLocalDate(),
                        row -> (BigDecimal) row[1]
                ));

        return Stream.iterate(firstDayOfMonth, date -> date.plusDays(1))
                .limit(ChronoUnit.DAYS.between(firstDayOfMonth, lastDayOfMonth) + 1)
                .map(date -> {
                    ComprehensiveRevenueDTO.DailyRevenueDTO dto = new ComprehensiveRevenueDTO.DailyRevenueDTO();
                    dto.setDate(date);
                    dto.setRevenue(revenueByDate.getOrDefault(date, BigDecimal.ZERO));
                    return dto;
                })
                .collect(Collectors.toList());
    }

    private List<ComprehensiveRevenueDTO.YearlyRevenueDTO> mapYearlyRevenue(List<Object[]> data) {
        return data.stream().map(row -> {
            ComprehensiveRevenueDTO.YearlyRevenueDTO dto = new ComprehensiveRevenueDTO.YearlyRevenueDTO();
            dto.setYear((Integer) row[0]);
            dto.setRevenue((BigDecimal) row[1]);
            return dto;
        }).collect(Collectors.toList());
    }

    private List<ComprehensiveRevenueDTO.MonthlyRevenueDTO> mapMonthlyRevenue(List<Object[]> data) {
        return IntStream.rangeClosed(1, 12).mapToObj(monthNumber -> {
            ComprehensiveRevenueDTO.MonthlyRevenueDTO dto = new ComprehensiveRevenueDTO.MonthlyRevenueDTO();
            dto.setMonth(Month.of(monthNumber).name());
            dto.setRevenue(BigDecimal.ZERO);
            return dto;
        }).collect(Collectors.toList());
    }


    private BigDecimal calculateTotalRevenue(List<Object[]> data) {
        return data.stream()
                .map(row -> (BigDecimal) row[1])
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private void populateMonthlyRevenue(List<ComprehensiveRevenueDTO.MonthlyRevenueDTO> monthlyRevenue, List<Object[]> data) {
        data.forEach(row -> {
            int monthNumber = (Integer) row[0];
            BigDecimal revenue = (BigDecimal) row[1];
            monthlyRevenue.get(monthNumber - 1).setRevenue(revenue);
        });
    }
}

    // Fetch and set today's hourly revenue
//        List<Object[]> hourlyData = orderRepository.getTodayHourlyRevenueForOrganizer(organizerId);
//        result.setTodayRevenue(mapHourlyRevenue(hourlyData));
//        result.setTotalTodayRevenue(calculateTotalRevenue(hourlyData));
//    -----------------------------------------------------------------------------------------

    //    private List<ComprehensiveRevenueDTO.HourlyRevenueDTO> mapHourlyRevenue(List<Object[]> data) {
//        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:00");
//        return IntStream.rangeClosed(0, 23).mapToObj(hour -> {
//            ComprehensiveRevenueDTO.HourlyRevenueDTO dto = new ComprehensiveRevenueDTO.HourlyRevenueDTO();
//            dto.setHour(formatter.format(java.time.LocalTime.of(hour, 0)));
//            dto.setRevenue(BigDecimal.ZERO);
//            return dto;
//        }).collect(Collectors.toList());
//    }

//    -----------------------------------------------------------------------------------------

//    private void populateHourlyRevenue(List<ComprehensiveRevenueDTO.HourlyRevenueDTO> hourlyRevenue, List<Object[]> data) {
//        data.forEach(row -> {
//            int hour = (Integer) row[0];
//            BigDecimal revenue = (BigDecimal) row[1];
//            hourlyRevenue.get(hour).setRevenue(revenue);
//        });
//    }
//}


