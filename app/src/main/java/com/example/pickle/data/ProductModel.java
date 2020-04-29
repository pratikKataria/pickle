package com.example.pickle.data;

import android.util.Log;

import androidx.annotation.Nullable;

import java.io.Serializable;
import java.util.Date;

public class ProductModel {
    String itemName;
    String itemDesc;
    int itemBasePrice;
    int itemSellPrice;
    int itemMaxQtyPerUser;
    String itemOffers;
    int itemQty;

    String qtyType;
    String itemType;
    String itemCategory;

    String itemId;
    int itemUnits;
    Date date;
    boolean itemAvailability;

    String itemImageUrl;
    String itemThumbImage;

    int quantityCounter;

    public ProductModel() {}

    public ProductModel(String itemName, int itemBasePrice) {
        this.itemName = itemName;
        this.itemBasePrice = itemBasePrice;
    }

    public ProductModel(String itemName, String itemDesc, int itemBasePrice, int itemSellPrice, int itemMaxQtyPerUser, String itemOffers, int itemQty, String qtyType, String itemType, String itemCategory, String itemId, int itemUnits, Date date, boolean itemAvailability, String itemImageUrl, String itemThumbImage) {
        this.itemName = itemName;
        this.itemDesc = itemDesc;
        this.itemBasePrice = itemBasePrice;
        this.itemSellPrice = itemSellPrice;
        this.itemMaxQtyPerUser = itemMaxQtyPerUser;
        this.itemOffers = itemOffers;
        this.itemQty = itemQty;
        this.qtyType = qtyType;
        this.itemType = itemType;
        this.itemCategory = itemCategory;
        this.itemId = itemId;
        this.itemUnits = itemUnits;
        this.date = date;
        this.itemAvailability = itemAvailability;
        this.itemImageUrl = itemImageUrl;
        this.itemThumbImage = itemThumbImage;
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

    public String getItemOffers() {
        return itemOffers;
    }

    public void setItemOffers(String itemOffers) {
        this.itemOffers = itemOffers;
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

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public boolean isItemAvailability() {
        return itemAvailability;
    }

    public void setItemAvailability(boolean itemAvailability) {
        this.itemAvailability = itemAvailability;
    }

    public String getItemImageUrl() {
        return itemImageUrl;
    }

    public void setItemImageUrl(String itemImageUrl) {
        this.itemImageUrl = itemImageUrl;
    }

    public String getItemThumbImage() {
        return itemThumbImage;
    }

    public void setItemThumbImage(String itemThumbImage) {
        this.itemThumbImage = itemThumbImage;
    }

    public int getQuantityCounter() {
        return quantityCounter;
    }

    public void setQuantityCounter(int quantityCounter) {
        this.quantityCounter = quantityCounter;
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        ProductModel productModel = (ProductModel) obj;
        Log.e("Product Model ", "on equals invoked ");
        return itemId.matches(productModel.getItemId());
    }

    @Override
    public String toString() {
        return "ProductModel{" +
                "itemName='" + itemName + '\'' +
                ", itemDesc='" + itemDesc + '\'' +
                ", itemBasePrice=" + itemBasePrice +
                ", itemSellPrice=" + itemSellPrice +
                ", itemMaxQtyPerUser=" + itemMaxQtyPerUser +
                ", itemOffers='" + itemOffers + '\'' +
                ", itemQty=" + itemQty +
                ", qtyType='" + qtyType + '\'' +
                ", itemType='" + itemType + '\'' +
                ", itemCategory='" + itemCategory + '\'' +
                ", itemId='" + itemId + '\'' +
                ", itemUnits=" + itemUnits +
                ", date=" + date +
                ", itemAvailability=" + itemAvailability +
                ", itemImageUrl='" + itemImageUrl + '\'' +
                ", itemThumbImage='" + itemThumbImage + '\'' +
                ", quantityCounter=" + quantityCounter +
                '}';
    }
}
