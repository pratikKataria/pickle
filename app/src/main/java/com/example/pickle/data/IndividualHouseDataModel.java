package com.example.pickle.data;

public class IndividualHouseDataModel extends  Address{

    private String residentialType;

    public IndividualHouseDataModel() {

    }

    public IndividualHouseDataModel(String areaPin, String address, String landmark, String floors, String residentialType) {
        super(areaPin, address, landmark, floors);
        this.residentialType =residentialType;
    }

    public IndividualHouseDataModel(String areaPin, String address, String residentialType) {
        super(areaPin, address);
        this.residentialType = residentialType;
    }

    public String getResidentialType() {
        return residentialType;
    }

    public void setResidentialType(String residentialType) {
        this.residentialType = residentialType;
    }
}