package com.example.fieldify.network.model;

public class RespondInvitationRequest {
    private String status; // "ACCEPTED" or "DECLINED"

    public RespondInvitationRequest() {
    }

    public RespondInvitationRequest(String status) {
        this.status = status;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}