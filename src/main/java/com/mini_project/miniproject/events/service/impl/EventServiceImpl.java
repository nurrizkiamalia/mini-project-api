package com.mini_project.miniproject.events.service.impl;

import com.mini_project.miniproject.events.dto.*;
import com.mini_project.miniproject.events.entity.EventVouchers;
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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import jakarta.persistence.criteria.Predicate;

import java.math.BigDecimal;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import java.io.IOException;


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

    @Override
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
    @Transactional
    public String uploadEventPicture(Long eventId, MultipartFile file, Authentication authentication) throws IOException {
        Jwt jwt = (Jwt) authentication.getPrincipal();
        Long userId = jwt.getClaim("userId");
        String userRole = jwt.getClaim("role");

        // Check if the user has the ORGANIZER role
        if (!"ORGANIZER".equals(userRole)) {
            throw new AccessDeniedException("Only users with ORGANIZER role can upload images for events");
        }

        Events event = eventRepository.findById(eventId)
                .orElseThrow(() -> new ApplicationException("Event not found"));

        if (!event.getOrganizer().getId().equals(userId)) {
            throw new ApplicationException("You are not authorized to upload a picture for this event");
        }

        String imageUrl = cloudinaryService.uploadImage(file);
        event.setEventPicture(imageUrl);
        eventRepository.save(event);

        return imageUrl;
    }

    @Override
    public EventResponseDto getEventById(Long eventId) {
        Events event = eventRepository.findById(eventId)
                .orElseThrow(() -> new RuntimeException("Event not found"));

        return convertToDto(event);

    }

    @Override
    public PaginatedEventResponseDto searchEvents(String category, String city, String dates, String prices, String keyword, Long organizerId, int page, int size) {
//        Pageable pageable = PageRequest.of(page, size);
        Pageable pageable = PageRequest.of(page, size, Sort.by("date").ascending());

        Specification<Events> spec = (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            // Add a predicate to filter out past events
            LocalDate today = LocalDate.now();
            predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("date"), today));

            if (category != null && !category.isEmpty()) {
                predicates.add(criteriaBuilder.equal(
                        criteriaBuilder.lower(root.get("category")),
                        category.toLowerCase()
                ));
            }

            if (city != null && !city.isEmpty()) {
                predicates.add(criteriaBuilder.equal(
                        criteriaBuilder.lower(root.get("city")),
                        city.toLowerCase()
                ));
            }

            if (dates != null) {
                LocalDate now = LocalDate.now();
                switch (dates.toLowerCase()) {
                    case "this week":
                        LocalDate startOfWeek = now.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
                        LocalDate endOfWeek = startOfWeek.plusDays(6);
                        predicates.add(criteriaBuilder.between(root.get("date"), startOfWeek, endOfWeek));
                        break;
                    case "this month":
                        LocalDate startOfMonth = now.withDayOfMonth(1);
                        LocalDate endOfMonth = now.with(TemporalAdjusters.lastDayOfMonth());
                        predicates.add(criteriaBuilder.between(root.get("date"), startOfMonth, endOfMonth));
                        break;
                    case "this year":
                        LocalDate startOfYear = now.withDayOfYear(1);
                        LocalDate endOfYear = now.with(TemporalAdjusters.lastDayOfYear());
                        predicates.add(criteriaBuilder.between(root.get("date"), startOfYear, endOfYear));
                        break;
                    // "all" is default, so we don't need to add a predicate for it
                }
            }

            if (prices != null) {
                switch (prices.toLowerCase()) {
                    case "free":
                        predicates.add(criteriaBuilder.equal(
                                criteriaBuilder.lower(root.get("eventType")),
                                "free"
                        ));
                        break;
                    case "paid":
                        predicates.add(criteriaBuilder.equal(
                                criteriaBuilder.lower(root.get("eventType")),
                                "paid"
                        ));
                        break;
                    // "all" is default, so we don't need to add a predicate for it
                }
            }

            if (keyword != null && !keyword.isEmpty()) {
                String lowercaseKeyword = "%" + keyword.toLowerCase() + "%";
                predicates.add(criteriaBuilder.or(
                        criteriaBuilder.like(criteriaBuilder.lower(root.get("name")), lowercaseKeyword),
                        criteriaBuilder.like(criteriaBuilder.lower(root.get("description")), lowercaseKeyword)
                ));
            }

            if (organizerId != null) {
                predicates.add(criteriaBuilder.equal(root.get("organizer").get("id"), organizerId));
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };

        Page<Events> eventsPage = eventRepository.findAll(spec, pageable);

        PaginatedEventResponseDto response = new PaginatedEventResponseDto();
        response.setEvents(eventsPage.getContent().stream()
                .map(this::convertToDto)
                .collect(Collectors.toList()));
        response.setPage(page);
        response.setPerPage(size);
        response.setTotalPages(eventsPage.getTotalPages());
        response.setTotalEvents(eventsPage.getTotalElements());

        return response;
    }

    @Override
    @Transactional
    public UpdateEventResponseDto updateEvent(Long eventId, CreateEventRequestDto requestDto, Authentication authentication) {
        Jwt jwt = (Jwt) authentication.getPrincipal();
        Long userId = jwt.getClaim("userId");
        String userRole = jwt.getClaim("role");

        // Check if the user has the ORGANIZER role
        if (!"ORGANIZER".equals(userRole)) {
            throw new AccessDeniedException("Only users with ORGANIZER role can update events");
        }

        Events event = eventRepository.findById(eventId)
                .orElseThrow(() -> new ApplicationException("Event not found"));

        if (!event.getOrganizer().getId().equals(userId)) {
            throw new ApplicationException("You are not authorized to update this event");
        }

        // Update event details
        event.setName(requestDto.getName());
        event.setDescription(requestDto.getDescription());
        event.setDate(requestDto.getDate());
        event.setTime(requestDto.getTime());
        event.setLocation(requestDto.getLocation());
        event.setCity(requestDto.getCity());
        event.setEventType(requestDto.getEventType());
        event.setCategory(requestDto.getCategory());
        event.setReferralQuota(requestDto.getReferralQuota());

        // Update ticket tiers
        event.getTicketTiers().clear();
        for (CreateTicketTierDto tierDto : requestDto.getTicketTiers()) {
            TicketTiers tier = new TicketTiers();
            tier.setName(tierDto.getName());
            tier.setPrice(tierDto.getPrice());
            tier.setTotalSeats(tierDto.getTotalSeats());
            tier.setEvent(event);
            event.getTicketTiers().add(tier);
        }

        // Update event vouchers
        event.getEventVouchers().clear();
        if (requestDto.getEventVouchers() != null) {
            for (CreateEventVoucherDto voucherDto : requestDto.getEventVouchers()) {
                EventVouchers voucher = new EventVouchers();
                voucher.setCode(voucherDto.getCode());
                voucher.setDiscountPercentage(voucherDto.getDiscountPercentage());
                voucher.setStartDate(LocalDate.parse(voucherDto.getStartDate()));
                voucher.setEndDate(LocalDate.parse(voucherDto.getEndDate()));
                voucher.setEvent(event);
                event.getEventVouchers().add(voucher);
            }
        }

        // Save the updated event
        Events updatedEvent = eventRepository.save(event);

        // Convert to UpdateEventResponseDto
        UpdateEventResponseDto responseDto = new UpdateEventResponseDto();
        responseDto.setId(updatedEvent.getId().toString());
        responseDto.setName(updatedEvent.getName());
        responseDto.setDescription(updatedEvent.getDescription());
        responseDto.setDate(updatedEvent.getDate());
        responseDto.setTime(updatedEvent.getTime());
        responseDto.setLocation(updatedEvent.getLocation());
        responseDto.setCity(updatedEvent.getCity());
        responseDto.setEventType(updatedEvent.getEventType());
        responseDto.setCategory(updatedEvent.getCategory());
        responseDto.setReferralQuota(updatedEvent.getReferralQuota());
        responseDto.setEventPicture(updatedEvent.getEventPicture());

        // Set ticket tiers in response
        List<UpdateEventResponseDto.TicketTierDTO> ticketTierDTOs = updatedEvent.getTicketTiers().stream()
                .map(tier -> {
                    UpdateEventResponseDto.TicketTierDTO dto = new UpdateEventResponseDto.TicketTierDTO();
                    dto.setName(tier.getName());
                    dto.setPrice(tier.getPrice());
                    dto.setTotalSeats(tier.getTotalSeats());
                    return dto;
                })
                .collect(Collectors.toList());
        responseDto.setTicketTiers(ticketTierDTOs);

        // Set event vouchers in response
        List<UpdateEventResponseDto.EventVoucherDTO> voucherDTOs = updatedEvent.getEventVouchers().stream()
                .map(voucher -> {
                    UpdateEventResponseDto.EventVoucherDTO dto = new UpdateEventResponseDto.EventVoucherDTO();
                    dto.setCode(voucher.getCode());
                    dto.setDiscountPercentage(voucher.getDiscountPercentage());
                    dto.setStartDate(voucher.getStartDate());
                    dto.setEndDate(voucher.getEndDate());
                    return dto;
                })
                .collect(Collectors.toList());
        responseDto.setEventVouchers(voucherDTOs);

        return responseDto;

    }

    @Override
    public void deleteEvent(Long eventId, Authentication authentication) {
        Jwt jwt = (Jwt) authentication.getPrincipal();
        Long userId = jwt.getClaim("userId");
        String userRole = jwt.getClaim("role");

        // Check if the user has the ORGANIZER role
        if (!"ORGANIZER".equals(userRole)) {
            throw new AccessDeniedException("Only users with ORGANIZER role can delete events");
        }

        Events event = eventRepository.findById(eventId)
                .orElseThrow(() -> new ApplicationException("Event not found"));

        if (!event.getOrganizer().getId().equals(userId)) {
            throw new ApplicationException("You are not authorized to delete this event");
        }

        eventRepository.delete(event);
    }

    @Override
    @Transactional
    public String updateEventPicture(Long eventId, MultipartFile file, Authentication authentication) throws IOException {
        Jwt jwt = (Jwt) authentication.getPrincipal();
        Long userId = jwt.getClaim("userId");
        String userRole = jwt.getClaim("role");

        // Check if the user has the ORGANIZER role
        if (!"ORGANIZER".equals(userRole)) {
            throw new AccessDeniedException("Only users with ORGANIZER role can upload images for events");
        }

        Events event = eventRepository.findById(eventId)
                .orElseThrow(() -> new ApplicationException("Event not found"));

        if (!event.getOrganizer().getId().equals(userId)) {
            throw new ApplicationException("You are not authorized to update the picture of this event");
        }

        String imageUrl = cloudinaryService.uploadImage(file);
        event.setEventPicture(imageUrl);
        eventRepository.save(event);

        return imageUrl;
    }

