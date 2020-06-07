package com.example.pickle.cart;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.provider.Settings;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;

import com.example.pickle.BR;
import com.example.pickle.LocationTracker;
import com.example.pickle.interfaces.INavigation;
import com.example.pickle.models.Address;
import com.example.pickle.models.ProductModel;
import com.example.pickle.utils.PermissionUtils;
import com.example.pickle.utils.PriceFormatUtils;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class CartViewModel extends BaseObservable {
    private List<ProductModel> cartProducts = new ArrayList<>();
    private boolean isCartVisible;
    private boolean currentLocationFound;
    private String address;
    private String deliveryTime;
    DatabaseReference reference;

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
    public void setAddress(String address) {
        this.address = address;
        Log.e(CartActivity.class.getName(), "address: " + address);
        notifyPropertyChanged(BR.address);
    }

    @Bindable
    public String getAddress() {
        return address;
    }

    @Bindable
    public boolean isCartVisible() {
        return isCartVisible;
    }

    @Bindable
    public void  setCurrentLocationFound(boolean currentLocationFound) {
        this.currentLocationFound = currentLocationFound;
        Log.e(CartViewModel.class.getName(), "current found " + currentLocationFound);
        notifyPropertyChanged(BR.currentLocationFound);
    }

    @Bindable
    public boolean isCurrentLocationFound() {
        return currentLocationFound;
    }

    @Bindable
    public void setDeliveryTime(String deliveryTime) {
        this.deliveryTime = deliveryTime;
        notifyPropertyChanged(BR.deliveryTime);
    }

    @Bindable
    public String getDeliveryTime() {return deliveryTime;}

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

    public void navigateTo(Context context, String navigateTo) {
        INavigation iNavigation = (INavigation) context;
        iNavigation.navigateTo(navigateTo);
    }

    public void getDatabaseAddress() {
        Log.e(CartViewModel.class.getName(), "load address ");
        if (FirebaseAuth.getInstance().getUid() != null) {
            DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Addresses/" + FirebaseAuth.getInstance().getUid());
            reference.keepSynced(true);
            reference.child("slot1").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    Address newAddress = dataSnapshot.getValue(Address.class);
                    if (newAddress != null)
                        setAddress(newAddress.toString());
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    setAddress(databaseError.getMessage());
                }
            });
        }
    }

    public void getCurrentLocation(Context context) {
        boolean hasPerm = PermissionUtils.hasPermission((Activity) context, Manifest.permission.ACCESS_FINE_LOCATION) && PermissionUtils.hasPermission((Activity) context, Manifest.permission.ACCESS_COARSE_LOCATION);
        if (!hasPerm) {
            Log.e("has perm ", "else ");
            if (!PermissionUtils.shouldShowRational((Activity)context, Manifest.permission.ACCESS_FINE_LOCATION)) {
                Log.e("rational ", "else ");
                showMessageOKCancel(
                        "These permissions are mandatory for the application. Please allow access.",
                         (dialog, which) -> {
                             Intent intent = new Intent();
                             intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                             Uri uri = Uri.fromParts("package", context.getPackageName(), null);
                             intent.setData(uri);
                             context.startActivity(intent);
                         }
                        , context);
            }
        } else {
            Log.e("else ", "else ");
            location_setup(context);
        }
    }

    private void showMessageOKCancel(String message, DialogInterface.OnClickListener okListener, Context context) {
        new AlertDialog.Builder(context)
                .setMessage(message)
                .setPositiveButton("OK", okListener)
                .setNegativeButton("Cancel", null)
                .create()
                .show();
    }

    private void location_setup(Context context) {

        LocationTracker locationTrack = new LocationTracker(context);

        if (locationTrack.canGetLocation()) {

            double longitude = locationTrack.getLongitude();
            double latitude = locationTrack.getLatitude();

            //Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("geo:"+ latitude +"," + longitude));
            //intent.setPackage("com.google.android.apps.maps");
            Log.e("Location manager", "Location manage");
            if (latitude > 0 && longitude> 0) {
                Log.e("Location manager", "Location manage");
                setAddress(latitude +","+longitude);
                setCurrentLocationFound(true);
            }
        }else  {
            locationTrack.showSettingsAlert();
        }
    }
}
