package com.example.fieldify.network.model;

public class UpdateReservationStatusRequest {
    private String status; // "CONFIRMED" or "REJECTED"

    public UpdateReservationStatusRequest() {
    }

    public UpdateReservationStatusRequest(String status) {
        this.status = status;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}