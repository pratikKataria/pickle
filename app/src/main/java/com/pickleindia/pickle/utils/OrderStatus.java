package com.pickleindia.pickle.utils;

import androidx.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

public abstract class OrderStatus {

    @Retention(RetentionPolicy.SOURCE)
    @IntDef({
            PROCESSING,
            ORDERED,
            DELIVERED,
            CANCEL
    })

    public @interface IOrderStatus { }

    public static final int PROCESSING = 243;
    public static final int ORDERED = 347;
    public static final int DELIVERED = 324;
    public static final int CANCEL = 398;
    public static final int DETAINED = 903;
}
