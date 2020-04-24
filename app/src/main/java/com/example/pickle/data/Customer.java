package com.example.pickle.data;

public class Customer {
    PersonalInformation personalInformation;
    ApartmentDataModel apartmentDataModel;
    IndividualHouseDataModel individualHouseDataModel;

    String currentLocation;

    public Customer() {}

    public Customer(PersonalInformation personalInformation, ApartmentDataModel apartmentDataModel) {
        this.personalInformation = personalInformation;
        this.apartmentDataModel = apartmentDataModel;
    }

    public Customer(PersonalInformation personalInformation, IndividualHouseDataModel individualHouseDataModel) {
        this.personalInformation = personalInformation;
        this.individualHouseDataModel = individualHouseDataModel;
    }


    public Customer(PersonalInformation personalInformation, String currentLocation) {
        this.personalInformation = personalInformation;
        this.currentLocation = currentLocation;
    }

    public Customer(PersonalInformation personalInformation) {
        this.personalInformation = personalInformation;
    }

    public PersonalInformation getPersonalInformation() {
        return personalInformation;
    }

    public void setPersonalInformation(PersonalInformation personalInformation) {
        this.personalInformation = personalInformation;
    }

    public ApartmentDataModel getApartmentDataModel() {
        return apartmentDataModel;
    }

    public void setApartmentDataModel(ApartmentDataModel apartmentDataModel) {
        this.apartmentDataModel = apartmentDataModel;
    }

    public IndividualHouseDataModel getIndividualHouseDataModel() {
        return individualHouseDataModel;
    }

    public void setIndividualHouseDataModel(IndividualHouseDataModel individualHouseDataModel) {
        this.individualHouseDataModel = individualHouseDataModel;
    }

    public String getCurrentLocation() {
        return currentLocation;
    }

    public void setCurrentLocation(String currentLocation) {
        this.currentLocation = currentLocation;
    }
}
