package com.example.fieldify.network.model;

import com.google.gson.annotations.SerializedName;

public class InvitationDto {
    @SerializedName("id")
    private Long id;

    @SerializedName("senderName")
    private String senderName;

    @SerializedName("senderEmail")
    private String senderEmail;

    @SerializedName("sender")
    private SenderDto sender;

    @SerializedName("reservation")
    private ReservationSummaryDto reservation;

    @SerializedName("terrain")
    private TerrainDto terrain;

    @SerializedName("date")
    private String date;

    @SerializedName("timeSlot")
    private String timeSlot;

    @SerializedName("status")
    private String status; // "PENDING", "ACCEPTED", "DECLINED"

    public InvitationDto() {
    }

    public static class SenderDto {
        @SerializedName("id")
        private Long id;

        @SerializedName("name")
        private String name;

        @SerializedName("email")
        private String email;

        public SenderDto() {
        }

        public Long getId() {
            return id;
        }

        public String getName() {
            return name;
        }

        public String getEmail() {
            return email;
        }
    }

    public static class ReservationSummaryDto {
        @SerializedName("id")
        private Long id;

        @SerializedName("date")
        private String date;

        @SerializedName("timeSlot")
        private String timeSlot;

        @SerializedName("terrain")
        private TerrainDto terrain;

        public ReservationSummaryDto() {
        }

        public Long getId() {
            return id;
        }

        public String getDate() {
            return date;
        }

        public String getTimeSlot() {
            return timeSlot;
        }

        public TerrainDto getTerrain() {
            return terrain;
        }
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getSenderName() {
        if (sender != null && sender.getName() != null && !sender.getName().trim().isEmpty()) {
            return sender.getName();
        }
        if (sender != null && sender.getEmail() != null && !sender.getEmail().trim().isEmpty()) {
            return sender.getEmail();
        }
        if (senderName != null && !senderName.trim().isEmpty()) {
            return senderName;
        }
        return senderEmail != null ? senderEmail : "Un ami";
    }

    public void setSenderName(String senderName) {
        this.senderName = senderName;
    }

    public String getSenderEmail() {
        if (sender != null && sender.getEmail() != null) {
            return sender.getEmail();
        }
        return senderEmail;
    }

    public void setSenderEmail(String senderEmail) {
        this.senderEmail = senderEmail;
    }

    public SenderDto getSender() {
        return sender;
    }

    public ReservationSummaryDto getReservation() {
        return reservation;
    }

    public TerrainDto getTerrain() {
        if (reservation != null && reservation.getTerrain() != null) {
            return reservation.getTerrain();
        }
        return terrain;
    }

    public void setTerrain(TerrainDto terrain) {
        this.terrain = terrain;
    }

    public String getDate() {
        if (reservation != null && reservation.getDate() != null && !reservation.getDate().trim().isEmpty()) {
            return reservation.getDate();
        }
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTimeSlot() {
        if (reservation != null && reservation.getTimeSlot() != null && !reservation.getTimeSlot().trim().isEmpty()) {
            return reservation.getTimeSlot();
        }
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