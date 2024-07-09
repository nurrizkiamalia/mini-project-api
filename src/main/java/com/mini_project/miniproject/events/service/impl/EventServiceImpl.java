package com.mini_project.miniproject.events.service.impl;

import com.mini_project.miniproject.events.dto.CreateEventRequestDto;
import com.mini_project.miniproject.events.dto.CreateTicketTierDto;
import com.mini_project.miniproject.events.dto.EventDto;
import com.mini_project.miniproject.events.entity.Events;
import com.mini_project.miniproject.events.entity.TicketTiers;
import com.mini_project.miniproject.events.mapper.CreateEventMapper;
import com.mini_project.miniproject.events.repository.EventRepository;
import com.mini_project.miniproject.events.repository.TicketTiersRepository;
import com.mini_project.miniproject.events.repository.EventVouchersRepository;

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
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;


@Service
public class EventServiceImpl implements EventService {

    private final EventRepository eventRepository;
    private final UserRepository userRepository;
    private final CreateEventMapper createEventMapper;
    private final TicketTiersRepository ticketTiersRepository;
    private final EventVouchersRepository eventVouchersRepository;

    private final CloudinaryService cloudinaryService;

    public EventServiceImpl (
            EventRepository eventRepository,
            UserRepository userRepository,
            CreateEventMapper createEventMapper,
            TicketTiersRepository ticketTiersRepository,
            EventVouchersRepository eventVouchersRepository,
            CloudinaryService cloudinaryService){
        this.eventRepository = eventRepository;
        this.userRepository = userRepository;
        this.createEventMapper = createEventMapper;
        this.ticketTiersRepository = ticketTiersRepository;
        this.eventVouchersRepository = eventVouchersRepository;
        this.cloudinaryService = cloudinaryService;
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


        return finalEvent;

    }

    @Override
    public String uploadEventPicture(Long eventId, MultipartFile file, Authentication authentication) throws IOException {
        Jwt jwt = (Jwt) authentication.getPrincipal();
        Long userId = jwt.getClaim("userId");
        String userRole = jwt.getClaim("role");

        // Check if the user has the ORGANIZER role
        if (!"ORGANIZER".equals(userRole)) {
            throw new AccessDeniedException("Only users with ORGANIZER role can create events");
        }

        Events event = eventRepository.findById(eventId)
                .orElseThrow(() -> new RuntimeException("Event not found"));

//        if (!event.getOrganizerId().equals(userId)) {
//            throw new RuntimeException("You are not authorized to upload a picture for this event");
//        }
        if (!event.getOrganizer().getId().equals(userId)) {
            throw new RuntimeException("You are not authorized to upload a picture for this event");
        }

        String imageUrl = cloudinaryService.uploadImage(file);
        event.setEventPicture(imageUrl);
        eventRepository.save(event);

        return imageUrl;
    }

    @Override
    public EventDto getEventById(Long eventId) {
//        return eventRepository.findById(eventId)
//                .orElseThrow(() -> new ApplicationException("Event not found with id: " + eventId));

        Events event = eventRepository.findById(eventId)
                .orElseThrow(() -> new RuntimeException("Event not found with id: " + eventId));
        return convertToDto(event);
    }

    private EventDto convertToDto(Events event) {
        EventDto dto = new EventDto();
        dto.setId(event.getId());
//        dto.setOrganizer(event.getOrganizer());
        dto.setName(event.getName());
        dto.setDescription(event.getDescription());
        dto.setDate(event.getDate());
        dto.setTime(event.getTime());
        dto.setLocation(event.getLocation());
        dto.setCity(event.getCity());
        dto.setCategory(event.getCategory());
        dto.setReferralQuota(event.getReferralQuota());
        dto.setEventPicture(event.getEventPicture());

        return dto;
//
//        if (event.getTicketTiers() != null) {
//            List<TicketTierDto> ticketTierDtos = event.getTicketTiers().stream()
//                    .map(this::convertToTicketTierDto)
//                    .collect(Collectors.toList());
//            dto.setTicketTiers(ticketTierDtos);
//        }
    }

//    private CreateTicketTierDto convertToTicketTierDto(CreateTicketTierDto ticketTiers) {
//        CreateTicketTierDto dto = new CreateTicketTierDto();
//        dto.set
//    }

}

