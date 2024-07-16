package com.mini_project.miniproject.events.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class EventResponseForOrganizerDTO {
    private String id;
    private String eventPicture;
    private String name;
    private String category;
//    private List<TicketsDTO> tickets;
    private List<TicketsDTO> ticketTiers;

    @Data
    public static class TicketsDTO{
        private Long id;
        private String name;
        private BigDecimal price;
        private Integer totalSeats;
    }

}
