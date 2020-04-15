package com.example.pickle.data;

public class CustomerOrdersData {
    private int itemQty;
    private int itemBasePrice;
    private String orderStatus;
    private String itemCategory;
    private String address;
    private String userId;
    private String itemId;
    private String date;

    public CustomerOrdersData(int itemQty, String itemId, int itemBasePrice, String orderStatus, String itemCategory, String address, String userId, String date) {
        this.itemQty = itemQty;
        this.itemBasePrice = itemBasePrice;
        this.orderStatus = orderStatus;
        this.itemCategory = itemCategory;
        this.address = address;
        this.userId = userId;
        this.itemId = itemId;
        this.date = date;
    }

    public CustomerOrdersData() {
    }

    public String getItemId() {
        return itemId;
    }

    public void setItemId(String itemId) {
        this.itemId = itemId;
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

    public int getItemQty() {
        return itemQty;
    }
}
