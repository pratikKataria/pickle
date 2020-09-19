package com.pickleindia.pickle.utils;

import androidx.annotation.IntDef;
import androidx.annotation.StringDef;

import com.pickleindia.pickle.R;

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
            CAT_ONE,
            CAT_TWO,
            CAT_THREE,
            CAT_FOUR,
            PRODUCT_TYPE,
            ORDERS_CANCELLED,
            ORDERS_DELIVERED
    })

    public @interface IConstant {
    }

    public static final String ROOT = "pickle-8bac6";
    public static final String PRODUCT = "Products";
    public static final String CAT_ONE = "Fruits & Vegetables";
    public static final String CAT_TWO = "Branded Food";
    public static final String CAT_THREE = "Dairy";
    public static final String CAT_FOUR = "Grocery";
    public static final String CAT_FIVE = "Households";
    public static final String CAT_SIX = "PersonalCare";

    public static final String ORDERS = "Orders";
    public static final String ORDERS_DETAILS = "OrdersDetails";
    public static final String ORDERS_CANCELLED = "OrdersCancelled";
    public static final String ORDERS_DELIVERED = "OrdersDelivered";

    public static final String PRODUCT_TYPE = "productsInfo";

    @Retention(RetentionPolicy.SOURCE)
    @IntDef({
            FRUIT_IMG_RES,
            VEGETABLE_IMG_RES,
            DAIRY_IMG_RES
    })

    public @interface ImageResourceInterface { }

    public static final int FRUIT_IMG_RES = R.drawable.ic_fruits;
    public static final int VEGETABLE_IMG_RES = R.drawable.ic_vegetable;
    public static final int DAIRY_IMG_RES = R.drawable.ic_dairy;

    @IntDef({
            ADD,
            REMOVE,
            MODIFIED
    })

    public @interface DatabaseOperation {
    }

    public static final int ADD    = 1;
    public static final int REMOVE = 2;
    public static final int MODIFIED = 3;

    // firebase loading state
    public static final byte LOADING = 2;
    public static final byte SUCCESS = 1;
    public static final byte FAILED = -1;

    public static final byte LIMIT = 3;
    public static final byte ONGOING_LIMIT = 40;

    public static final int APARTMENT= 1223;
    public static final int INDIVIDUAL = 2341;

    public static final String PERMISSION_PREFS_KEY = "permissions";

    public static String GPS_CORD_RE = "-?[1-9][0-9]*(\\.[0-9]+)?,\\s*-?[1-9][0-9]*(\\.[0-9]+)?";

    public static String OFFER_COMBO = "offer_combo";
}
