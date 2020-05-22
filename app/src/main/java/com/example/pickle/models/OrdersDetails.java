package com.example.pickle.models;

import com.example.pickle.interfaces.Visitable;
import com.example.pickle.interfaces.Visitor;

public class OrdersDetails implements Visitable {
    private String userId;
    private String itemId;
    private String itemThumbImage;
    private int itemQty;
    private int itemBasePrice;
    private String itemCategory;
    private String address;
    private String deliveryTime;

    public transient boolean isPastOrder;
    public transient String orderId;
    public transient int status;

    public OrdersDetails(String itemId, String itemThumbImage, int itemQty, int itemBasePrice, String itemCategory, String address, String deliveryTime) {
        this.itemId = itemId;
        this.itemThumbImage = itemThumbImage;
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

    public String getItemThumbImage() {
        return itemThumbImage;
    }

    public void setItemThumbImage(String itemThumbImage) {
        this.itemThumbImage = itemThumbImage;
    }

    public Integer getItemQty() {
        return itemQty;
    }

    @Override
    public String toString() {
        return "OrdersDetails{" +
                "userId='" + userId + '\'' +
                ", itemId='" + itemId + '\'' +
                ", date='" + itemThumbImage + '\'' +
                ", itemQty=" + itemQty +
                ", itemBasePrice=" + itemBasePrice +
                ", itemCategory='" + itemCategory + '\'' +
                ", address='" + address + '\'' +
                ", deliveryTime='" + deliveryTime + '\'' +
                '}';
    }

    @Override
    public int accept(Visitor visitor) {
        return visitor.type(this);
    }
}
