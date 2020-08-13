package com.pickleindia.pickle.models;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.Nullable;

import com.google.firebase.database.Exclude;
import com.google.firebase.database.annotations.NotNull;

import java.util.Date;

public class ProductModel implements Parcelable {
    private String itemName;
    private String itemDesc;
    private double itemBasePrice;
    private double itemSellPrice;
    private int itemMaxQtyPerUser;
    private int itemQty;

    private String qtyType;
    private String itemType;
    private String itemCategory;

    private String itemId;
    private int itemUnits;
    private long date;
    private boolean itemAvailability;
    private boolean hasSale;

    private String itemThumbImage;
    private String itemName_itemId;

    private int quantityCounter;

    public boolean isCombo;
    private int percentage;
    private String itemSubCategory;
    
    public long cartAddedDate = new Date().getTime();

    public ProductModel() {
    }

    public ProductModel(String itemName, int itemBasePrice) {
        this.itemName = itemName;
        this.itemBasePrice = itemBasePrice;
    }

    public ProductModel(String itemName, String itemDesc, double itemBasePrice, double itemSellPrice, int itemMaxQtyPerUser, int itemQty, String qtyType, String itemType, String itemCategory, String itemId, int itemUnits, long date, boolean itemAvailability, String itemThumbImage, String itemName_itemId, boolean hasSale, String itemSubCategory) {
        this.itemName = itemName;
        this.itemDesc = itemDesc;
        this.itemBasePrice = itemBasePrice;
        this.itemSellPrice = itemSellPrice;
        this.itemMaxQtyPerUser = itemMaxQtyPerUser;
        this.itemQty = itemQty;
        this.qtyType = qtyType;
        this.itemType = itemType;
        this.itemCategory = itemCategory;
        this.itemId = itemId;
        this.itemUnits = itemUnits;
        this.date = date;
        this.itemAvailability = itemAvailability;
        this.hasSale = hasSale;
        this.itemThumbImage = itemThumbImage;
        this.itemName_itemId = itemName_itemId;
        this.itemSubCategory = itemSubCategory;
    }

    protected ProductModel(Parcel in) {
        itemName = in.readString();
        itemDesc = in.readString();
        itemBasePrice = in.readDouble();
        itemSellPrice = in.readDouble();
        itemMaxQtyPerUser = in.readInt();
        itemQty = in.readInt();
        qtyType = in.readString();
        itemType = in.readString();
        itemCategory = in.readString();
        itemId = in.readString();
        itemUnits = in.readInt();
        date = in.readLong();
        itemAvailability = in.readByte() != 0;
        itemThumbImage = in.readString();
        itemName_itemId = in.readString();
        quantityCounter = in.readInt();
        isCombo = in.readByte() != 0;
    }

    public static final Creator<ProductModel> CREATOR = new Creator<ProductModel>() {
        @Override
        public ProductModel createFromParcel(Parcel in) {
            return new ProductModel(in);
        }

        @Override
        public ProductModel[] newArray(int size) {
            return new ProductModel[size];
        }
    };

    public boolean showAddButton() {
        return quantityCounter > 0;
    }

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public String getItemDesc() {
        return itemDesc;
    }

    public void setItemDesc(String itemDesc) {
        this.itemDesc = itemDesc;
    }

    public double getItemBasePrice() {
        return itemBasePrice;
    }

    public void setItemBasePrice(double itemBasePrice) {
        this.itemBasePrice = itemBasePrice;
    }

    public double getItemSellPrice() {
        return itemSellPrice;
    }

    public void setItemSellPrice(double itemSellPrice) {
        this.itemSellPrice = itemSellPrice;
    }

    public int getItemMaxQtyPerUser() {
        return itemMaxQtyPerUser;
    }

    public void setItemMaxQtyPerUser(int itemMaxQtyPerUser) {
        this.itemMaxQtyPerUser = itemMaxQtyPerUser;
    }


    public int getItemQty() {
        return itemQty;
    }

    public void setItemQty(int itemQty) {
        this.itemQty = itemQty;
    }

    public String getQtyType() {
        return qtyType;
    }

    public void setQtyType(String qtyType) {
        this.qtyType = qtyType;
    }

