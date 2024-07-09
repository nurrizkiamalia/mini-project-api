package com.mini_project.miniproject.events.dto;

import lombok.Data;
import java.util.List;

@Data
public class PaginatedEventResponseDto {
    private List<EventResponseDto> events;
    private int page;
    private int perPage;
    private int totalPages;
    private long totalEvents;
}
