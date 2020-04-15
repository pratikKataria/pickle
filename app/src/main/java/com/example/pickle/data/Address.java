package com.example.pickle.data;

public class Address {
    private String city;
    private String nearLandmark;
    private String streetName;
    private long areaCode;
    private String state = "MadhyaPradesh";

    public Address() {
    }

    public Address(String city, String nearLandmark, String streetName, long areaCode, String state) {
        this.city = city;
        this.nearLandmark = nearLandmark;
        this.streetName = streetName;
        this.areaCode = areaCode;
        this.state = state;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getNearLandmark() {
        return nearLandmark;
    }

    public void setNearLandmark(String nearLandmark) {
        this.nearLandmark = nearLandmark;
    }

    public String getStreetName() {
        return streetName;
    }

    public void setStreetName(String streetName) {
        this.streetName = streetName;
    }

    public long getAreaCode() {
        return areaCode;
    }

    public void setAreaCode(long areaCode) {
        this.areaCode = areaCode;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    @Override
    public String toString() {
        return city +" \n"+
                nearLandmark +" \n"+
                streetName +" \n"+
                areaCode +" \n"+
                state;
    }
}
