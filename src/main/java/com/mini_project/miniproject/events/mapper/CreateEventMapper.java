package com.mini_project.miniproject.events.mapper;

import com.mini_project.miniproject.events.entity.EventVouchers;
import com.mini_project.miniproject.events.entity.TicketTiers;
import com.mini_project.miniproject.user.entity.Users;
import org.springframework.stereotype.Component;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;

import com.mini_project.miniproject.events.entity.Events;
import com.mini_project.miniproject.events.dto.CreateEventRequestDto;


@Component
public class CreateEventMapper {

    public Events toEntity(CreateEventRequestDto dto, Users organizer) {
        Events event = new Events();
        event.setName(dto.getName());
        event.setDescription(dto.getDescription());
        event.setDate(parseDate(dto.getDate()));
        event.setTime(parseTime(dto.getTime()));
        event.setLocation(dto.getLocation());
        event.setCity(dto.getCity());
        event.setEventType(dto.getEventType());
        event.setCategory(dto.getCategory());
        event.setReferralQuota(dto.getReferralQuota());
//        event.setOrganizerId(organizer.getId());
        event.setOrganizer(organizer);

        event.setTicketTiers(new ArrayList<>());
        event.setEventVouchers(new ArrayList<>());

        if (dto.getTicketTiers() != null) {
            dto.getTicketTiers().forEach(tierDto -> {
                TicketTiers tier = new TicketTiers();
                tier.setName(tierDto.getName());
                tier.setPrice(tierDto.getPrice());
                tier.setTotalSeats(tierDto.getTotalSeats());
                tier.setEvent(event);
                event.getTicketTiers().add(tier);
            });
        }

        if (dto.getEventVouchers() != null) {
            dto.getEventVouchers().forEach(voucherDto -> {
                EventVouchers voucher = new EventVouchers();
                voucher.setCode(voucherDto.getCode());
                voucher.setDiscountPercentage(voucherDto.getDiscountPercentage());
                voucher.setStartDate(LocalDate.parse(voucherDto.getStartDate()));
                voucher.setEndDate(LocalDate.parse(voucherDto.getEndDate()));
                voucher.setEvent(event);
                event.getEventVouchers().add(voucher);
            });
        }

        return event;
    }

        private LocalDate parseDate(Object date) {
        if (date instanceof LocalDate) {
            return (LocalDate) date;
        } else if (date instanceof String) {
            return LocalDate.parse((String) date);
        } else {
            throw new IllegalArgumentException("Unsupported date format");
        }
    }

    private LocalTime parseTime(Object time) {
        if (time instanceof LocalTime) {
            return (LocalTime) time;
        } else if (time instanceof String) {
            return LocalTime.parse((String) time);
        } else {
            throw new IllegalArgumentException("Unsupported time format");
        }
    }
}



//@Component
//public class CreateEventMapper {
//
//    public Events toEntity(CreateEventRequestDto dto, Users organizer) {
//        Events event = new Events();
//        event.setName(dto.getName());
//        event.setDescription(dto.getDescription());
//        event.setDate(parseDate(dto.getDate()));
//        event.setTime(parseTime(dto.getTime()));
//        event.setLocation(dto.getLocation());
//        event.setCity(dto.getCity());
//        event.setEventType(dto.getEventType());
//        event.setCategory(dto.getCategory());
//        event.setOrganizerId(organizer.getId());
//
//        // Initialize collections
//        event.setTicketTiers(new ArrayList<>());
//        event.setEventVouchers(new ArrayList<>());
//
//        // Map ticket tiers
//        if (dto.getTicketTiers() != null) {
//            dto.getTicketTiers().forEach(tierDto -> {
//                TicketTiers tier = new TicketTiers();
//                tier.setName(tierDto.getName());
//                tier.setPrice(tierDto.getPrice());
//                tier.setTotalSeats(tierDto.getTotalSeats());
//                tier.setEvent(event);
//                event.getTicketTiers().add(tier);
//            });
//        }
//
//        // Map event vouchers
//        if (dto.getEventVouchers() != null) {
//            dto.getEventVouchers().forEach(voucherDto -> {
//                EventVouchers voucher = new EventVouchers();
//                voucher.setCode(voucherDto.getCode());
//                voucher.setDiscountPercentage(voucherDto.getDiscountPercentage());
//                voucher.setStartDate(LocalDate.parse(voucherDto.getStartDate()));
//                voucher.setEndDate(LocalDate.parse(voucherDto.getEndDate()));
//                voucher.setEvent(event);
//                event.getEventVouchers().add(voucher);
//            });
//        }
//
//        // Map referral promo
//        if (dto.getReferralPromo() != null) {
//            ReferralPromo promo = new ReferralPromo();
//            promo.setDiscountPercentage(dto.getReferralPromo().getDiscountPercentage());
//            promo.setQuantity(dto.getReferralPromo().getQuantity());
//            promo.setEvent(event);
//            event.setReferralPromo(promo);
//        }
//
//        return event;
//    }
//
//    private LocalDate parseDate(Object date) {
//        if (date instanceof LocalDate) {
//            return (LocalDate) date;
//        } else if (date instanceof String) {
//            return LocalDate.parse((String) date);
//        } else {
//            throw new IllegalArgumentException("Unsupported date format");
//        }
//    }
//
//    private LocalTime parseTime(Object time) {
//        if (time instanceof LocalTime) {
//            return (LocalTime) time;
//        } else if (time instanceof String) {
//            return LocalTime.parse((String) time);
//        } else {
//            throw new IllegalArgumentException("Unsupported time format");
//        }
//    }
//}
