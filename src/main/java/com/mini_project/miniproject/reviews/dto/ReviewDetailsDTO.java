package com.mini_project.miniproject.reviews.dto;

import lombok.Data;

@Data
public class ReviewDetailsDTO {
    private Long id;
    private Integer rating;
    private String reviewText;
    private UserDTO user;

    @Data
    public static class UserDTO {
        private String firstName;
        private String lastName;
        private String avatar;
    }

}
