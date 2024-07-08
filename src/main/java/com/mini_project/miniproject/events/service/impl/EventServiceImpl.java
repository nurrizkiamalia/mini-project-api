package com.mini_project.miniproject.events.service.impl;

import com.mini_project.miniproject.events.dto.CreateEventRequestDto;
import com.mini_project.miniproject.events.entity.Events;
import com.mini_project.miniproject.events.mapper.CreateEventMapper;
import com.mini_project.miniproject.events.repository.EventRepository;
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

    public EventServiceImpl (
            EventRepository eventRepository,
            UserRepository userRepository,
            CreateEventMapper createEventMapper){
        this.eventRepository = eventRepository;
        this.userRepository = userRepository;
        this.createEventMapper = createEventMapper;
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
        return eventRepository.save(event);
    }


//
//    private final EventRepository eventRepository;
////    private final CloudinaryService cloudinaryService;
//
//    public EventServiceImpl(EventRepository eventRepository, CloudinaryService cloudinaryService) {
//        this.eventRepository = eventRepository;
////        this.cloudinaryService = cloudinaryService;
//    }
//
//    @Override
//    @Transactional
//    public Events createEvent(CreateEventRequestDto createEventRequestDto, Authentication authentication) {
//        Jwt jwt = (Jwt) authentication.getPrincipal();
//        Long organizerId = jwt.getClaim("userId");
//
//        Events event = new Events();
//        event.setOrganizerId(organizerId);
//        event.setName(createEventRequestDto.getName());
//        event.setDescription(createEventRequestDto.getDescription());
//        event.setDate(createEventRequestDto.getDate());
//        event.setTime(createEventRequestDto.getTime());
//        event.setLocation(createEventRequestDto.getLocation());
//        event.setCity(createEventRequestDto.getCity());
//        event.setEventType(createEventRequestDto.getEventType());
//        event.setCategory(createEventRequestDto.getCategory());
//
////        // Upload event picture to Cloudinary
////        if (createEventRequestDto.getEventPicture() != null) {
////            String imageUrl = cloudinaryService.uploadImage(createEventRequestDto.getEventPicture());
////            event.setEventPicture(imageUrl);
////        }
//
//        event.setTicketTiers(createEventRequestDto.getTicketTiers());
//        event.setEventVouchers(createEventRequestDto.getEventVouchers());
//        event.setReferralPromo(createEventRequestDto.getReferralPromo());
//
//        // Set bidirectional relationships
//        event.getTicketTiers().forEach(tier -> tier.setEvent(event));
//        event.getEventVouchers().forEach(voucher -> voucher.setEvent(event));
//        if (event.getReferralPromo() != null) {
//            event.getReferralPromo().setEvent(event);
//        }
//
//        return eventRepository.save(event);
//    }
}

