package com.example.pickle.utils;

import androidx.annotation.StringDef;

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



}
