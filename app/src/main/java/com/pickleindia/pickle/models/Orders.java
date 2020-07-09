package com.pickleindia.pickle.models;

public class Orders {
    private String userId;
    private String orderId;
    private int orderStatus;
    private long date;

    public Orders() {}


    public Orders(String userId, String orderId, int orderStatus, long date) {
        this.userId = userId;
        this.orderId = orderId;
        this.orderStatus = orderStatus;
        this.date = date;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public int getOrderStatus() {
        return orderStatus;
    }

    public void setOrderStatus(int orderStatus) {
        this.orderStatus = orderStatus;
    }

    public long getDate() {
        return date;
    }

    public void setDate(long date) {
        this.date = date;
    }

    @Override
    public String toString() {
        return "Orders{" +
                "userId='" + userId + '\'' +
                ", orderId='" + orderId + '\'' +
                ", orderStatus=" + orderStatus +
                ", date=" + date +
                '}';
    }
}
