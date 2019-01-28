package com.example.riana.a500px;

/**
 * Created by riana on 2019-01-26.
 */

public class GridItem {
    private String image;
    private String title;
    private Double rating;
    private String description;
    private String user_firstname;
    private String user_lastname;

    public GridItem() {
        super();
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
