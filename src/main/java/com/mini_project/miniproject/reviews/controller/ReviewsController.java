package com.mini_project.miniproject.reviews.controller;

import com.mini_project.miniproject.responses.Response;
import com.mini_project.miniproject.reviews.dto.CreateReviewDTO;
import com.mini_project.miniproject.reviews.dto.PaginatedReviewResponseDTO;
import com.mini_project.miniproject.reviews.dto.ReviewResponseDTO;
import com.mini_project.miniproject.reviews.dto.UpdateReviewDTO;
import com.mini_project.miniproject.reviews.service.ReviewService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/reviews")
public class ReviewsController {
    private final ReviewService reviewService;

    public ReviewsController(ReviewService reviewService){
        this.reviewService = reviewService;
    }

    @PostMapping
    public ResponseEntity<Response<Object>> createReview(@Valid @RequestBody CreateReviewDTO createReviewDTO, Authentication authentication){
        ReviewResponseDTO response = reviewService.createReview(createReviewDTO, authentication);
        return Response.success("Review successfully created.", response);
    }

    @PutMapping("/{reviewId}")
    public ResponseEntity<Response<Object>> updateReview(@PathVariable Long reviewId, @Valid @RequestBody UpdateReviewDTO updateReviewDTO, Authentication authentication){
        ReviewResponseDTO response = reviewService.updateReview(reviewId, updateReviewDTO, authentication);
        return Response.success("Review updated successfully.", response);
    }

    @DeleteMapping("/{reviewId}")
    public ResponseEntity<Response<Void>> deleteReview(@PathVariable Long reviewId, Authentication authentication){
        reviewService.deleteReview(reviewId, authentication);
        return Response.success("Review deleted successfully.");
    }

    @GetMapping("/{reviewId}")
    public ResponseEntity<Response<Object>> getReview(@PathVariable Long reviewId){
        ReviewResponseDTO response = reviewService.getReview(reviewId);
        return Response.success("Review retrieved successfully.", response);
    }

    @GetMapping("/event/{eventId}")
    public ResponseEntity<Response<Object>> getReviewsByEvent(
            @PathVariable Long eventId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "9") int size){
        PaginatedReviewResponseDTO response = reviewService.getReviewsByEvent(eventId, page, size);
        return Response.success("Reviews by event retrieved successfully.", response);
    }

//    @GetMapping("/user")
//    public ResponseEntity<Response<Object>> getReviewsByUser(
//            Authentication authentication,
//            @RequestParam(defaultValue = "0") int page,
//            @RequestParam(defaultValue = "9") int size){
//        PaginatedReviewResponseDTO response = reviewService.getReviewsByUser(authentication, page, size);
//        return Response.success("Reviews by user retrieved successfully", response);
//    }
}
