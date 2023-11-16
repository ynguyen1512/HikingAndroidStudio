package com.example.khike;

import android.graphics.Bitmap;
import android.util.Pair;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class ObservationData {
    private int id;
    private int hikeId;
    private String name;
    private String date;
    private String time;
//    private String notes;
    private List<Pair<String, String>> titlesAndDescriptions;
    private Bitmap imageBitmap;


    public ObservationData() {

    }
    // Constructor to initialize all attributes
    public ObservationData(int id,int hikeId, String name, String date, String time, List<Pair<String, String>> titlesAndDescriptions, Bitmap imageBitmap) {
        this.id = id;
        this.hikeId = hikeId;
//        this.notes = notes;
        this.titlesAndDescriptions = titlesAndDescriptions;
        this.name = name;
        this.date = date;
        this.time = time;
        this.imageBitmap = imageBitmap;
    }


    public String getNotes() {
        StringBuilder sb = new StringBuilder();
        for (Pair<String, String> pair : titlesAndDescriptions) {
            sb.append(pair.first).append(": ").append(pair.second).append("\n");
        }
        return sb.toString(); // Convert the list to a single string for saving in the database
    }
    public void setNotes(String notes) {
        titlesAndDescriptions.clear(); // Clear the existing list

        // Split the notes String into individual note lines
        String[] lines = notes.split("\n");

        for (String line : lines) {
            // Split each line into title and description based on the ": " delimiter
            String[] parts = line.split(": ", 2); // Limit to 2 parts

            if (parts.length >= 2) {
                // Create a new pair and add to the list
                Pair<String, String> pair = new Pair<>(parts[0], parts[1]);
                titlesAndDescriptions.add(pair);
            }
        }
    }

    public String getTitles() {
        StringBuilder sb = new StringBuilder();
        for (Pair<String, String> pair : titlesAndDescriptions) {
            sb.append(pair.first).append("\n");
        }
        return sb.toString().trim();
    }

    public String getDescriptions() {
        StringBuilder sb = new StringBuilder();
        for (Pair<String, String> pair : titlesAndDescriptions) {
            sb.append(pair.second).append("\n");
        }
        return sb.toString().trim();
    }

    public void setTitlesAndDescriptions(List<Pair<String, String>> titlesAndDescriptions) {
        this.titlesAndDescriptions = titlesAndDescriptions;
    }


    public List<Pair<String, String>> getTitlesAndDescriptionsList() {
        return this.titlesAndDescriptions;
    }



    // Getters and setters for all attributes

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getHikeId() {
        return hikeId;
    }

    public void setHikeId(int hikeId) {
        this.hikeId = hikeId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public void setImage(Bitmap image) {
        this.imageBitmap = image;
    }
    public Bitmap getImageBitmap() {
        return imageBitmap;
    }


}
