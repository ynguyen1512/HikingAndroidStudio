package com.example.khike;

import android.graphics.Bitmap;

public class HikeData {
    private int id;
    private String name;
    private String location;
    private String date;
    private String parking;
    private String length;
    private String difficulty;
    private String description;
    private Bitmap imageBitmap;

    public HikeData(Integer id, String name, String location, String date, String parking, String length, String difficulty, String description, Bitmap imageBitmap) {
        this.id = id;
        this.name = name;
        this.location = location;
        this.date = date;
        this.parking = parking;
        this.length = length;
        this.difficulty = difficulty;
        this.description = description;
        this.imageBitmap = imageBitmap;
    }

    public HikeData() {

    }

    private boolean isBookmarked;
    // Add getter and setter for this field
    public boolean isBookmarked() { return isBookmarked; }
    public void setBookmarked(boolean bookmarked) { isBookmarked = bookmarked; }

    public Integer getId() {return id;}

    public void setId(Integer id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getLocation() {
        return location;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getDate() {
        return date;
    }

    public void setParking(String parking) {
        this.parking = parking;
    }

    public String getParking() {
        return parking;
    }
    

    public void setLength(String length) {
        this.length = length;
    }
    public String getLength() {
        return length;
    }

    public void setDifficulty(String difficulty){
        this.difficulty = difficulty;
    }
    public String getDifficulty() {
        return difficulty;
    }

    public void setDescription(String description){
        this.description = description;
    }
    public String getDescription(){
        return description;
    }

    public void setImage(Bitmap image) {
        this.imageBitmap = image;
    }

    public Bitmap getImageBitmap() {
        return imageBitmap;
    }




}
