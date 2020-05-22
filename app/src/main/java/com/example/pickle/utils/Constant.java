package com.example.pickle.utils;

import androidx.annotation.IntDef;
import androidx.annotation.StringDef;

import com.example.pickle.R;
import com.google.firebase.auth.FirebaseAuth;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * created by pratik katariya
 * 19 / 05 /2020
 */
public abstract class Constant {

    @Retention(RetentionPolicy.SOURCE)
    @StringDef({
            PRODUCT,
            FRUITS,
            VEGETABLES,
            DAIRY,
            BEVERAGES,
            PRODUCT_BUNDLE,
            ORDERS_CANCELLED,
            ORDERS_DELIVERED
    })

    public @interface IConstant {}

    public static final String ROOT = "pickle-8bac6";
    public static final String PRODUCT = "Products";
    public static final String FRUITS = "Fruits";
    public static final String VEGETABLES = "Vegetables";
    public static final String DAIRY = "Dairy";
    public static final String BEVERAGES = "Beverages";
    public static final String ORDERS = "Orders";
    public static final String ORDERS_DETAILS = "OrdersDetails";
    public static final String ORDERS_CANCELLED ="OrdersCancelled";
    public static final String ORDERS_DELIVERED ="OrdersDelivered";
    public static final String FIREBASE_AUTH_ID = FirebaseAuth.getInstance().getUid();

    public static final String PRODUCT_BUNDLE = "productsInfo";

    @IntDef({
          FRUIT_IMG_RES,
          VEGETABLE_IMG_RES,
          BEVERAGE_IMG_RES,
          DAIRY_IMG_RES
    })

    public @interface ImageResourceInterface {}

    public static final int FRUIT_IMG_RES = R.drawable.ic_fruits;
    public static final int VEGETABLE_IMG_RES = R.drawable.ic_vegetable;
    public static final int BEVERAGE_IMG_RES = R.drawable.ic_beverages;
    public static final int DAIRY_IMG_RES = R.drawable.ic_dairy;

}
