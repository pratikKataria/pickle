package com.pickleindia.pickle.models;

import androidx.annotation.Nullable;

import com.pickleindia.pickle.interfaces.Visitable;
import com.pickleindia.pickle.interfaces.Visitor;

public class Orders implements Visitable {
    private long date;
    private String orderDetailsIds;
    private String orderId;
    private int orderStatus;
    private String userId;
    private int subTotal;
    private int pcoinsSpent;
    private String paymentMethod;
    private String deliveryTime;
    private String address;
    private int shipping;

    public boolean isPastOrder;
    public int totalProduct;

    public Orders(long date, String orderDetailsIds, String orderId, int orderStatus, int pcoinsSpent, String userId, int subTotal, String paymentMethod, String deliveryTime, String address, int shipping) {
        this.date = date;
        this.orderDetailsIds = orderDetailsIds;
        this.orderId = orderId;
        this.orderStatus = orderStatus;
        this.pcoinsSpent = pcoinsSpent;
        this.userId = userId;
        this.subTotal = subTotal;
        this.paymentMethod = paymentMethod;
        this.deliveryTime = deliveryTime;
        this.address = address;
        this.shipping = shipping;
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

    public int getPcoinsSpent() {
        return pcoinsSpent;
    }

    public void setPcoinsSpent(int pcoinsSpent) {
        this.pcoinsSpent = pcoinsSpent;
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

    public int getSubTotal() {
        return subTotal;
    }

    public void setSubTotal(int subTotal) {
        this.subTotal = subTotal;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public String getDeliveryTime() {
        return deliveryTime;
    }

    public void setDeliveryTime(String deliveryTime) {
        this.deliveryTime = deliveryTime;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public int getShipping() {
        return shipping;
    }

    public void setShipping(int shipping) {
        this.shipping = shipping;
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        if (obj instanceof  Orders) {
            Orders orders = (Orders) obj;
            return orders.getOrderId().equals(orderId);
        }
        return false;
    }
}
