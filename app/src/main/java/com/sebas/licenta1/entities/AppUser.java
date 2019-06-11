package com.sebas.licenta1.entities;

import java.io.Serializable;
import java.util.ArrayList;

public class AppUser implements Serializable {
    private String firstName;
    private String lastName;
    private String emailAddress;
    private String profileImgUrl;
    private ArrayList<Reservation> reservations;
    private ArrayList<PlaceDetails> favoritePlaces;

    public AppUser() {

    }

    public AppUser(String firstName, String lastName, String emailAddress, String profileImgUrl, ArrayList<Reservation> reservations, ArrayList<PlaceDetails> favoritePlaces) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.emailAddress = emailAddress;
        this.profileImgUrl = profileImgUrl;
        this.reservations = reservations;
        this.favoritePlaces = favoritePlaces;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmailAddress() {
        return emailAddress;
    }

    public void setEmailAddress(String emailAddress) {
        this.emailAddress = emailAddress;
    }

    public String getProfileImgUrl() {
        return profileImgUrl;
    }

    public void setProfileImgUrl(String profileImgUrl) {
        this.profileImgUrl = profileImgUrl;
    }

    public String getFullName() {
        return this.firstName + " " + this.lastName;
    }

    @Override
    public String toString() {
        return String.format(firstName + " " + lastName + " " + emailAddress);
    }

    public ArrayList<Reservation> getReservations() {
        return reservations;
    }

    public void setReservations(ArrayList<Reservation> reservations) {
        this.reservations = reservations;
    }

    public ArrayList<PlaceDetails> getFavoritePlaces() {
        return favoritePlaces;
    }

    public void setFavoritePlaces(ArrayList<PlaceDetails> favoritePlaces) {
        this.favoritePlaces = favoritePlaces;
    }
}
