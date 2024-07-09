package com.mini_project.miniproject.events.dto;

import com.mini_project.miniproject.user.entity.Users;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Data
public class EventDto {
    private Long id;
//    private Users organizer;
    private String name;
    private String description;
    private LocalDate date;
    private LocalTime time;
    private String location;
    private String city;
    private String eventType;
    private String category;
    private Integer referralQuota;
    private String eventPicture;
//    private List<CreateTicketTierDto> ticketTiers;
//    private List<CreateEventVoucherDto> eventVouchers;
}
