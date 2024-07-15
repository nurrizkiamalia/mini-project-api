package com.mini_project.miniproject.dashboard.dto;

import lombok.Data;
import java.math.BigDecimal;
import java.util.List;

@Data
public class RevenueByEventDTO {
    private BigDecimal totalRevenue;
    private List<EventRevenueDTO> events;

    @Data
    public static class EventRevenueDTO {
        private String name;
        private BigDecimal revenue;
        private BigDecimal percentage;
    }
}