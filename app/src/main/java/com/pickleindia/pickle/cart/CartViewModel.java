package com.pickleindia.pickle.cart;

import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;

import com.pickleindia.pickle.BR;
import com.pickleindia.pickle.models.ProductModel;
import com.pickleindia.pickle.utils.PriceFormatUtils;

import java.util.ArrayList;
import java.util.List;


public class CartViewModel extends BaseObservable {
    private List<ProductModel> cartProducts = new ArrayList<>();
    private boolean isCartVisible;
    private double comboPriceAddon;

    @Bindable
    public List<ProductModel> getCartProducts() {
        return cartProducts;
    }

    @Bindable
    public void setCartProducts(List<ProductModel> cartProducts) {
        this.cartProducts = cartProducts;
        notifyPropertyChanged(BR.cartProducts);
    }

    @Bindable
    public void setCartVisible(boolean cartVisible) {
        isCartVisible = cartVisible;
        notifyPropertyChanged(BR.cartVisible);
    }

    @Bindable
    public boolean getCartVisible() {
        return isCartVisible;
    }


    @Bindable
    public boolean isCartVisible() {
        return isCartVisible;
    }


    public String getProductQuantitiesString() {
        int totalItems = 0;

        for (ProductModel product : cartProducts) {
            totalItems += 1;
        }

        String s;
        if (totalItems > 1) {
            s = "items";
        } else {
            s = "item";
        }

        if (totalItems > 0)
            setCartVisible(true);
        else
            setCartVisible(false);

        return ("(" + totalItems + " " + s + ")");
    }

    public String getTotalCostString() {
        double totalCost = 0;

        for (ProductModel product : cartProducts) {
            int productQuantity = product.getQuantityCounter();
            double cost;
            if (product.getItemSellPrice() > 0) {
                cost = productQuantity * product.getItemSellPrice();
            } else {
                cost = productQuantity * product.getItemBasePrice();
            }
            totalCost += cost;
        }

        if (comboPriceAddon > 0) {
            return PriceFormatUtils.getStringFormattedPrice(totalCost) + " + " + PriceFormatUtils.getStringFormattedPrice(comboPriceAddon) + " = \u20b9" + (totalCost + comboPriceAddon);
        }

        return PriceFormatUtils.getStringFormattedPrice(totalCost);
    }

    public double getTotalCostInt() {
        double totalCost = 0;

        for (ProductModel product : cartProducts) {
            int productQuantity = product.getQuantityCounter();
            double cost;
            if (product.getItemSellPrice() > 0) {
                cost = productQuantity * product.getItemSellPrice();
            } else {
                cost = productQuantity * product.getItemBasePrice();
            }
            totalCost += cost;
        }
        return totalCost;
    }

    public double getComboValue() {
        return comboPriceAddon;
    }

    public void setComboValue(double comboPriceAddon) {
        this.comboPriceAddon = comboPriceAddon;
    }
}
