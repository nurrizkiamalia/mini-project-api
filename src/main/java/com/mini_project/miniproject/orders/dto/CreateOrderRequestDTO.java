package com.mini_project.miniproject.orders.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

@Data
public class CreateOrderRequestDTO {
//    private Long customerId;
    @NotNull
    private Long eventId;
    @NotNull
    private List<TicketRequest> tickets;
    private Long eventVoucherId;    // optional
    private Integer points;         // optional, default = 0
    private Boolean useDisc10;      // optional, default = false


    @Data
    public static class TicketRequest {
        private Long ticketId;
        private Integer quantity;
    }
}
