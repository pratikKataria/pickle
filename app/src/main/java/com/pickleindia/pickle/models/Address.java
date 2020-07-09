package com.pickleindia.pickle.models;

public class Address {
    private String apartmentSociety;
    private String areaPin;
    private String flatHouseNo;
    private String address;
    private String landmark;

    private String houseNoPlotno;
    private String floors;

    private String gpsLocation;

    public Address() {}

    //generic
    public Address(String apartmentSociety, String areaPin, String flatHouseNo, String address, String landmark, String houseNoPlotno, String floors) {
        this.apartmentSociety = apartmentSociety;
        this.areaPin = areaPin;
        this.flatHouseNo = flatHouseNo;
        this.address = address;
        this.landmark = landmark;
        this.houseNoPlotno = houseNoPlotno;
        this.floors = floors;
    }

    //Address apartment
    public Address(String apartmentSociety, String areaPin, String flatHouseNo, String address, String landmark) {
        this.apartmentSociety = apartmentSociety;
        this.areaPin = areaPin;
        this.flatHouseNo = flatHouseNo;
        this.address = address;
        this.landmark = landmark;
    }


    public Address(String areaPin, String address) {
        this.areaPin = areaPin;
        this.address = address;
    }

    //Address individual
    public Address(String areaPin, String address, String landmark, String floors) {
        this.areaPin = areaPin;
        this.address = address;
        this.landmark = landmark;
        this.floors = floors;
    }

    //address current;
    public Address(String gpsLocation) {
        this.gpsLocation = gpsLocation;
    }

    public String getApartmentSociety() {
        return apartmentSociety;
    }

    public void setApartmentSociety(String apartmentSociety) {
        this.apartmentSociety = apartmentSociety;
    }

    public String getAreaPin() {
        return areaPin;
    }

    public void setAreaPin(String areaPin) {
        this.areaPin = areaPin;
    }

    public String getFlatHouseNo() {
        return flatHouseNo;
    }

    public void setFlatHouseNo(String flatHouseNo) {
        this.flatHouseNo = flatHouseNo;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getLandmark() {
        return landmark;
    }

    public void setLandmark(String landmark) {
        this.landmark = landmark;
    }

    public String getHouseNoPlotno() {
        return houseNoPlotno;
    }

    public void setHouseNoPlotno(String houseNoPlotno) {
        this.houseNoPlotno = houseNoPlotno;
    }

    public String getFloors() {
        return floors;
    }

    public void setFloors(String floors) {
        this.floors = floors;
    }

    public String getGpsLocation() {
        return gpsLocation;
    }

    public void setGpsLocation(String gpsLocation) {
        this.gpsLocation = gpsLocation;
    }


    @Override
    public String toString() {
        if (gpsLocation != null) {
            return gpsLocationAddressFormatter();
        } else {
            return buildAddress();
        }
    }

    private String gpsLocationAddressFormatter() {
        return "current location is stored in database \nYou can update it by going to -> Address in navigation";
    }

    ;

    private String buildAddress() {
        return "Address:\n" +
                (apartmentSociety == null ? "" : apartmentSociety) + " " +
                "["+(areaPin == null ? "" : areaPin) + "] " +
                (flatHouseNo == null ? "" : flatHouseNo) + " " +
                (address == null ? "" : address) + " " +
                (landmark == null ? "" : landmark) + " " +
                (houseNoPlotno == null ? "" : houseNoPlotno) + " " +
                (floors == null ? "" : floors) + " ";
    }
}
