package com.mini_project.miniproject.orders.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class OrderDetailsForOrganizerDTO {
    private Long orderId;
    private BigDecimal originalPrice;
    private BigDecimal totalPrice;
    private List<PurchasedTicketsDTO> tickets;
    private List<AppliedDiscountsDTO> appliedDiscounts;

    @Data
    public class AppliedDiscountsDTO{
        private String discountType;
        private BigDecimal discountAmount;
    }

    @Data
    public class PurchasedTicketsDTO{
        private String ticketName;
        private BigDecimal price;
        private Integer quantity;
    }
}
