package com.mini_project.miniproject.events.dto;

import lombok.Data;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Data
public class EventResponseDto {
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
    private OrganizerDTO organizer;
    private List<TicketTierDTO> ticketTiers;
    private List<EventVoucherDTO> eventVouchers;

    @Data
    public static class OrganizerDTO {
        private String id;
        private String firstName;
        private String lastName;
        private String email;
        private String avatar;
        private String quotes;
    }

    @Data
    public static class TicketTierDTO {
        private String name;
        private Double price;
        private Integer totalSeats;
    }

    @Data
    public static class EventVoucherDTO {
        private String code;
        private Double discountPercentage;
        private LocalDate startDate;
        private LocalDate endDate;
    }
}