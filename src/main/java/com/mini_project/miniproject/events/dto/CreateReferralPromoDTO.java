package com.mini_project.miniproject.events.dto;

import lombok.Data;

@Data
public class CreateReferralPromoDTO {
    private Double discountPercentage;
    private Integer quantity;
}
