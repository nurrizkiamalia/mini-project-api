package com.mini_project.miniproject.reviews.dto;

import lombok.Data;

import java.util.List;

@Data
public class PaginatedReviewResponseDTO {
    private List<ReviewDetailsDTO> reviews;
    private int page;
    private int perPage;
    private int totalPages;
    private long totalReviews;
}
