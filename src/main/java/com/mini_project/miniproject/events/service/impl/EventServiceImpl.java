package com.mini_project.miniproject.events.service.impl;

import com.mini_project.miniproject.events.dto.CreateEventRequestDto;
import com.mini_project.miniproject.events.entity.Events;
import com.mini_project.miniproject.events.mapper.CreateEventMapper;
import com.mini_project.miniproject.events.repository.EventRepository;
import com.mini_project.miniproject.events.repository.TicketTiersRepository;
import com.mini_project.miniproject.events.repository.EventVouchersRepository;
import com.mini_project.miniproject.events.repository.ReferralPromoRepository;

import com.mini_project.miniproject.events.service.EventService;
import com.mini_project.miniproject.exceptions.ApplicationException;
import com.mini_project.miniproject.helpers.CloudinaryService;
import com.mini_project.miniproject.user.entity.Users;
import com.mini_project.miniproject.user.repository.UserRepository;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

//import java.io.IOException;

@Service
public class EventServiceImpl implements EventService {

    private final EventRepository eventRepository;
    private final UserRepository userRepository;
    private final CreateEventMapper createEventMapper;
    private final TicketTiersRepository ticketTiersRepository;
    private final EventVouchersRepository eventVouchersRepository;
    private final ReferralPromoRepository referralPromoRepository;


    public EventServiceImpl (
            EventRepository eventRepository,
            UserRepository userRepository,
            CreateEventMapper createEventMapper,
            TicketTiersRepository ticketTiersRepository,
            EventVouchersRepository eventVouchersRepository,
            ReferralPromoRepository referralPromoRepository){
        this.eventRepository = eventRepository;
        this.userRepository = userRepository;
        this.createEventMapper = createEventMapper;
        this.ticketTiersRepository = ticketTiersRepository;
        this.eventVouchersRepository = eventVouchersRepository;
        this.referralPromoRepository = referralPromoRepository;
    }

    @Transactional
    public Events createEvent(CreateEventRequestDto createEventDTO, Authentication authentication) {
        Jwt jwt = (Jwt) authentication.getPrincipal();
        Long userId = jwt.getClaim("userId");
        String userRole = jwt.getClaim("role");

        // Check if the user has the ORGANIZER role
        if (!"ORGANIZER".equals(userRole)) {
            throw new AccessDeniedException("Only users with ORGANIZER role can create events");
        }

        Users organizer = userRepository.findById(userId)
                .orElseThrow(() -> new ApplicationException("Organizer not found"));

        Events event = createEventMapper.toEntity(createEventDTO, organizer);
        event = eventRepository.save(event);

        final Events finalEvent = event;

        // Save ticket tiers
        if (finalEvent.getTicketTiers() != null) {
            finalEvent.getTicketTiers().forEach(tier -> {
                tier.setEvent(finalEvent);
                ticketTiersRepository.save(tier);
            });
        }

        // Save event vouchers
        if (finalEvent.getEventVouchers() != null) {
            finalEvent.getEventVouchers().forEach(voucher -> {
                voucher.setEvent(finalEvent);
                eventVouchersRepository.save(voucher);
            });
        }

        // Save referral promo
        if (finalEvent.getReferralPromo() != null) {
            finalEvent.getReferralPromo().setEvent(finalEvent);
            referralPromoRepository.save(finalEvent.getReferralPromo());
        }

        return finalEvent;

    }

}

