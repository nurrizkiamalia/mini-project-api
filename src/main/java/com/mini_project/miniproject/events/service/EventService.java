package com.mini_project.miniproject.events.service;

import com.mini_project.miniproject.events.dto.*;
import com.mini_project.miniproject.events.entity.Events;
import org.springframework.security.core.Authentication;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface EventService {
    Events createEvent(CreateEventRequestDto requestDto, Authentication authentication);
    String uploadEventPicture(Long eventId, MultipartFile file, Authentication authentication) throws IOException;
    EventResponseDto getEventById(Long eventId);
    PaginatedEventResponseDto searchEvents(String category, String city, String dates,
                                           String prices, String keyword, Long organizerId,
                                           int page, int size);
    UpdateEventResponseDto updateEvent(Long eventId, CreateEventRequestDto requestDto, Authentication authentication);

    void deleteEvent(Long eventId, Authentication authentication);

    // get event list (current organizer only)
//    PaginatedEventResponseForOrganizerDTO getEventsForOrganizer(Authentication authentication, int page, int size);

    // get event by id (current organizer only)
}
