package com.mini_project.miniproject.orders.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class CreateOrderResponseDTO {
    private Long orderId;
    private BigDecimal originalPrice;
    private BigDecimal finalPrice;
    private List<OrderDiscountsDTO> appliedDiscounts;

    @Data
    public class OrderDiscountsDTO{
        private String discountType;
        private BigDecimal discountAmount;
    }
}