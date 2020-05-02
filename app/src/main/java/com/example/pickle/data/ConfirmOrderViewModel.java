package com.example.pickle.data;

import android.widget.TextView;

import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;

import com.example.pickle.BR;

public class ConfirmOrderViewModel extends BaseObservable {
    private boolean showAnimation;
    private int totalPrice;
    private String quantity;

    @Bindable
    public boolean isShowAnimation() {
        return showAnimation;
    }

    @Bindable
    public void setShowAnimation(boolean showAnimation) {
        this.showAnimation = showAnimation;
        notifyPropertyChanged(BR.showAnimation);
    }

    @Bindable
    public int getTotalPrice() {
        return totalPrice;
    }

    @Bindable
    public void setTotalPrice(int totalPrice) {
        this.totalPrice = totalPrice;
        notifyPropertyChanged(BR.showAnimation);
    }

    @Bindable
    public String getQuantity() {
        return quantity;
    }

    @Bindable
    public void setQuantity(String quantity) {
        this.quantity = quantity;
        notifyPropertyChanged(BR.showAnimation);
    }
}
