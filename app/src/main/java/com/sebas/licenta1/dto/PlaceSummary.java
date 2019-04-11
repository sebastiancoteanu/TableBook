package com.sebas.licenta1.dto;

public class PlaceSummary {
    private String placeId;
    private String name;
    private String vicinity;

    public PlaceSummary(String placeId, String name, String vicinity) {
        this.placeId = placeId;
        this.name = name;
        this.vicinity = vicinity;
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
}
