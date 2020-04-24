package com.example.pickle.data;

public class ApartmentDataModel  extends  Address{

    private String residentialType;

    public ApartmentDataModel() {

    }

    public ApartmentDataModel(String apartmentSociety, String areaPin, String flatHouseNo, String address, String landmark, String residentialType) {
        super(apartmentSociety, areaPin, flatHouseNo, address, landmark);
        this.residentialType = residentialType;
    }

    public String getResidentialType() {
        return residentialType;
    }

    public void setResidentialType(String residentialType) {
        this.residentialType = residentialType;
    }
}
