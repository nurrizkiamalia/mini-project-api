package com.mini_project.miniproject.user.service;


import com.mini_project.miniproject.user.dto.ProfileResponseDto;
import com.mini_project.miniproject.user.dto.RegisterRequestDto;
import com.mini_project.miniproject.user.entity.Users;


public interface UserService {
    Users register(RegisterRequestDto registerRequestDto);

    ProfileResponseDto getUserProfile(Long userId);

}
