package com.mini_project.miniproject.user.dto;

import lombok.Data;

@Data
public class ProfileResponseDto {
    private String firstName;
    private String lastName;
    private String email;
    private String referralCode;
    private String avatar;
    private int points; // sum of points the user has that are not expired
}
