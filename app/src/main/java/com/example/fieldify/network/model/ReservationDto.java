package com.example.fieldify.network.model;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class ReservationDto {
    @SerializedName("id")
    private Long id;

    @SerializedName("terrain")
    private TerrainDto terrain;

    @SerializedName("date")
    private String date;

    @SerializedName("timeSlot")
    private String timeSlot;

    @SerializedName("status")
    private String status;

    @SerializedName("invitations")
    private List<InvitationSummary> invitations;

    @SerializedName("invitedFriends")
    private List<String> invitedFriends;

    @SerializedName("invitedEmails")
    private List<String> invitedEmails;

    @SerializedName("userEmail")
    private String userEmail;

    @SerializedName("userName")
    private String userName;

    @SerializedName("isOrganizer")
    private Boolean isOrganizer;

    @SerializedName("organizer")
    private Boolean organizer;

    public ReservationDto() {
    }

    public static class InvitationSummary {
        @SerializedName("id")
        private Long id;

        @SerializedName("invitedEmail")
        private String invitedEmail;

        @SerializedName("status")
        private String status;

        public InvitationSummary() {
        }

        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public String getInvitedEmail() {
            return invitedEmail;
        }

        public void setInvitedEmail(String invitedEmail) {
            this.invitedEmail = invitedEmail;
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }
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

    public List<InvitationSummary> getInvitations() {
        return invitations;
    }

    public void setInvitations(List<InvitationSummary> invitations) {
        this.invitations = invitations;
    }

    public List<String> getInvitedFriends() {
        List<String> list = new ArrayList<>();
        if (invitations != null && !invitations.isEmpty()) {
            for (InvitationSummary inv : invitations) {
                if (inv != null && inv.getInvitedEmail() != null && !inv.getInvitedEmail().trim().isEmpty()) {
                    list.add(inv.getInvitedEmail().trim());
                }
            }
        }
        if (list.isEmpty() && invitedFriends != null && !invitedFriends.isEmpty()) {
            list.addAll(invitedFriends);
        }
        if (list.isEmpty() && invitedEmails != null && !invitedEmails.isEmpty()) {
            list.addAll(invitedEmails);
        }
        return list;
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

    public Boolean getIsOrganizer() {
        if (isOrganizer != null) return isOrganizer;
        if (organizer != null) return organizer;
        return true;
    }

    public void setIsOrganizer(Boolean isOrganizer) {
        this.isOrganizer = isOrganizer;
    }
}