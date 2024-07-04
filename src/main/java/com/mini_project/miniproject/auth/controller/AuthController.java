package com.mini_project.miniproject.auth.controller;

import com.mini_project.miniproject.auth.dto.LoginRequestDto;
import com.mini_project.miniproject.auth.dto.LoginResponseDto;
import com.mini_project.miniproject.auth.service.AuthService;
import com.mini_project.miniproject.responses.Response;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/")
public class AuthController {
    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    public ResponseEntity<Response<LoginResponseDto>> login(@Valid @RequestBody LoginRequestDto loginRequest) {
        LoginResponseDto loginResponse = authService.login(loginRequest);
        return Response.success("Login successful", loginResponse);
    }

//    @PostMapping("/logout")
//    public ResponseEntity<Response<Object>> logout(@RequestHeader("Authorization") String token) {
//        authService.logout(token);
//        return Response.success("Logout successful");
//    }
}
