package com.example.fieldify.backend.dto;

import com.example.fieldify.backend.model.Role;
import com.example.fieldify.backend.model.User;
import java.time.Instant;
import java.util.UUID;

public class UserResponse {
    private UUID id;
    private String fullName;
    private String email;
    private String phone;
    private Role role;
    private Instant lastActiveAt;
    private Instant updatedAt;

    public UserResponse() {}

    public UserResponse(UUID id, String fullName, String email, String phone, Role role, Instant lastActiveAt, Instant updatedAt) {
        this.id = id;
        this.fullName = fullName;
        this.email = email;
        this.phone = phone;
        this.role = role;
        this.lastActiveAt = lastActiveAt;
        this.updatedAt = updatedAt;
    }

    public static UserResponse fromUser(User user) {
        if (user == null) return null;
        return new UserResponse(
                user.getId(),
                user.getFullName(),
                user.getEmail(),
                user.getPhone(),
                user.getRole(),
                user.getLastActiveAt(),
                user.getUpdatedAt()
        );
    }

    public UUID getId() { return id; }
    public String getFullName() { return fullName; }
    public String getEmail() { return email; }
    public String getPhone() { return phone; }
    public Role getRole() { return role; }
    public Instant getLastActiveAt() { return lastActiveAt; }
    public Instant getUpdatedAt() { return updatedAt; }
}
