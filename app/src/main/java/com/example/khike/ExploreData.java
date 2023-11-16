package com.example.khike;

import android.graphics.Bitmap;

public class ExploreData {
    private Integer id;
    private String datePost;

    private Bitmap imagePost;

    private String nameHike;
    private String locationHike;


    public ExploreData(){}
    // Constructor
    public ExploreData(Integer id, String nameHike, String locationHike, String datePost, Bitmap imagePost) {
        this.id = id;
        this.nameHike = nameHike;
        this.locationHike = locationHike;
        this.datePost = datePost;
        this.imagePost = imagePost;

    }

    // Getter and setter for id

    public Integer getId(){
        return id;
    }


    public void setId(Integer id) {
        this.id = id;
    }

    // Getter and setter for nameHike
    public String getNameHike() {
        return nameHike;
    }

    public void setNameHike(String nameHike) {
        this.nameHike = nameHike;
    }

    // Getter and setter for locationHike
    public String getLocationHike() {
        return locationHike;
    }

    public void setLocationHike(String locationHike) {
        this.locationHike = locationHike;
    }

    // Getter and setter for datePost
    public String getDatePost() {
        return datePost;
    }

    public void setDatePost(String datePost) {
        this.datePost = datePost;
    }

    public void setImage(Bitmap image) {
        this.imagePost = image;
    }
    public Bitmap getImageBitmap() {
        return imagePost;
    }


}