    public String getItemType() {
        return itemType;
    }

    public void setItemType(String itemType) {
        this.itemType = itemType;
    }

    public String getItemCategory() {
        return itemCategory;
    }

    public void setItemCategory(String itemCategory) {
        this.itemCategory = itemCategory;
    }

    public String getItemId() {
        return itemId;
    }

    public void setItemId(String itemId) {
        this.itemId = itemId;
    }

    public int getItemUnits() {
        return itemUnits;
    }

    public void setItemUnits(int itemUnits) {
        this.itemUnits = itemUnits;
    }

    public long getDate() {
        return date;
    }

    public void setDate(long date) {
        this.date = date;
    }

    public boolean isItemAvailability() {
        return itemAvailability;
    }

    public void setItemAvailability(boolean itemAvailability) {
        this.itemAvailability = itemAvailability;
    }


    public String getItemThumbImage() {
        return itemThumbImage;
    }

    public void setItemThumbImage(String itemThumbImage) {
        this.itemThumbImage = itemThumbImage;
    }

    @Exclude
    public int getQuantityCounter() {
        return quantityCounter;
    }

    @Exclude
    public void setQuantityCounter(int quantityCounter) {
        this.quantityCounter = quantityCounter;
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        if (obj instanceof ProductModel) {
            ProductModel productModel = (ProductModel) obj;
            return itemId.matches(productModel.getItemId());
        } else return false;
    }

    @Override
    public String toString() {
        return "ProductModel{" +
                "itemName='" + itemName + '\'' +
                ", itemDesc='" + itemDesc + '\'' +
                ", itemBasePrice=" + itemBasePrice +
                ", itemSellPrice=" + itemSellPrice +
                ", itemMaxQtyPerUser=" + itemMaxQtyPerUser +
                ", itemQty=" + itemQty +
                ", qtyType='" + qtyType + '\'' +
                ", itemType='" + itemType + '\'' +
                ", itemCategory='" + itemCategory + '\'' +
                ", itemId='" + itemId + '\'' +
                ", itemUnits=" + itemUnits +
                ", date=" + date +
                ", itemAvailability=" + itemAvailability +
                ", itemThumbImage='" + itemThumbImage + '\'' +
                ", quantityCounter=" + quantityCounter +
                '}';
    }

    public String getItemName_itemId() {
        return itemName_itemId;
    }

    public void setItemName_itemId(String itemName_itemId) {
        this.itemName_itemId = itemName_itemId;
    }

    public boolean isPriceSame(@NotNull ProductModel productModel) {
        boolean same = true;
        if (productModel.getItemId().equals(itemId)) {
            if (productModel.getItemBasePrice() != itemBasePrice || productModel.getItemSellPrice() != itemSellPrice)
                same = false;
        }

        return same;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(itemName);
        dest.writeString(itemDesc);
        dest.writeDouble(itemBasePrice);
        dest.writeDouble(itemSellPrice);
        dest.writeInt(itemMaxQtyPerUser);
        dest.writeInt(itemQty);
        dest.writeString(qtyType);
        dest.writeString(itemType);
        dest.writeString(itemCategory);
        dest.writeString(itemId);
        dest.writeInt(itemUnits);
        dest.writeLong(date);
        dest.writeByte((byte) (itemAvailability ? 1 : 0));
        dest.writeString(itemThumbImage);
        dest.writeString(itemName_itemId);
        dest.writeInt(quantityCounter);
        dest.writeByte((byte) (isCombo ? 1 : 0));
    }

    public boolean isHasSale() {
        return hasSale;
    }

    public void setHasSale(boolean hasSale) {
        this.hasSale = hasSale;
    }

    public int getPercentage() {
        if (hasSale && itemBasePrice > 0) {
            double floatPercentage = (itemBasePrice - itemSellPrice) / (float)itemBasePrice;
            return percentage = (int) (floatPercentage*100);
        }
        return 0;
    }

    public String getItemSubCategory() {
        return itemSubCategory;
    }

    public void setItemSubCategory(String itemSubCategory) {
        this.itemSubCategory = itemSubCategory;
    }
}
