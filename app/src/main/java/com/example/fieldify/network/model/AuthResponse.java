package com.example.fieldify.network.model;

public class AuthResponse {
    private String message;
    private String token;
    private UserDto user;

    // Fallback direct user fields if returned directly at root
    private Long id;
    private String name;
    private String email;
    private String role;

    public AuthResponse() {
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public UserDto getUser() {
        return user;
    }

    public void setUser(UserDto user) {
        this.user = user;
    }

    public Long getId() {
        if (id != null) {
            return id;
        }
        return user != null ? user.getId() : -1L;
    }

    public String getName() {
        if (name != null) {
            return name;
        }
        return user != null ? user.getName() : "";
    }

    public String getEmail() {
        if (email != null) {
            return email;
        }
        return user != null ? user.getEmail() : "";
    }

    public String getRole() {
        if (role != null) {
            return role;
        }
        return user != null ? user.getRole() : "JOUEUR";
    }
}