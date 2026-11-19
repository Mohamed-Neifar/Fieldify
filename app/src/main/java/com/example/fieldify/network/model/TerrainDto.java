package com.example.fieldify.network.model;

import java.util.List;

public class TerrainDto {
    private Long id;
    private String name;
    private String category;
    private String sportName;
    private SportDto sport;
    private String city;
    private String zone;
    private String address;
    private Double pricePerHour;
    private Double rating;
    private String description;
    private List<String> equipments;
    private List<String> imageUrls;
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

    public String getCategory() {
        if (category != null && !category.trim().isEmpty()) {
            return category;
        }
        if (sportName != null && !sportName.trim().isEmpty()) {
            return sportName;
        }
        if (sport != null && sport.getName() != null && !sport.getName().trim().isEmpty()) {
            return sport.getName();
        }
        return "Football";
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getSportName() {
        return sportName;
    }

    public void setSportName(String sportName) {
        this.sportName = sportName;
    }

    public SportDto getSport() {
        return sport;
    }

    public void setSport(SportDto sport) {
        this.sport = sport;
    }

    public String getCity() {
        if (city != null && !city.trim().isEmpty()) {
            return city;
        }
        return zone != null ? zone : "Sfax, Tunisie";
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getZone() {
        if (zone != null && !zone.trim().isEmpty()) {
            return zone;
        }
        return city != null ? city : "Sfax, Tunisie";
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