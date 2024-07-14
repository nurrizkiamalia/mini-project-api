package com.mini_project.miniproject.reviews.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class UpdateReviewDTO {
    @NotNull
    @Min(1)
    @Max(5)
    private Integer rating;

    private String reviewText;
}
