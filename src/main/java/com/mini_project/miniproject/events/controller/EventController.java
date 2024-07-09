package com.mini_project.miniproject.events.controller;

//import com.mini_project.miniproject.auth.annotation.IsOrganizer;
import com.mini_project.miniproject.events.dto.CreateEventRequestDto;
import com.mini_project.miniproject.events.dto.EventDto;
import com.mini_project.miniproject.events.entity.Events;
import com.mini_project.miniproject.events.service.EventService;
import com.mini_project.miniproject.responses.Response;
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

    @PostMapping
//    @IsOrganizer
    public ResponseEntity<Response<Object>> createEvent(
            @RequestBody CreateEventRequestDto createEventRequestDto, Authentication authentication) {
        Events createdEvent = eventService.createEvent(createEventRequestDto, authentication);
        return Response.success("Event created successfully.", createdEvent);
    }

    @PostMapping(value = "/{eventId}/upload-picture", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
//    @IsOrganizer
    public ResponseEntity<Response<Object>> uploadEventPicture(
            @PathVariable Long eventId,
            @RequestParam("file") MultipartFile file,
            Authentication authentication) throws IOException {
        String imageUrl = eventService.uploadEventPicture(eventId, file, authentication);
        return Response.success("Image successfully uploaded",imageUrl);
    }

    @GetMapping("/{eventId}")
    public ResponseEntity<Response<Object>> getEventById (@PathVariable Long eventId) {
        EventDto event = eventService.getEventById(eventId);
        return Response.success("Event retrieved successfully", event);
    }
}
