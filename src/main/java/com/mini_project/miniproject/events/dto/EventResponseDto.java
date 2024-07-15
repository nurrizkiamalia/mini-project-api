package com.mini_project.miniproject.events.dto;

//import com.mini_project.miniproject.reviews.dto.ReviewDetailsDTO;
import lombok.Data;

import java.math.BigDecimal;
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
//    private ReviewDetailsDTO reviews;

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
        private Long id;
        private String name;
        private BigDecimal price;
        private Integer totalSeats;
    }

    @Data
    public static class EventVoucherDTO {
        private Long id;
        private String code;
        private BigDecimal discountPercentage;
        private LocalDate startDate;
        private LocalDate endDate;
    }

}