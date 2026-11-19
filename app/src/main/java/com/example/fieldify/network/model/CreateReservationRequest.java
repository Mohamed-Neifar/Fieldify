package com.example.fieldify.network.model;

import java.util.List;

public class CreateReservationRequest {
    private Long terrainId;
    private String date;
    private String timeSlot;
    private List<String> invitedEmails;

    public CreateReservationRequest() {
    }

    public CreateReservationRequest(Long terrainId, String date, String timeSlot, List<String> invitedEmails) {
        this.terrainId = terrainId;
        this.date = date;
        this.timeSlot = timeSlot;
        this.invitedEmails = invitedEmails;
    }

    public Long getTerrainId() {
        return terrainId;
    }

    public void setTerrainId(Long terrainId) {
        this.terrainId = terrainId;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTimeSlot() {
        return timeSlot;
    }

    public void setTimeSlot(String timeSlot) {
        this.timeSlot = timeSlot;
    }

    public List<String> getInvitedEmails() {
        return invitedEmails;
    }

    public void setInvitedEmails(List<String> invitedEmails) {
        this.invitedEmails = invitedEmails;
    }
}