//    @Override
//    public PaginatedEventResponseForOrganizerDTO eventListForOrganizer(Authentication authentication, int page, int size) {
//        // Get userId and userRole from authentication
//        Jwt jwt = (Jwt) authentication.getPrincipal();
//        Long userId = jwt.getClaim("userId");
//        String userRole = jwt.getClaim("role");
//
//        // Check if the user is an organizer
//        if (!"ORGANIZER".equals(userRole)) {
//            throw new ApplicationException("Only organizers can access this feature");
//        }
//
//
//        // Get list of events created by the current organizer
//        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
//        Page<Events> eventsPage = eventRepository.findByOrganizerId(userId, pageable);
//
//        // Convert Events to EventResponseForOrganizerDTO
//        List<EventResponseForOrganizerDTO> eventDTOs = eventsPage.getContent().stream()
//                .map(this::convertToEventResponseForOrganizerDTO)
//                .collect(Collectors.toList());
//
//        // Create and populate PaginatedEventResponseForOrganizerDTO
//        PaginatedEventResponseForOrganizerDTO response = new PaginatedEventResponseForOrganizerDTO();
//        response.setEvents(eventDTOs);
//        response.setPage(page);
//        response.setPerPage(size);
//        response.setTotalPages(eventsPage.getTotalPages());
//        response.setTotalEvents(eventsPage.getTotalElements());
//
//        return response;
//    }

    @Override
    public List<EventResponseForOrganizerDTO> eventListForOrganizer(Authentication authentication){
        // Get userId and userRole from authentication
        Jwt jwt = (Jwt) authentication.getPrincipal();
        Long userId = jwt.getClaim("userId");
        String userRole = jwt.getClaim("role");

        // Check if the user is an organizer
        if (!"ORGANIZER".equals(userRole)) {
            throw new ApplicationException("Only organizers can access this feature");
        }

        // Get list of events created by the current organizer
        List<Events> eventsList = eventRepository.findByOrganizerId(userId);

        // Convert Events to EventResponseForOrganizerDTO
        List<EventResponseForOrganizerDTO> eventDTOs = eventsList.stream()
                .map(this::convertToEventResponseForOrganizerDTO)
                .collect(Collectors.toList());

        return eventDTOs;

    }

    private EventResponseForOrganizerDTO convertToEventResponseForOrganizerDTO(Events event) {
        EventResponseForOrganizerDTO dto = new EventResponseForOrganizerDTO();
        dto.setId(event.getId().toString());
        dto.setEventPicture(event.getEventPicture());
        dto.setName(event.getName());
        dto.setCategory(event.getCategory());

        // Convert all ticket tiers to TicketsDTO
        List<EventResponseForOrganizerDTO.TicketsDTO> ticketsDTOList = event.getTicketTiers().stream()
                .map(this::convertToTicketsDTO)
                .collect(Collectors.toList());
        dto.setTicketTiers(ticketsDTOList);

        return dto;
    }

    private EventResponseForOrganizerDTO.TicketsDTO convertToTicketsDTO(TicketTiers ticketTier) {
        EventResponseForOrganizerDTO.TicketsDTO ticketsDTO = new EventResponseForOrganizerDTO.TicketsDTO();
        ticketsDTO.setId(ticketTier.getId());
        ticketsDTO.setName(ticketTier.getName());
        ticketsDTO.setPrice(ticketTier.getPrice());
        ticketsDTO.setTotalSeats(ticketTier.getTotalSeats());
        return ticketsDTO;
    }

    private EventResponseDto convertToDto(Events event) {
        EventResponseDto dto = new EventResponseDto();
        dto.setId(event.getId().toString());
        dto.setName(event.getName());
        dto.setDescription(event.getDescription());
        dto.setDate(event.getDate());
        dto.setTime(event.getTime());
        dto.setLocation(event.getLocation());
        dto.setCity(event.getCity());
        dto.setEventType(event.getEventType());
        dto.setCategory(event.getCategory());
        dto.setReferralQuota(event.getReferralQuota());
        dto.setEventPicture(event.getEventPicture());

        // Set organizer
        EventResponseDto.OrganizerDTO organizerDTO = new EventResponseDto.OrganizerDTO();
        organizerDTO.setId(event.getOrganizer().getId().toString());
        organizerDTO.setFirstName(event.getOrganizer().getFirstName());
        organizerDTO.setLastName(event.getOrganizer().getLastName());
        organizerDTO.setEmail(event.getOrganizer().getEmail());
        organizerDTO.setAvatar(event.getOrganizer().getAvatar());
        organizerDTO.setQuotes(event.getOrganizer().getQuotes());
        dto.setOrganizer(organizerDTO);

        // Set ticket tiers
        dto.setTicketTiers(event.getTicketTiers().stream()
                .map(tier -> {
                    EventResponseDto.TicketTierDTO tierDTO = new EventResponseDto.TicketTierDTO();
                    tierDTO.setId(tier.getId());
                    tierDTO.setName(tier.getName());
                    tierDTO.setPrice(tier.getPrice());
                    tierDTO.setTotalSeats(tier.getTotalSeats());
                    return tierDTO;
                })
                .collect(Collectors.toList()));

        // Set event vouchers
        dto.setEventVouchers(event.getEventVouchers().stream()
                .map(voucher -> {
                    EventResponseDto.EventVoucherDTO voucherDTO = new EventResponseDto.EventVoucherDTO();
                    voucherDTO.setId(voucher.getId());
                    voucherDTO.setCode(voucher.getCode());
                    voucherDTO.setDiscountPercentage(voucher.getDiscountPercentage());
                    voucherDTO.setStartDate(voucher.getStartDate());
                    voucherDTO.setEndDate(voucher.getEndDate());
                    return voucherDTO;
                })
                .collect(Collectors.toList()));

        return dto;
    }


}

