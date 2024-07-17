package com.mini_project.miniproject.orders.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Data
public class OrderDetailsDTO {
    private Long id;
    private String invoice;
    private BigDecimal totalPrice;
    private Integer totalTickets;
    private List<TicketDetailsDTO> ticketDetails;
    private EventDetailsDTO eventDetail;

    @Data
    public class TicketDetailsDTO {
        private String ticketTier;
        private Integer quantity;
    }

    @Data
    public class EventDetailsDTO {
        private Long id;
        private String name;
        private String category;
        private LocalDate date;
        private LocalTime time;
        private String location;
        private String city;
        private String eventPicture;
    }

}
