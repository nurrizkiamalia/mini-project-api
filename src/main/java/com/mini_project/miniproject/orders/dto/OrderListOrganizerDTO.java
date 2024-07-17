package com.mini_project.miniproject.orders.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class OrderListOrganizerDTO {
    private BigDecimal totalAmount;
    private List<OrdersForOrganizerDTO> orders;

}
