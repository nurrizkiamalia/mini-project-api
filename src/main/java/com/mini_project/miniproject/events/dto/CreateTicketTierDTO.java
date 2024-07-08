package com.mini_project.miniproject.events.dto;

import lombok.Data;

@Data
public class CreateTicketTierDTO {
    private String name;
    private Double price;
    private Integer totalSeats;
}
