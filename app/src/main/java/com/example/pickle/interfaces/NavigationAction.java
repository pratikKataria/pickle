package com.example.pickle.interfaces;

import androidx.annotation.IntDef;

import com.example.pickle.R;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

public abstract class NavigationAction {
    @Retention(RetentionPolicy.SOURCE)
    @IntDef({
            NAVIGATE_TO_PRODUCTS,
    })
    public @interface NavigationDestination { }

    public static final int NAVIGATE_TO_PRODUCTS = R.id.action_homeFragment_to_productsFragment;


    @NavigationDestination
    public abstract int getNavigationMode();

    public abstract void setNavigationMode(@NavigationDestination int mode);

}
