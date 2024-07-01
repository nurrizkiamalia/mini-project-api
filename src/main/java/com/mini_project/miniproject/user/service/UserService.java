package com.mini_project.miniproject.user.service;


import com.mini_project.miniproject.user.dto.ChangePasswordDto;
import com.mini_project.miniproject.user.dto.ProfileResponseDto;
import com.mini_project.miniproject.user.dto.ProfileSettingsDto;
import com.mini_project.miniproject.user.dto.RegisterRequestDto;
import com.mini_project.miniproject.user.entity.Users;

import java.io.IOException;


public interface UserService {
    Users register(RegisterRequestDto registerRequestDto);
    ProfileResponseDto getUserProfile(Long userId);
    void updateProfile(Long userId, ProfileSettingsDto profileSettingsDto) throws IOException;
    void changePassword(Long userId, ChangePasswordDto changePasswordDto);
}
