package com.mini_project.miniproject.user.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mini_project.miniproject.user.entity.Role;
import com.mini_project.miniproject.user.entity.Users;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.io.IOException;

@Data
public class RegisterRequestDto {
    @NotBlank(message = "First name is required")
    private String firstName;

    @NotBlank(message = "Last name is required")
    private String lastName;

//    @Email(message = "Invalid email format")
    @NotBlank(message = "Email is required")
    private String email;

//    @Size(min = 8, message = "Password must be at least 8 characters long")
    @NotBlank(message = "Password is required")
    private String password;

    @NotBlank(message = "Role is required")
    private String role;

    // Optional field
    private String referralCode;


    @JsonCreator
    public static RegisterRequestDto create(String json) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        return mapper.readValue(json, RegisterRequestDto.class);
    }

    @JsonValue
    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }


}
