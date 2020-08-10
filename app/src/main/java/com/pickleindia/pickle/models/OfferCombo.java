package com.pickleindia.pickle.models;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.Nullable;

import com.google.firebase.database.PropertyName;

public class OfferCombo implements Parcelable {
    private String offerId;
    private String offerThumb;
    private String productIds_cat;
    private double totalPrice;
    private int maxQty;

    public int qtyCounter;
    private boolean isCombo;

    public OfferCombo() {}

    public OfferCombo(String offerId, String offerThumb, String productIds_cat, double totalPrice, int maxQty, boolean isCombo) {
        this.offerId = offerId;
        this.offerThumb = offerThumb;
        this.productIds_cat = productIds_cat;
        this.totalPrice = totalPrice;
        this.maxQty = maxQty;
        this.isCombo = isCombo;
    }

    protected OfferCombo(Parcel in) {
        offerId = in.readString();
        offerThumb = in.readString();
        productIds_cat = in.readString();
        totalPrice = in.readDouble();
        maxQty = in.readInt();
        qtyCounter = in.readInt();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(offerId);
        dest.writeString(offerThumb);
        dest.writeString(productIds_cat);
        dest.writeDouble(totalPrice);
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

    public String getOfferId() {
        return offerId;
    }

    public void setOfferId(String offerId) {
        this.offerId = offerId;
    }

    public String getOfferThumb() {
        return offerThumb;
    }

    public void setOfferThumb(String offerThumb) {
        this.offerThumb = offerThumb;
    }

    public String getProductIds_cat() {
        return productIds_cat;
    }

    public void setProductIds_cat(String productIds_cat) {
        this.productIds_cat = productIds_cat;
    }

    public double getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(double totalPrice) {
        this.totalPrice = totalPrice;
    }

    public int getMaxQty() {
        return maxQty;
    }

    public void setMaxQty(int maxQty) {
        this.maxQty = maxQty;
    }


    @Override
    public boolean equals(@Nullable Object obj) {
        if (obj instanceof OfferCombo) {
            OfferCombo offerCombo = (OfferCombo) obj;
            return offerCombo.getOfferId().matches(offerId);
        }

        return false;
    }

    @PropertyName("isCombo")
    public boolean isCombo() {
        return isCombo;
    }

    public void setIsCombo(boolean isCombo) {
        this.isCombo = isCombo;
    }

    @Override
    public String toString() {
        return "OfferCombo{" +
                "offerId='" + offerId + '\'' +
                ", offerThumb='" + offerThumb + '\'' +
                ", productIds_cat='" + productIds_cat + '\'' +
                ", totalPrice=" + totalPrice +
                ", maxQty=" + maxQty +
                ", qtyCounter=" + qtyCounter +
                ", isCombo=" + isCombo +
                '}';
    }
}
