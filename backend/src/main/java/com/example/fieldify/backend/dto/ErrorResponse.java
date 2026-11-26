package com.example.fieldify.backend.dto;

import java.time.Instant;

public class ErrorResponse {
    private String message;
    private String timestamp;

    public ErrorResponse(String message) {
        this.message = message;
        this.timestamp = Instant.now().toString();
    }

    public String getMessage() { return message; }
    public String getTimestamp() { return timestamp; }
}
