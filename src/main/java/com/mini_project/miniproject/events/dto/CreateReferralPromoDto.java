package com.mini_project.miniproject.events.dto;

import lombok.Data;

@Data
public class CreateReferralPromoDto {
    private Double discountPercentage;
    private Integer quantity;
}
