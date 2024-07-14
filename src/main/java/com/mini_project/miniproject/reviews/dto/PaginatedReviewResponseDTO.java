package com.mini_project.miniproject.reviews.dto;

import lombok.Data;

import java.util.List;

@Data
public class PaginatedReviewResponseDTO {
    private List<ReviewResponseDTO> reviews;
    private int page;
    private int perPage;
    private int totalPages;
    private long totalReviews;
}
