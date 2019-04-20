package com.sebas.licenta1.dto;

import java.util.ArrayList;

public class PlaceDetails {
    private String placeID;
    private String description;
    private String name;
    private String preBooking;
    private String address;
    private String ambientType;
    private String foodType;
    private ArrayList<Table> tables;

    public PlaceDetails() {
        // no arg constructor neeeded
    }

    public PlaceDetails(String placeID, String description, String name, String preBooking, String address, String ambientType, String foodType, ArrayList<Table> tables) {
        this.placeID = placeID;
        this.description = description;
        this.name = name;
        this.preBooking = preBooking;
        this.address = address;
        this.ambientType = ambientType;
        this.foodType = foodType;
        this.tables = tables;
    }

    public String getPlaceID() {
        return placeID;
    }

    public void setPlaceID(String placeID) {
        this.placeID = placeID;
    }

    public ArrayList<Table> getTables() {
        return tables;
    }

    public void setTables(ArrayList<Table> tables) {
        this.tables = tables;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPreBooking() {
        return preBooking;
    }

    public void setPreBooking(String preBooking) {
        this.preBooking = preBooking;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getAmbientType() {
        return ambientType;
    }

    public void setAmbientType(String ambientType) {
        this.ambientType = ambientType;
    }

    public String getFoodType() {
        return foodType;
    }

    public void setFoodType(String foodType) {
        this.foodType = foodType;
    }
}
