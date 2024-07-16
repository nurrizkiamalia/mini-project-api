package com.mini_project.miniproject.user.dto;

import lombok.Data;

@Data
public class ProfileResponseDto {
    private String firstName;
    private String lastName;
    private String email;
    private String referralCode;
    private String avatar;
    private String quotes;
    private String role;
    private int points; // sum of points the user has that are not expired
    private String referralDiscount;

//    public void setRole(String role) {
//        this.role = role;
//    }
}

//    public String getRole(Role role) {
//        return role.toString();
//    }
//
//    public void setRole(String role) {
//        this.role = role;
//    }

