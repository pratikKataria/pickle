package com.example.pickle.data;

import android.content.Context;

import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;

import com.example.pickle.BR;
import com.example.pickle.binding.INavigation;
import com.example.pickle.binding.NavigationAction;
import com.example.pickle.utils.PriceFormatUtils;

import java.util.ArrayList;
import java.util.List;

public class CartViewModel extends BaseObservable {
    List<ProductModel> cartProducts = new ArrayList<>();
    private boolean isCartVisible;

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
    public boolean isCartVisible() {
        return isCartVisible;
    }

    @Bindable
    public void setCartVisible(boolean cartVisible) {
        isCartVisible = cartVisible;
        notifyPropertyChanged(BR.cartVisible);
    }

    public String getProductQuantitiesString() {
        int totalItems = 0;

        for (ProductModel product : cartProducts) {
            totalItems += product.getQuantityCounter();
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

        return ("(" + String.valueOf(totalItems) + " " + s + ")");

    }

    public String getTotalCostString() {
        int totalCost = 0;

        for(ProductModel product : cartProducts) {
            int productQuantity = product.getQuantityCounter();
            int cost;
            if (product.getItemSellPrice() > 0) {
               cost = productQuantity * product.getItemSellPrice();
            } else {
                cost = productQuantity * product.getItemBasePrice();
            }
            totalCost += cost;
        }
        return PriceFormatUtils.getStringFormattedPrice(totalCost);
    }

    public void navigateTo(Context context, int navigate) {
        INavigation iNavigation = (INavigation) context;
        switch (navigate) {
            case NavigationAction.NAVIGATE_TO_FRUIT:
                iNavigation.navigateTo(NavigationAction.NAVIGATE_TO_FRUIT);
                break;
            case NavigationAction.NAVIGATE_TO_VEGETABLE:
                iNavigation.navigateTo(NavigationAction.NAVIGATE_TO_VEGETABLE);
                break;
            case NavigationAction.NAVIGATE_TO_BEVERAGES:
                iNavigation.navigateTo(NavigationAction.NAVIGATE_TO_BEVERAGES);
                break;
            case NavigationAction.NAVIGATE_TO_DAIRY:
                iNavigation.navigateTo(NavigationAction.NAVIGATE_TO_DAIRY);
                break;
        }
    }

    public void onHomePressed(Context context) {
        INavigation iNavigation = (INavigation) context;
        iNavigation.onHomePressed(true);
    }

}
