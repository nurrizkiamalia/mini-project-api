package com.mini_project.miniproject.events.controller;

import com.mini_project.miniproject.auth.annotation.IsOrganizer;
import com.mini_project.miniproject.events.dto.CreateEventRequestDto;
import com.mini_project.miniproject.events.entity.Events;
import com.mini_project.miniproject.events.service.EventService;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/api/v1/events")
public class EventController {

    private final EventService eventService;

    public EventController(EventService eventService) {
        this.eventService = eventService;
    }

    @PostMapping //(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
//    @IsOrganizer
    public ResponseEntity<Events> createEvent(
            @RequestBody CreateEventRequestDto createEventRequestDto,
//            @RequestPart(value = "eventPicture", required = false) MultipartFile eventPicture,
            Authentication authentication) {
//
//        if (eventPicture != null) {
//            createEventRequestDto.setEventPicture(eventPicture);
//        }

        Events createdEvent = eventService.createEvent(createEventRequestDto, authentication);
        return ResponseEntity.ok(createdEvent);
    }
}
