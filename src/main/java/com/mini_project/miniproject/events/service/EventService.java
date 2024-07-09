package com.mini_project.miniproject.events.service;

import com.mini_project.miniproject.events.dto.CreateEventRequestDto;
import com.mini_project.miniproject.events.dto.EventDto;
import com.mini_project.miniproject.events.entity.Events;
import org.springframework.security.core.Authentication;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface EventService {
    Events createEvent(CreateEventRequestDto requestDto, Authentication authentication);
    String uploadEventPicture(Long eventId, MultipartFile file, Authentication authentication) throws IOException;
    EventDto getEventById(Long eventId);
//    Events createEvent(CreateEventRequestDto requestDto, Authentication authentication) throws IOException;
    // 2. get all events based on query parameters (category, city, date, price, search, organizerId), apply pagination (all users)
    // 2. get all events based on query parameters (category, city, date, price, search), apply pagination (all users)

    // 3. get an event by its id (all users)
    // 4. update an event by its id (ORGANIZER only)
    // 5. delete an event by its id (ORGANIZER only)
}
