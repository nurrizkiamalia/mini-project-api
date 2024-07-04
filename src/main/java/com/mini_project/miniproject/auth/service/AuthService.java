package com.mini_project.miniproject.auth.service;


import com.mini_project.miniproject.auth.dto.LoginRequestDto;
import com.mini_project.miniproject.auth.dto.LoginResponseDto;

public interface AuthService {
    LoginResponseDto login(LoginRequestDto loginRequest);
}
