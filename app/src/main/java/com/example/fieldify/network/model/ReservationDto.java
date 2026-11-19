package com.example.fieldify.network.model;

import java.util.List;

public class ReservationDto {
    private Long id;
    private TerrainDto terrain;
    private String date;
    private String timeSlot;
    private String status;
    private List<String> invitedFriends;
    private List<String> invitedEmails;
    private String userEmail;
    private String userName;

    public ReservationDto() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public TerrainDto getTerrain() {
        return terrain;
    }

    public void setTerrain(TerrainDto terrain) {
        this.terrain = terrain;
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

    public String getStatus() {
        if ("CONFIRMED".equalsIgnoreCase(status)) {
            return "Confirmé";
        } else if ("REJECTED".equalsIgnoreCase(status) || "CANCELLED".equalsIgnoreCase(status)) {
            return "Refusé";
        } else if ("PENDING".equalsIgnoreCase(status)) {
            return "En attente";
        }
        return status != null ? status : "En attente";
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public List<String> getInvitedFriends() {
        if (invitedFriends != null && !invitedFriends.isEmpty()) {
            return invitedFriends;
        }
        return invitedEmails;
    }

    public void setInvitedFriends(List<String> invitedFriends) {
        this.invitedFriends = invitedFriends;
    }

    public List<String> getInvitedEmails() {
        return invitedEmails;
    }

    public void setInvitedEmails(List<String> invitedEmails) {
        this.invitedEmails = invitedEmails;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }
}