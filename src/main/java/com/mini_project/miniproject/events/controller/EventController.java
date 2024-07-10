package com.mini_project.miniproject.events.controller;

//import com.mini_project.miniproject.auth.annotation.IsOrganizer;
import com.mini_project.miniproject.auth.annotation.IsOrganizer;
import com.mini_project.miniproject.events.dto.CreateEventRequestDto;
import com.mini_project.miniproject.events.dto.EventResponseDto;
import com.mini_project.miniproject.events.dto.PaginatedEventResponseDto;
import com.mini_project.miniproject.events.dto.UpdateEventResponseDto;
import com.mini_project.miniproject.events.entity.Events;
import com.mini_project.miniproject.events.service.EventService;
import com.mini_project.miniproject.responses.Response;
import jakarta.validation.Valid;
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
        EventResponseDto event = eventService.getEventById(eventId);
        return Response.success("Event retrieved successfully", event);
    }

    @GetMapping("/search")
    public ResponseEntity<PaginatedEventResponseDto> searchEvents(
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String city,
            @RequestParam(required = false) String dates,
            @RequestParam(required = false) String prices,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Long organizerId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "9") int size) {

        PaginatedEventResponseDto response = eventService.searchEvents(
                category, city, dates, prices, keyword, organizerId, page, size);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{eventId}")
    public ResponseEntity<Response<Object>> updateEvent(@PathVariable Long eventId,
                                                              @Valid @RequestBody CreateEventRequestDto requestDto,
                                                              Authentication authentication) {
        UpdateEventResponseDto updatedEvent = eventService.updateEvent(eventId, requestDto, authentication);
        return Response.success("Event updated successfully", updatedEvent);
    }

    @DeleteMapping("/{eventId}")
    public ResponseEntity<Response<Void>> deleteEvent(@PathVariable Long eventId, Authentication authentication) {
        eventService.deleteEvent(eventId, authentication);
        return Response.success("Event deleted successfully");
    }
}
