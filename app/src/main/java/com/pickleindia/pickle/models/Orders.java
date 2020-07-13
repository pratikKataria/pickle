package com.pickleindia.pickle.models;

import com.pickleindia.pickle.interfaces.Visitable;
import com.pickleindia.pickle.interfaces.Visitor;

import java.util.ArrayList;

public class Orders implements Visitable {
    private long date;
    private String orderDetailsIds;
    private String orderId;
    private int orderStatus;
    private int pCoinsSpent;
    private String userId;

    public boolean isPastOrder;
    public int totalProduct;

    public Orders(long date, String orderDetailsIds, String orderId, int orderStatus, int pCoinsSpent, String userId) {
        this.date = date;
        this.orderDetailsIds = orderDetailsIds;
        this.orderId = orderId;
        this.orderStatus = orderStatus;
        this.pCoinsSpent = pCoinsSpent;
        this.userId = userId;
    }

    public Orders() {}

    public long getDate() {
        return date;
    }

    public void setDate(long date) {
        this.date = date;
    }

    public String getOrderDetailsIds() {
        return orderDetailsIds;
    }

    public void setOrderDetailsIds(String orderDetailsIds) {
        this.orderDetailsIds = orderDetailsIds;
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

    public int getpCoinsSpent() {
        return pCoinsSpent;
    }

    public void setpCoinsSpent(int pCoinsSpent) {
        this.pCoinsSpent = pCoinsSpent;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    @Override
    public int accept(Visitor visitor) {
        return visitor.type(this);
    }
}
