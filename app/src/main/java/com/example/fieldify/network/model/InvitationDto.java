package com.example.fieldify.network.model;

public class InvitationDto {
    private Long id;
    private String senderName;
    private String senderEmail;
    private TerrainDto terrain;
    private String date;
    private String timeSlot;
    private String status; // "PENDING", "ACCEPTED", "DECLINED"

    public InvitationDto() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getSenderName() {
        if (senderName != null && !senderName.isEmpty()) {
            return senderName;
        }
        return senderEmail != null ? senderEmail : "Un ami";
    }

    public void setSenderName(String senderName) {
        this.senderName = senderName;
    }

    public String getSenderEmail() {
        return senderEmail;
    }

    public void setSenderEmail(String senderEmail) {
        this.senderEmail = senderEmail;
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
        if ("ACCEPTED".equalsIgnoreCase(status)) {
            return "Acceptée";
        } else if ("DECLINED".equalsIgnoreCase(status)) {
            return "Refusée";
        }
        return "En attente";
    }

    public void setStatus(String status) {
        this.status = status;
    }
}