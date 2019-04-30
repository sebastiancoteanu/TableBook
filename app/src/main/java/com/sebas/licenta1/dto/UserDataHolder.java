package com.sebas.licenta1.dto;

public class UserDataHolder {
    private AppUser appUser;

    public AppUser getAppUser() {
        return appUser;
    }

    public void setAppUser(AppUser appUser) {
        this.appUser = appUser;
    }

    private UserDataHolder() {

    }

    private static final UserDataHolder userDataHolder = new UserDataHolder();
    public static UserDataHolder getInstance() {return userDataHolder;}
}
