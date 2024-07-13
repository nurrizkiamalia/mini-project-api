package com.mini_project.miniproject.orders.dto;

import lombok.Data;

import java.util.List;

@Data
public class CreateOrderRequestDTO {
    private Long customerId;
    private Long eventId;
    private List<TicketRequest> tickets;
    private Long eventVoucherId;
    private Integer points;
    private boolean useDisc10;

    @Data
    public static class TicketRequest {
        private Long ticketId;
        private Integer quantity;
    }
}
