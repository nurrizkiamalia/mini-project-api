package com.mini_project.miniproject.user.service.impl;

import com.mini_project.miniproject.exceptions.ApplicationException;
import com.mini_project.miniproject.user.dto.RegisterRequestDto;
import com.mini_project.miniproject.user.entity.Users;
import com.mini_project.miniproject.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import java.util.Optional;
import com.mini_project.miniproject.user.entity.Role;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

class UserServiceImplTest {

    @InjectMocks
    private UserServiceImpl userService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testRegister_SuccessfulRegistration() {
        // Arrange
        RegisterRequestDto requestDto = new RegisterRequestDto();
        requestDto.setFirstName("John");
        requestDto.setLastName("Doe");
        requestDto.setEmail("john@example.com");
        requestDto.setPassword("password123");
        requestDto.setRole("USER");

        when(userRepository.findByEmail(anyString())).thenReturn(Optional.empty());
        when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");
        when(userRepository.save(any(Users.class))).thenAnswer(invocation -> {
            Users savedUser = invocation.getArgument(0);
            savedUser.setId(1L); // Simulate database saving by setting an ID
            return savedUser;
        });

        // Act
        Users result = userService.register(requestDto);

        // Assert
        assertNotNull(result);
        assertEquals("John", result.getFirstName());
        assertEquals("Doe", result.getLastName());
        assertEquals("john@example.com", result.getEmail());
        assertEquals("encodedPassword", result.getPassword());
        assertEquals(Role.USER, result.getRole());
        assertNotNull(result.getReferralCode());

        verify(userRepository).findByEmail("john@example.com");
        verify(passwordEncoder).encode("password123");
        verify(userRepository).save(any(Users.class));
    }

    @Test
    void testRegister_EmailAlreadyExists() {
        // Arrange
        RegisterRequestDto requestDto = new RegisterRequestDto();
        requestDto.setFirstName("John");
        requestDto.setLastName("Doe");
        requestDto.setEmail("a.minpro@example.dev"); // Using an email from the existing list
        requestDto.setPassword("password123");
        requestDto.setRole("USER");

        Users existingUser = new Users();
        existingUser.setEmail("a.minpro@example.dev");

        when(userRepository.findByEmail("a.minpro@example.dev")).thenReturn(Optional.of(existingUser));

        // Act & Assert
        ApplicationException exception = assertThrows(ApplicationException.class, () -> {
            userService.register(requestDto);
        });

        // Verify the exception details
        assertEquals(HttpStatus.BAD_REQUEST, exception.getHttpStatus());
        assertEquals("Failed to register. Email already exists.", exception.getMessage());

        // Verify that the findByEmail method was called with the correct email
        verify(userRepository).findByEmail("a.minpro@example.dev");

        // Verify that save was never called
        verify(userRepository, never()).save(any(Users.class));
    }

    @Test
    void testRegister_InvalidReferralCode() {
        // Arrange
        RegisterRequestDto requestDto = new RegisterRequestDto();
        requestDto.setFirstName("Jane");
        requestDto.setLastName("Doe");
        requestDto.setEmail("jane@example.com");
        requestDto.setPassword("password123");
        requestDto.setRole("USER");
        requestDto.setReferralCode("53DLEP5N"); // An invalid referral code

        // Mock the behavior for a new email (not existing)
        when(userRepository.findByEmail("jane@example.com")).thenReturn(Optional.empty());

        // Mock the behavior for an invalid referral code
        when(userRepository.findByReferralCode("53DLEP5N")).thenReturn(Optional.empty());

        // Act & Assert
        ApplicationException exception = assertThrows(ApplicationException.class, () -> {
            userService.register(requestDto);
        });

        // Verify the exception details
        assertEquals(HttpStatus.BAD_REQUEST, exception.getHttpStatus());
        assertEquals("Failed to register. Invalid referral code.", exception.getMessage());

        // Verify that the findByEmail and findByReferralCode methods were called with the correct parameters
        verify(userRepository).findByEmail("jane@example.com");
        verify(userRepository).findByReferralCode("53DLEP5N");

        // Verify that save was never called
        verify(userRepository, never()).save(any(Users.class));
    }
}