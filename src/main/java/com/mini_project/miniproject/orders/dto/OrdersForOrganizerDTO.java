package com.mini_project.miniproject.orders.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class OrdersForOrganizerDTO {
    private Long orderId;
    private String invoice;
    private String eventName;
    private String paymentMethod;
    private Integer totalTickets;
    private BigDecimal totalPrice;
    private CustomerDetailsDTO customerDetails;

    @Data
    public static class CustomerDetailsDTO{
        private Long id;
        private String firstName;
        private String lastName;
        private String email;
    }
}
