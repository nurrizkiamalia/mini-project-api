package com.mini_project.miniproject.events.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class CreateEventVoucherDto {
    private String code;
    private BigDecimal discountPercentage;
    private String startDate;
    private String endDate;
}
