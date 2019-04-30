package com.sebas.licenta1.dto;

import java.io.Serializable;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;

public class Reservation implements Serializable {
    private long localDateTime;
    private String placeID;
    private Integer seatsNo;

    public Reservation() {
        // no arg constructor neeeded
    }

    public Reservation(long localDateTime, String placeID, Integer seatsNo) {
        this.localDateTime = localDateTime;
        this.placeID = placeID;
        this.seatsNo = seatsNo;
    }

    public long getLocalDateTime() {
        return localDateTime;
    }

    public void setLocalDateTime(long localDateTime) {
        this.localDateTime = localDateTime;
    }

    public String getPlaceID() {
        return placeID;
    }

    public void setPlaceID(String placeID) {
        this.placeID = placeID;
    }

    public Integer getSeatsNo() {
        return seatsNo;
    }

    public void setSeatsNo(Integer seatsNo) {
        this.seatsNo = seatsNo;
    }

    public LocalDateTime extractLocalDateTime() {
        LocalDateTime date = LocalDateTime.ofInstant(Instant.ofEpochMilli(localDateTime), ZoneId.systemDefault());
        return date;
    }
}
