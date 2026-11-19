package com.example.fieldify.network.model;

import java.util.List;

public class CreateTerrainRequest {
    private String sportName;
    private String name;
    private String description;
    private String address;
    private String city;
    private Double pricePerHour;
    private List<String> equipments;
    private List<String> imageUrls;

    public CreateTerrainRequest() {
    }

    public CreateTerrainRequest(String sportName, String name, String description, String address, String city, Double pricePerHour, List<String> equipments, List<String> imageUrls) {
        this.sportName = sportName;
        this.name = name;
        this.description = description;
        this.address = address;
        this.city = city;
        this.pricePerHour = pricePerHour;
        this.equipments = equipments;
        this.imageUrls = imageUrls;
    }

    public String getSportName() {
        return sportName;
    }

    public void setSportName(String sportName) {
        this.sportName = sportName;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public Double getPricePerHour() {
        return pricePerHour;
    }

    public void setPricePerHour(Double pricePerHour) {
        this.pricePerHour = pricePerHour;
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
}