package com.example.pickle.data;

public class OrdersDetails {
    private String userId;
    private String itemId;
    private String date;
    private int itemQty;
    private int itemBasePrice;
    private String itemCategory;
    private String address;
    private String deliveryTime;

    public OrdersDetails(String itemId, String date, int itemQty, int itemBasePrice, String itemCategory, String address, String deliveryTime) {
        this.itemId = itemId;
        this.date = date;
        this.itemQty = itemQty;
        this.itemBasePrice = itemBasePrice;
        this.itemCategory = itemCategory;
        this.address = address;
        this.deliveryTime = deliveryTime;
    }


    public OrdersDetails() { }

    public String getDeliveryTime() {
        return deliveryTime;
    }

    public void setDeliveryTime(String deliveryTime) {
        this.deliveryTime = deliveryTime;
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

    public Integer getItemQty() {
        return itemQty;
    }

    @Override
    public String toString() {
        return "OrdersDetails{" +
                "userId='" + userId + '\'' +
                ", itemId='" + itemId + '\'' +
                ", date='" + date + '\'' +
                ", itemQty=" + itemQty +
                ", itemBasePrice=" + itemBasePrice +
                ", itemCategory='" + itemCategory + '\'' +
                ", address='" + address + '\'' +
                ", deliveryTime='" + deliveryTime + '\'' +
                '}';
    }
}
