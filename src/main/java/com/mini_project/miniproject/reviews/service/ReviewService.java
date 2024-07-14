package com.mini_project.miniproject.reviews.service;

import com.mini_project.miniproject.reviews.dto.CreateReviewDTO;
import com.mini_project.miniproject.reviews.dto.PaginatedReviewResponseDTO;
import com.mini_project.miniproject.reviews.dto.ReviewResponseDTO;
import com.mini_project.miniproject.reviews.dto.UpdateReviewDTO;
import org.springframework.security.core.Authentication;

import java.util.List;

public interface ReviewService {
    ReviewResponseDTO createReview(CreateReviewDTO createReviewDTO, Authentication authentication);
    ReviewResponseDTO updateReview(Long reviewId, UpdateReviewDTO updateReviewDTO, Authentication authentication);
    void deleteReview(Long reviewId, Authentication authentication);
    ReviewResponseDTO getReview(Long reviewId);
    PaginatedReviewResponseDTO getReviewsByEvent(Long eventId, int page, int size);
//    PaginatedReviewResponseDTO getReviewsByUser(Authentication authentication, int page, int size);

}
