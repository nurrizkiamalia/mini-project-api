package com.mini_project.miniproject.events.service;

import com.mini_project.miniproject.events.dto.CreateEventRequestDto;
import com.mini_project.miniproject.events.entity.Events;
import org.springframework.security.core.Authentication;

import java.io.IOException;

public interface EventService {
    // 1. create event (ORGANIZER only)
    Events createEvent(CreateEventRequestDto requestDto, Authentication authentication);

//    Events createEvent(CreateEventRequestDto requestDto, Authentication authentication) throws IOException;
    // 2. get all events based on query parameters (category, city, date, price, search, organizerId), apply pagination (all users)
    // 3. get an event by its id (all users)
    // 4. update an event by its id (ORGANIZER only)
    // 5. delete an event by its id (ORGANIZER only)
}
