package com.mini_project.miniproject.user.controller;

import com.mini_project.miniproject.exceptions.ApplicationException;
import com.mini_project.miniproject.exceptions.DataNotFoundException;
import com.mini_project.miniproject.responses.Response;
import com.mini_project.miniproject.user.dto.ChangePasswordDto;
import com.mini_project.miniproject.user.dto.ProfileResponseDto;
import com.mini_project.miniproject.user.dto.ProfileSettingsDto;
import com.mini_project.miniproject.user.dto.RegisterRequestDto;
import com.mini_project.miniproject.user.entity.Users;
import com.mini_project.miniproject.user.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
@RequestMapping("/api/v1")
@Validated
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/register")
    public ResponseEntity<Response<Object>> register(@Valid @RequestBody RegisterRequestDto registerRequestDto) {
        try {
            Users registeredUser = userService.register(registerRequestDto);
            return Response.success("User registered successfully", registeredUser);
        } catch (ApplicationException e) {
            return Response.failed(e.getHttpStatus().value(), e.getMessage());
        }
    }

    @GetMapping("/profile/{userId}")
    public ResponseEntity<Response<Object>> getUserProfile(@PathVariable("userId") Long userId) {
        try {
            ProfileResponseDto profile = userService.getUserProfile(userId);
            return Response.success("User profile retrieved successfully", profile);
        } catch (ApplicationException e) {
            return Response.failed(e.getHttpStatus().value(), e.getMessage());
        }
    }

    @PutMapping(value = "/settings/profile/{userId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Response<Object>> updateProfile(
            @PathVariable Long userId,
            @Valid @ModelAttribute ProfileSettingsDto profileSettingsDto) {
        try {
            userService.updateProfile(userId, profileSettingsDto);
            return Response.success("Profile updated successfully");
        } catch (ApplicationException e) {
            return Response.failed(e.getHttpStatus().value(), e.getMessage());
        } catch (IOException e) {
            return Response.failed(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Error processing image");
        }
    }

    @PutMapping("/settings/password/{userId}")
    public ResponseEntity<Response<Object>> changePassword(
            @PathVariable Long userId,
            @Valid @RequestBody ChangePasswordDto changePasswordDto) {
        try {
            userService.changePassword(userId, changePasswordDto);
            return Response.success("Password changed successfully");
        } catch (ApplicationException e) {
            return Response.failed(e.getHttpStatus().value(), e.getMessage());
        }
    }

}
