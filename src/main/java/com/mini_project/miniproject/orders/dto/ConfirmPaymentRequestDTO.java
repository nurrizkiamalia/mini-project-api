package com.mini_project.miniproject.orders.dto;


import lombok.Data;

@Data
public class ConfirmPaymentRequestDTO {
    private Long orderId;
    private String paymentMethod;
}