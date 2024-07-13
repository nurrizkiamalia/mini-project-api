package com.mini_project.miniproject.events.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class CreateTicketTierDto {
    private String name;
    private BigDecimal price;
    private Integer totalSeats;
}
