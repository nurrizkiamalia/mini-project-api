package com.mini_project.miniproject.auth.service;

import com.mini_project.miniproject.auth.repository.AuthRedisRepository;
import com.mini_project.miniproject.user.repository.UserRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.*;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.stream.Collectors;

@Service
public class AuthServiceImpl implements AuthService{
    private final JwtEncoder jwtEncoder;
    private final JwtDecoder jwtDecoder;
    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final AuthRedisRepository authRedisRepository;

    public AuthServiceImpl(JwtEncoder jwtEncoder, PasswordEncoder passwordEncoder, UserRepository userRepository, AuthRedisRepository authRedisRepository, JwtDecoder jwtDecoder) {
        this.jwtEncoder = jwtEncoder;
        this.passwordEncoder = passwordEncoder;
        this.userRepository = userRepository;
        this.authRedisRepository = authRedisRepository;
        this.jwtDecoder = jwtDecoder;
    }

    public String generateToken(Authentication authentication) {
        Instant now = Instant.now();
        String scope = authentication.getAuthorities()
                .stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(" "));

        var existingKey = authRedisRepository.getJwtKey((authentication.getName()));
        if (existingKey != null) {
            return existingKey;
        }

        JwtClaimsSet claims = JwtClaimsSet.builder()
                .issuer("self")
                .issuedAt(now)
                .expiresAt(now.plus(1, ChronoUnit.HOURS))
                .subject(authentication.getName())
                .claim("scope", scope)
                .claim("userId", userRepository.findByEmail(authentication.getName()).get().getId())
                .claim("role", userRepository.findByEmail(authentication.getName()).get().getRole())
                .build();

        var jwt = jwtEncoder.encode(JwtEncoderParameters.from(claims)).getTokenValue();
        authRedisRepository.saveJwtKey(authentication.getName(), jwt);
        return jwt;
    }

    @Override
    public void logout(String token) {
        authRedisRepository.addToBlacklist(token);
        String email = getEmailFromToken(token);
        if (email != null) {
            authRedisRepository.deleteJwtKey(email);
        }
    }

    private String getEmailFromToken(String token) {
        try {
            Jwt jwt = jwtDecoder.decode(token);
            return jwt.getSubject();
        } catch (Exception e) {
            return null;
        }
    }

}

