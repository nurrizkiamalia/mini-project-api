package com.mini_project.miniproject.user.service;


import com.mini_project.miniproject.user.dto.ChangePasswordDto;
import com.mini_project.miniproject.user.dto.ProfileResponseDto;
import com.mini_project.miniproject.user.dto.ProfileSettingsDto;
import com.mini_project.miniproject.user.dto.RegisterRequestDto;
import com.mini_project.miniproject.user.entity.Users;
import org.springframework.security.core.Authentication;

import java.io.IOException;


public interface UserService {
    Users register(RegisterRequestDto registerRequestDto);
    ProfileResponseDto getCurrentUserProfile(Authentication authentication);
    void updateCurrentUserProfile(Authentication authentication, ProfileSettingsDto profileSettingsDto) throws IOException;
    void changeCurrentUserPassword(Authentication authentication, ChangePasswordDto changePasswordDto);
}

