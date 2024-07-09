package com.mini_project.miniproject.user.entity;

public enum Role {
    USER,
    ORGANIZER;

    public static Role fromString(String role) {
        return Role.valueOf(role.toUpperCase());
    }

}
