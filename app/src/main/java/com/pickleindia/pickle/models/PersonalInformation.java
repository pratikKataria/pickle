package com.pickleindia.pickle.models;

public class PersonalInformation {
    private String username;
    private String userId;
    private String deviceToken;
    private String creationDate;
    private String userPhoneNo;

    public PersonalInformation() { }

    public PersonalInformation(String username, String userId, String deviceToken, String creationDate, String userPhoneNo) {
        this.username = username;
        this.userId = userId;
        this.deviceToken = deviceToken;
        this.creationDate = creationDate;
        this.userPhoneNo = userPhoneNo;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getDeviceToken() {
        return deviceToken;
    }

    public void setDeviceToken(String deviceToken) {
        this.deviceToken = deviceToken;
    }

    public String getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(String creationDate) {
        this.creationDate = creationDate;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserPhoneNo() {
        return userPhoneNo;
    }

    public void setUserPhoneNo(String userPhoneNo) {
        this.userPhoneNo = userPhoneNo;
    }
}
