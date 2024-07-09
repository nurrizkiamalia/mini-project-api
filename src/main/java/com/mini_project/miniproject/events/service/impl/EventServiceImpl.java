package com.mini_project.miniproject.events.service.impl;

import com.mini_project.miniproject.events.dto.CreateEventRequestDto;
import com.mini_project.miniproject.events.dto.EventResponseDto;
import com.mini_project.miniproject.events.dto.PaginatedEventResponseDto;
import com.mini_project.miniproject.events.entity.Events;
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
import java.time.LocalDate;
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
            throw new AccessDeniedException("Only users with ORGANIZER role can upload images for events");
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
    public EventResponseDto getEventById(Long eventId) {
        Events event = eventRepository.findById(eventId)
                .orElseThrow(() -> new RuntimeException("Event not found"));

        return convertToDto(event);

    }

    @Override
    public PaginatedEventResponseDto searchEvents(String category, String city, String dates, String prices, String keyword, Long organizerId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);

        Specification<Events> spec = (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

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
                        predicates.add(criteriaBuilder.between(root.get("date"), now, now.plusWeeks(1)));
                        break;
                    case "this month":
                        predicates.add(criteriaBuilder.between(root.get("date"), now, now.plusMonths(1)));
                        break;
                    case "this year":
                        predicates.add(criteriaBuilder.between(root.get("date"), now, now.plusYears(1)));
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

