package com.mini_project.miniproject.auth.service;

import org.springframework.security.core.Authentication;

public interface AuthService {
    String generateToken(Authentication authentication);
    void logout(String token);
}