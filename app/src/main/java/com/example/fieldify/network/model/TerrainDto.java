package com.example.fieldify.network.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class TerrainDto {
    @SerializedName("id")
    private Long id;

    @SerializedName("name")
    private String name;

    @SerializedName("category")
    private String category;

    @SerializedName("sportName")
    private String sportName;

    @SerializedName("sport")
    private String sport;

    @SerializedName("city")
    private String city;

    @SerializedName("zone")
    private String zone;

    @SerializedName("address")
    private String address;

    @SerializedName("pricePerHour")
    private Double pricePerHour;

    @SerializedName("rating")
    private Double rating;

    @SerializedName("description")
    private String description;

    @SerializedName("equipments")
    private List<String> equipments;

    @SerializedName("imageUrls")
    private List<String> imageUrls;

    @SerializedName("active")
    private Boolean active;

    public TerrainDto() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSport() {
        return sport;
    }

    public void setSport(String sport) {
        this.sport = sport;
    }

    public String getSportName() {
        return sportName;
    }

    public void setSportName(String sportName) {
        this.sportName = sportName;
    }

    public String getCategory() {
        if (sport != null && !sport.trim().isEmpty()) {
            return sport;
        }
        if (sportName != null && !sportName.trim().isEmpty()) {
            return sportName;
        }
        if (category != null && !category.trim().isEmpty()) {
            return category;
        }
        return "Football";
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getCity() {
        if (city != null && !city.trim().isEmpty()) {
            return city;
        }
        return zone != null && !zone.trim().isEmpty() ? zone : "Sfax, Tunisie";
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getZone() {
        if (zone != null && !zone.trim().isEmpty()) {
            return zone;
        }
        return city != null && !city.trim().isEmpty() ? city : "Sfax, Tunisie";
    }

    public void setZone(String zone) {
        this.zone = zone;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public Double getPricePerHour() {
        return pricePerHour != null ? pricePerHour : 0.0;
    }

    public void setPricePerHour(Double pricePerHour) {
        this.pricePerHour = pricePerHour;
    }

    public Double getRating() {
        return rating != null ? rating : 4.8;
    }

    public void setRating(Double rating) {
        this.rating = rating;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<String> getEquipments() {
        return equipments;
    }

    public void setEquipments(List<String> equipments) {
        this.equipments = equipments;
    }

    public List<String> getImageUrls() {
        return imageUrls;
    }

    public void setImageUrls(List<String> imageUrls) {
        this.imageUrls = imageUrls;
    }

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }
}