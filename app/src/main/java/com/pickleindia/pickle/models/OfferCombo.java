package com.pickleindia.pickle.models;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.Nullable;

import java.io.Serializable;

public class OfferCombo implements Parcelable {
    private String comboId;
    private String comboThumb;
    private String productIds_cat;
    private int totalPrice;
    private int maxQty;

    public int qtyCounter;

    public OfferCombo() {}

    public OfferCombo(String comboId, String comboThumb, String productIds_cat, int totalPrice, int maxQty) {
        this.comboId = comboId;
        this.comboThumb = comboThumb;
        this.productIds_cat = productIds_cat;
        this.totalPrice = totalPrice;
        this.maxQty = maxQty;
    }

    protected OfferCombo(Parcel in) {
        comboId = in.readString();
        comboThumb = in.readString();
        productIds_cat = in.readString();
        totalPrice = in.readInt();
        maxQty = in.readInt();
        qtyCounter = in.readInt();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(comboId);
        dest.writeString(comboThumb);
        dest.writeString(productIds_cat);
        dest.writeInt(totalPrice);
        dest.writeInt(maxQty);
        dest.writeInt(qtyCounter);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<OfferCombo> CREATOR = new Creator<OfferCombo>() {
        @Override
        public OfferCombo createFromParcel(Parcel in) {
            return new OfferCombo(in);
        }

        @Override
        public OfferCombo[] newArray(int size) {
            return new OfferCombo[size];
        }
    };

    public String getComboId() {
        return comboId;
    }

    public void setComboId(String comboId) {
        this.comboId = comboId;
    }

    public String getComboThumb() {
        return comboThumb;
    }

    public void setComboThumb(String comboThumb) {
        this.comboThumb = comboThumb;
    }

    public String getProductIds_cat() {
        return productIds_cat;
    }

    public void setProductIds_cat(String productIds_cat) {
        this.productIds_cat = productIds_cat;
    }

    public int getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(int totalPrice) {
        this.totalPrice = totalPrice;
    }

    public int getMaxQty() {
        return maxQty;
    }

    public void setMaxQty(int maxQty) {
        this.maxQty = maxQty;
    }
}
