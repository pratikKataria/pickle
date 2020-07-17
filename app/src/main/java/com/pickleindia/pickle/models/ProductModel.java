package com.pickleindia.pickle.models;

import android.util.Log;

import androidx.annotation.Nullable;

import com.google.firebase.database.Exclude;
import com.google.firebase.database.annotations.NotNull;

public class ProductModel {
    private String itemName;
    private String itemDesc;
    private int itemBasePrice;
    private int itemSellPrice;
    private int itemMaxQtyPerUser;
    private int itemQty;

    private String qtyType;
    private String itemType;
    private String itemCategory;

    private String itemId;
    private int itemUnits;
    private long date;
    private boolean itemAvailability;

    private String itemThumbImage;
    private String itemName_itemId;

    private int quantityCounter;

    public ProductModel() {
    }

    public ProductModel(String itemName, int itemBasePrice) {
        this.itemName = itemName;
        this.itemBasePrice = itemBasePrice;
    }

    public ProductModel(String itemName, String itemDesc, int itemBasePrice, int itemSellPrice, int itemMaxQtyPerUser, int itemQty, String qtyType, String itemType, String itemCategory, String itemId, int itemUnits, long date, boolean itemAvailability, String itemThumbImage, String itemName_itemId) {
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
        this.itemThumbImage = itemThumbImage;
        this.itemName_itemId = itemName_itemId;
    }

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

    public int getItemBasePrice() {
        return itemBasePrice;
    }

    public void setItemBasePrice(int itemBasePrice) {
        this.itemBasePrice = itemBasePrice;
    }

    public int getItemSellPrice() {
        return itemSellPrice;
    }

    public void setItemSellPrice(int itemSellPrice) {
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
}
