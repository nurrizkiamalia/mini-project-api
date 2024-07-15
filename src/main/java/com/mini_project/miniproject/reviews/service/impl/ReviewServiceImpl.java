package com.mini_project.miniproject.reviews.service.impl;

import com.mini_project.miniproject.exceptions.ApplicationException;
import com.mini_project.miniproject.orders.entity.Orders;
import com.mini_project.miniproject.orders.repository.OrderRepository;
import com.mini_project.miniproject.reviews.dto.*;
import com.mini_project.miniproject.reviews.entity.Reviews;
import com.mini_project.miniproject.reviews.repository.ReviewsRepository;
import com.mini_project.miniproject.reviews.service.ReviewService;
import com.mini_project.miniproject.user.entity.Users;
import com.mini_project.miniproject.user.repository.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ReviewServiceImpl implements ReviewService {
    private final ReviewsRepository reviewsRepository;
    private final OrderRepository orderRepository;
    private final UserRepository userRepository;
    public ReviewServiceImpl(ReviewsRepository reviewsRepository, OrderRepository orderRepository, UserRepository userRepository){
        this.reviewsRepository = reviewsRepository;
        this.orderRepository = orderRepository;
        this.userRepository = userRepository;

    }
    @Override
    @Transactional
    public ReviewResponseDTO createReview(CreateReviewDTO createReviewDTO, Authentication authentication) {
        Long userId = getUserIdFromAuthentication(authentication);

        // check from the orderId if the eventId == event_id and userId == customer_id in the table orders
        Orders order = orderRepository.findById(createReviewDTO.getOrderId()).orElseThrow(() -> new ApplicationException("Order not found."));
        if (order.getCustomerId() != userId || order.getEventId() != createReviewDTO.getEventId()){
            throw new ApplicationException("You cannot review this event.");
        }

        if (reviewsRepository.findByUserIdAndEventId(userId, createReviewDTO.getEventId()).isPresent()) {
            throw new ApplicationException("You have already reviewed this event");
        }

        Reviews review = new Reviews();
        review.setUserId(userId);
        review.setEventId(createReviewDTO.getEventId());
        review.setOrderId(createReviewDTO.getOrderId());
        review.setRating(createReviewDTO.getRating());
        review.setReviewText(createReviewDTO.getReviewText());

        Reviews savedReview = reviewsRepository.save(review);
        return mapToReviewResponseDTO(savedReview);
    }

    @Override
    @Transactional
    public ReviewResponseDTO updateReview(Long reviewId, UpdateReviewDTO updateReviewDTO, Authentication authentication) {
        Long userId = getUserIdFromAuthentication(authentication);
        Reviews review = reviewsRepository.findById(reviewId)
                .orElseThrow(() -> new ApplicationException("Review not found"));

        if (!review.getUserId().equals(userId)) {
            throw new ApplicationException("You are not authorized to update this review");
        }

        review.setRating(updateReviewDTO.getRating());
        review.setReviewText(updateReviewDTO.getReviewText());

        Reviews updatedReview = reviewsRepository.save(review);
        return mapToReviewResponseDTO(updatedReview);
    }

    @Override
    @Transactional
    public void deleteReview(Long reviewId, Authentication authentication) {
        Long userId = getUserIdFromAuthentication(authentication);
        Reviews review = reviewsRepository.findById(reviewId)
                .orElseThrow(() -> new ApplicationException("Review not found"));

        if (!review.getUserId().equals(userId)) {
            throw new ApplicationException("You are not authorized to delete this review");
        }

        reviewsRepository.delete(review);

    }

    @Override
    public ReviewResponseDTO getReview(Long reviewId) {
        Reviews review = reviewsRepository.findById(reviewId)
                .orElseThrow(() -> new ApplicationException("Review not found"));
        return mapToReviewResponseDTO(review);
    }

    @Override
    public PaginatedReviewResponseDTO getReviewsByEvent(Long eventId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<Reviews> reviewPage = reviewsRepository.findByEventId(eventId, pageable);
        return createPaginatedResponse(reviewPage);
    }

//    @Override
//    public PaginatedReviewResponseDTO getReviewsByUser(Authentication authentication, int page, int size) {
//        Long userId = getUserIdFromAuthentication(authentication);
//        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
//        Page<Reviews> reviewPage = reviewsRepository.findByUserId(userId, pageable);
//        return createPaginatedResponse(reviewPage);
//    }

    private Long getUserIdFromAuthentication(Authentication authentication) {
        Jwt jwt = (Jwt) authentication.getPrincipal();
        return jwt.getClaim("userId");
    }

    private ReviewResponseDTO mapToReviewResponseDTO(Reviews review) {
        ReviewResponseDTO dto = new ReviewResponseDTO();
        dto.setId(review.getId());
        dto.setUserId(review.getUserId());
        dto.setEventId(review.getEventId());
        dto.setOrderId(review.getOrderId());
        dto.setRating(review.getRating());
        dto.setReviewText(review.getReviewText());
        return dto;
    }

    private PaginatedReviewResponseDTO createPaginatedResponse(Page<Reviews> reviewPage) {
        List<ReviewDetailsDTO> reviews = reviewPage.getContent().stream()
                .map(this::mapToReviewDetailsDTO)
                .collect(Collectors.toList());
        PaginatedReviewResponseDTO response = new PaginatedReviewResponseDTO();
        response.setReviews(reviews);
        response.setPage(reviewPage.getNumber());
        response.setPerPage(reviewPage.getSize());
        response.setTotalPages(reviewPage.getTotalPages());
        response.setTotalReviews(reviewPage.getTotalElements());

        return response;
    }

    private ReviewDetailsDTO mapToReviewDetailsDTO(Reviews review) {
        ReviewDetailsDTO dto = new ReviewDetailsDTO();
        dto.setId(review.getId());
        dto.setRating(review.getRating());
        dto.setReviewText(review.getReviewText());

        Users user = userRepository.findById(review.getUserId())
                .orElseThrow(() -> new ApplicationException("User not found"));

        ReviewDetailsDTO.UserDTO userDetailsDTO = new ReviewDetailsDTO.UserDTO();
        userDetailsDTO.setFirstName(user.getFirstName());
        userDetailsDTO.setLastName(user.getLastName());
        userDetailsDTO.setAvatar(user.getAvatar());

        dto.setUser(userDetailsDTO);

        return dto;
    }


}

