package com.example.pickle.binding;

import androidx.annotation.IntDef;

import com.example.pickle.R;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

public abstract class NavigationAction {
    @Retention(RetentionPolicy.SOURCE)
    @IntDef({
            NAVIGATE_TO_VEGETABLE,
            NAVIGATE_TO_FRUIT,
            NAVIGATE_TO_BEVERAGES,
            NAVIGATE_TO_DAIRY
    })
    public @interface NavigationDestination { }

    public static final int NAVIGATE_TO_VEGETABLE = R.id.action_orderFragment_to_vegetableFragment;
    public static final int NAVIGATE_TO_FRUIT = R.id.action_orderFragment_to_fruitsFragment;
    public static final int NAVIGATE_TO_BEVERAGES = R.id.action_orderFragment_to_beveragesFragment;
    public static final int NAVIGATE_TO_DAIRY = R.id.action_orderFragment_to_dairyFragment;


    @NavigationDestination
    public abstract int getNavigationMode();

    public abstract void setNavigationMode(@NavigationDestination int mode);

}
