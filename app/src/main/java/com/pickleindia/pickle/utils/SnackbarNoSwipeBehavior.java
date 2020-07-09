package com.pickleindia.pickle.utils;

import android.view.View;

import com.google.android.material.snackbar.BaseTransientBottomBar;

public class SnackbarNoSwipeBehavior extends BaseTransientBottomBar.Behavior {

    @Override
    public boolean canSwipeDismissView(View child) {
        return false;
    }
}
