package com.mini_project.miniproject.events.dto;

import lombok.Data;

@Data
public class CreateEventVoucherDto {
    private String code;
    private Double discountPercentage;
    private String startDate;
    private String endDate;
}
