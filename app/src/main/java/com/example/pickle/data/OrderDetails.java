package com.example.pickle.data;

public class OrderDetails {
    private String userId;
    private String itemId;
    private int orderStatus;

    public OrderDetails() {}

    public OrderDetails(String userId, String itemId, int orderStatus) {
        this.userId = userId;
        this.itemId = itemId;
        this.orderStatus = orderStatus;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getItemId() {
        return itemId;
    }

    public void setItemId(String itemId) {
        this.itemId = itemId;
    }

    public int getOrderStatus() {
        return orderStatus;
    }

    public void setOrderStatus(int orderStatus) {
        this.orderStatus = orderStatus;
    }
}
