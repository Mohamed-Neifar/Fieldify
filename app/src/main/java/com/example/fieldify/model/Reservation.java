package com.example.fieldify.model;

import java.util.ArrayList;
import java.util.List;

public class Reservation {
    private String id;
    private Terrain terrain;
    private String date;
    private String timeSlot;
    private String status; // "Confirmé", "En attente", "Terminé"
    private List<String> invitedFriends;
    private boolean organizer = true;

    public Reservation() {
        this.invitedFriends = new ArrayList<>();
    }

    public Reservation(String id, Terrain terrain, String date, String timeSlot, String status, List<String> invitedFriends) {
        this.id = id;
        this.terrain = terrain;
        this.date = date;
        this.timeSlot = timeSlot;
        this.status = status;
        this.invitedFriends = invitedFriends != null ? invitedFriends : new ArrayList<>();
        this.organizer = true;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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

    public List<String> getInvitedFriends() {
        return invitedFriends;
    }

    public void setInvitedFriends(List<String> invitedFriends) {
        this.invitedFriends = invitedFriends;
    }

    public void addInvitedFriend(String friend) {
        if (invitedFriends == null) {
            invitedFriends = new ArrayList<>();
        }
        if (!invitedFriends.contains(friend)) {
            invitedFriends.add(friend);
        }
    }

    public boolean isOrganizer() {
        return organizer;
    }

    public void setOrganizer(boolean organizer) {
        this.organizer = organizer;
    }
}