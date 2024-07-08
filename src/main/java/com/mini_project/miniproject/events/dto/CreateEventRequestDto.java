package com.mini_project.miniproject.events.dto;

import com.mini_project.miniproject.events.entity.EventVouchers;
import com.mini_project.miniproject.events.entity.ReferralPromo;
import com.mini_project.miniproject.events.entity.TicketTiers;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

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
//    private MultipartFile eventPicture;
//    private List<TicketTiers> ticketTiers;
//    private List<EventVouchers> eventVouchers;
//    private ReferralPromo referralPromo;
    private List<CreateTicketTierDTO> ticketTiers;
    private List<CreateEventVoucherDTO> eventVouchers;
    private CreateReferralPromoDTO referralPromo;
}
