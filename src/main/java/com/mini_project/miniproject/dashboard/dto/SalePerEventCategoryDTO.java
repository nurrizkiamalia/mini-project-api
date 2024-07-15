package com.mini_project.miniproject.dashboard.dto;

import lombok.Data;
import java.util.List;

@Data
public class SalePerEventCategoryDTO {
//    private int totalSeats;
    private int soldSeats;
    private List<SaleDetailDTO> saleDetails;

    @Data
    public static class SaleDetailDTO {
        private String eventCategory;
        private int totalTicketSold;
        private double revenuePercentage;
    }
}