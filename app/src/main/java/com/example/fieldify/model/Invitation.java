package com.example.fieldify.model;

public class Invitation {
    private String id;
    private String senderName;
    private Terrain terrain;
    private String date;
    private String timeSlot;
    private String status; // "En attente", "Acceptée", "Refusée"

    public Invitation() {
    }

    public Invitation(String id, String senderName, Terrain terrain, String date, String timeSlot, String status) {
        this.id = id;
        this.senderName = senderName;
        this.terrain = terrain;
        this.date = date;
        this.timeSlot = timeSlot;
        this.status = status;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getSenderName() {
        return senderName;
    }

    public void setSenderName(String senderName) {
        this.senderName = senderName;
    }

    public Terrain getTerrain() {
        return terrain;
    }

    public void setTerrain(Terrain terrain) {
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
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}