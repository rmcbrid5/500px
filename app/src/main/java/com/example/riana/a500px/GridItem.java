package com.example.riana.a500px;

/**
 * Created by riana on 2019-01-26.
 */

public class GridItem {
    private String image_small;
    private String image_large;
    private String title;
    private Double rating;
    private String description;
    private String user;

    public GridItem() {
        super();
    }

    public String getSmallImage() {
        return image_small;
    }

    public void setSmallImage(String image) {
        this.image_small = image;
    }

    public String getLargeImage(){
        return image_large;
    }

    public void setLargeImage(String image){
        this.image_large = image;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Double getRating() {
        return rating;
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

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }
}
