package com.sebas.licenta1.entities;

import java.io.Serializable;

public class Table implements Serializable {
    private Integer seatsNo;

    public Table() {
        // no arg constructor neeeded
    }

    public Table(Integer seatsNo) {
        this.seatsNo = seatsNo;
    }

    public Integer getSeatsNo() {
        return seatsNo;
    }

    public void setSeatsNo(Integer seatsNo) {
        this.seatsNo = seatsNo;
    }
}
