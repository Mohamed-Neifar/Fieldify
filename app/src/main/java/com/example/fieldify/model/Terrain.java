package com.example.fieldify.model;

import java.util.List;

public class Terrain {
    private String id;
    private String name;
    private String category;
    private String zone;
    private String address;
    private double pricePerHour;
    private double rating;
    private String description;
    private List<String> equipments;
    private List<Integer> imageResIds;
    private List<String> imageUrls;
    private boolean isFavorite;

    public Terrain() {
    }

    public Terrain(String id, String name, String category, String zone, String address, double pricePerHour, double rating, String description, List<String> equipments, List<Integer> imageResIds, boolean isFavorite) {
        this.id = id;
        this.name = name;
        this.category = category;
        this.zone = zone;
        this.address = address;
        this.pricePerHour = pricePerHour;
        this.rating = rating;
        this.description = description;
        this.equipments = equipments;
        this.imageResIds = imageResIds;
        this.isFavorite = isFavorite;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getZone() {
        return zone;
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

    public double getPricePerHour() {
        return pricePerHour;
    }

    public void setPricePerHour(double pricePerHour) {
        this.pricePerHour = pricePerHour;
    }

    public double getRating() {
        return rating;
    }

    public void setRating(double rating) {
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

    public List<Integer> getImageResIds() {
        return imageResIds;
    }

    public void setImageResIds(List<Integer> imageResIds) {
        this.imageResIds = imageResIds;
    }

    public List<String> getImageUrls() {
        return imageUrls;
    }

    public void setImageUrls(List<String> imageUrls) {
        this.imageUrls = imageUrls;
    }

    public boolean isFavorite() {
        return isFavorite;
    }

    public void setFavorite(boolean favorite) {
        isFavorite = favorite;
    }

    public int getPrimaryImageResId() {
        if (imageResIds != null && !imageResIds.isEmpty()) {
            return imageResIds.get(0);
        }
        return 0;
    }
}