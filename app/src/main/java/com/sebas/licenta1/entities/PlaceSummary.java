package com.sebas.licenta1.entities;

import java.io.Serializable;

public class PlaceSummary implements Serializable {
    private String placeId;
    private String name;
    private String vicinity;
    private Float rating;
    private Integer ratingsNumber;
    private Integer priceLevel;

    public PlaceSummary(String placeId, String name, String vicinity, Float rating, Integer ratingsNumber, Integer priceLevel) {
        this.placeId = placeId;
        this.name = name;
        this.vicinity = vicinity;
        this.rating = rating;
        this.ratingsNumber = ratingsNumber;
        this.priceLevel = priceLevel;
    }

    public String getPlaceId() {
        return placeId;
    }

    public void setPlaceId(String placeId) {
        this.placeId = placeId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getVicinity() {
        return vicinity;
    }

    public void setVicinity(String vicinity) {
        this.vicinity = vicinity;
    }

    public Float getRating() {
        return rating;
    }

    public void setRating(Float rating) {
        this.rating = rating;
    }

    public Integer getRatingsNumber() {
        return ratingsNumber;
    }

    public void setRatingsNumber(Integer ratingsNumber) {
        this.ratingsNumber = ratingsNumber;
    }

    public Integer getPriceLevel() {
        return priceLevel;
    }

    public void setPriceLevel(Integer priceLevel) {
        this.priceLevel = priceLevel;
    }
}
