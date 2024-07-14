package com.mini_project.miniproject.reviews.dto;

import lombok.Data;

@Data
public class ReviewResponseDTO {
    private Long id;
    private Long userId;
    private Long eventId;
    private Long orderId;
    private Integer rating;
    private String reviewText;
}
