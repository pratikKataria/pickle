package com.example.pickle.data;

import java.text.SimpleDateFormat;

public class PlaceOrderModel {
    private int itemBasePrice;
    private String orderStatus;
    private String itemCategory;
    private String address;
    private String userId;
    private String date;
    private ProductModel pm;

    public PlaceOrderModel(ProductModel pm, int itemBasePrice, String orderStatus, String itemCategory, String address, String userId, String date) {
        this.itemBasePrice = itemBasePrice;
        this.orderStatus = orderStatus;
        this.itemCategory = itemCategory;
        this.address = address;
        this.userId = userId;
        this.date = date;
        this.pm = pm;

    }

    public ProductModel getPm() {
        return pm;
    }

    public void setPm(ProductModel pm) {
        this.pm = pm;
    }

    public int getItemBasePrice() {
        return itemBasePrice;
    }

    public void setItemBasePrice(int itemBasePrice) {
        this.itemBasePrice = itemBasePrice;
    }

    public String getOrderStatus() {
        return orderStatus;
    }

    public void setOrderStatus(String orderStatus) {
        this.orderStatus = orderStatus;
    }

    public String getItemCategory() {
        return itemCategory;
    }

    public void setItemCategory(String itemCategory) {
        this.itemCategory = itemCategory;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
