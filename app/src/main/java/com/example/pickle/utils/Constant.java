package com.example.pickle.utils;

import androidx.annotation.IntDef;
import androidx.annotation.StringDef;

import com.example.pickle.R;

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
            BEVERAGES
    })

    public @interface IConstant {}

    public static final String PRODUCT = "Products";
    public static final String FRUITS = "Fruits";
    public static final String VEGETABLES = "Vegetables";
    public static final String DAIRY = "Dairy";
    public static final String BEVERAGES = "Beverages";


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