package com.mini_project.miniproject.events.dto;

import lombok.Data;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Data
public class CreateEventRequestDto {
    private String name;
    private String description;
    private LocalDate date;
    private LocalTime time;
    private String location;
    private String city;
    private String eventType;
    private String category;
    private Integer referralQuota;
    private List<CreateTicketTierDto> ticketTiers;
    private List<CreateEventVoucherDto> eventVouchers;
}
