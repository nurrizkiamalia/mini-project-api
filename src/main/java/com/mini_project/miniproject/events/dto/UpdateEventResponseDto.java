package com.mini_project.miniproject.events.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Data
public class UpdateEventResponseDto {
    private String id;
    private String name;
    private String description;
    private LocalDate date;
    private LocalTime time;
    private String location;
    private String city;
    private String eventType;
    private String category;
    private Integer referralQuota;
    private String eventPicture;
    private List<TicketTierDTO> ticketTiers;
    private List<EventVoucherDTO> eventVouchers;

    @Data
    public static class TicketTierDTO {
        private String name;
        private BigDecimal price;
        private Integer totalSeats;
    }

    @Data
    public static class EventVoucherDTO {
        private String code;
        private BigDecimal discountPercentage;
        private LocalDate startDate;
        private LocalDate endDate;
    }
}
