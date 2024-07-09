package com.mini_project.miniproject.auth.controller;

import com.mini_project.miniproject.auth.dto.LoginRequestDto;
import com.mini_project.miniproject.auth.dto.LoginResponseDto;
import com.mini_project.miniproject.auth.entity.UserAuth;
import com.mini_project.miniproject.auth.service.AuthService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/v1/")
@Validated
public class AuthController {
    private final AuthService authService;
    private final AuthenticationManager authenticationManager;

    public AuthController(AuthService authService, AuthenticationManager authenticationManager) {
        this.authService = authService;
        this.authenticationManager = authenticationManager;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequestDto loginRequestDto, HttpServletResponse response) throws IllegalAccessException {
        Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                loginRequestDto.getEmail(),
                loginRequestDto.getPassword()
        ));
        SecurityContextHolder.getContext().setAuthentication(authentication);

        UserAuth userDetails = (UserAuth) authentication.getPrincipal();
        String token = authService.generateToken(authentication);

        LoginResponseDto responseDto = new LoginResponseDto();
        responseDto.setMessage("User logged in successfully");
        responseDto.setToken(token);

        // Set the cookie with appropriate attributes
        String cookieValue = String.format("sid=%s; Path=/; HttpOnly; Secure; SameSite=None; Max-Age=%d",
                token, 24 * 60 * 60); // 1 day expiry
        response.setHeader("Set-Cookie", cookieValue);

        return ResponseEntity.status(HttpStatus.OK).body(responseDto);
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(@RequestHeader(name = "Authorization") String token, HttpServletResponse response) {
        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7);
            authService.logout(token);

            // Clear the cookie on logout
            String cookieValue = "sid=; Path=/; HttpOnly; Secure; SameSite=None; Max-Age=0";
            response.setHeader("Set-Cookie", cookieValue);

            return ResponseEntity.ok().body("Logged out successfully");
        }
        return ResponseEntity.badRequest().body("Invalid token");
    }
}

//@RestController
//@RequestMapping("/api/v1/")
//@Validated
//public class AuthController {
//    private final AuthService authService;
//    private final AuthenticationManager authenticationManager;
//
//    public AuthController(AuthService authService, AuthenticationManager authenticationManager) {
//        this.authService = authService;
//        this.authenticationManager = authenticationManager;
//    }
//
//    @PostMapping("/login")
//    public ResponseEntity<?> login(@RequestBody LoginRequestDto loginRequestDto) throws IllegalAccessException {
//        Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
//                loginRequestDto.getEmail(),
//                loginRequestDto.getPassword()
//        ));
//        var ctx = SecurityContextHolder.getContext();
//        ctx.setAuthentication(authentication);
//
//        UserAuth userDetails = (UserAuth) authentication.getPrincipal();
//        String token = authService.generateToken(authentication);
//
////        LoginResponseDto responseDto = new LoginResponseDto();
////        responseDto.setMessage("User logged in successfully");
////        responseDto.setToken(token);
////
////        Cookie cookie = new Cookie("sid", token);
////        HttpHeaders headers = new HttpHeaders();
////        headers.add("Set-Cookie", cookie.getName() + "=" + cookie.getValue() + "; Path=/; HttpOnly");
////        return ResponseEntity.status(HttpStatus.OK).headers(headers).body(responseDto);
//
//        LoginResponseDto responseDto = new LoginResponseDto();
//        responseDto.setMessage("User logged in successfully");
//        responseDto.setToken(token);
//
//        // Setting the cookie with SameSite=None; Secure; HttpOnly attributes
//        Cookie cookie = new Cookie("sid", token);
//        cookie.setPath("/");
//        cookie.setHttpOnly(true);
//        cookie.setSecure(true); // This ensures the cookie is only sent over HTTPS
//        cookie.setMaxAge(24 * 60 * 60); // 1 day expiry, adjust as needed
//        cookie.setAttribute("SameSite", "None");
//
//        HttpHeaders headers = new HttpHeaders();
//        headers.add("Set-Cookie", String.format("%s=%s; Path=%s; HttpOnly; Secure; SameSite=None",
//                cookie.getName(), cookie.getValue(), cookie.getPath()));
//
//        return ResponseEntity.status(HttpStatus.OK).headers(headers).body(responseDto);
//    }
//
//    @PostMapping("/logout")
//    public ResponseEntity<?> logout(@RequestHeader(name = "Authorization") String token) {
//        if (token != null && token.startsWith("Bearer ")) {
//            token = token.substring(7);
//            authService.logout(token);
//            return ResponseEntity.ok().body("Logged out successfully");
//        }
//        return ResponseEntity.badRequest().body("Invalid token");
//    }
//
//}
