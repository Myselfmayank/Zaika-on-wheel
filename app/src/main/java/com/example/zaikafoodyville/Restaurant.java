package com.example.zaikafoodyville;

public class Restaurant {
    private String restaurantName,description,imageUrl,rating;

    public Restaurant(String restaurantName, String description, String rating, String imageUrl) {
        this.restaurantName = restaurantName;
        this.description = description;
        this.rating = rating;
        this.imageUrl = imageUrl;
    }

    public Restaurant() {
    }

    public String getRestaurantName() {
        return restaurantName;
    }

    public void setRestaurantName(String restaurantName) {
        this.restaurantName = restaurantName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getRating() {
        return rating;
    }

    public void setRating(String rating) {
        this.rating = rating;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
}
