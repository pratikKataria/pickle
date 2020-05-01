package com.example.pickle.ui;

import android.app.Activity;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import com.example.pickle.activity.main.options.CartViewActivity;
import com.google.android.material.bottomsheet.BottomSheetBehavior;

public class ConstraintLayoutTouchListener implements View.OnTouchListener {

    static final String logTag = "ActivitySwipeDetector";
    private Activity activity;
    static final int MIN_DISTANCE = 100;// TODO change this runtime based on screen resolution. for 1920x1080 is to small the 100 distance
    private float downX, downY, upX, upY;
    BottomSheetBehavior _bottomSheetBehavior;

    // private MainActivity mMainActivity;

    public ConstraintLayoutTouchListener(CartViewActivity mainActivity, BottomSheetBehavior _bottomSheetBehavior) {
        activity = mainActivity;
        this._bottomSheetBehavior = _bottomSheetBehavior;
    }

    public void onRightToLeftSwipe() {
        _bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
    }

    public void onLeftToRightSwipe() {
        _bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
    }

    public void onTopToBottomSwipe() {
        _bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
    }

    public void onBottomToTopSwipe() {
        _bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
    }

    public boolean onTouch(View v, MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN: {
                downX = event.getX();
                downY = event.getY();
                return true;
            }
            case MotionEvent.ACTION_UP: {
                upX = event.getX();
                upY = event.getY();

                float deltaX = downX - upX;
                float deltaY = downY - upY;

                // swipe horizontal?
                if (Math.abs(deltaX) > MIN_DISTANCE) {
                    // left or right
                    if (deltaX < 0) {
                        this.onLeftToRightSwipe();
                        return true;
                    }
                    if (deltaX > 0) {
                        this.onRightToLeftSwipe();
                        return true;
                    }
                } else {
                    Log.i(logTag, "Swipe was only " + Math.abs(deltaX) + " long horizontally, need at least " + MIN_DISTANCE);
                    // return false; // We don't consume the event
                }

                // swipe vertical?
                if (Math.abs(deltaY) > MIN_DISTANCE) {
                    // top or down
                    if (deltaY < 0) {
                        this.onTopToBottomSwipe();
                        return true;
                    }
                    if (deltaY > 0) {
                        this.onBottomToTopSwipe();
                        return true;
                    }
                } else {
                    Log.i(logTag, "Swipe was only " + Math.abs(deltaX) + " long vertically, need at least " + MIN_DISTANCE);
                    // return false; // We don't consume the event
                }

                return false; // no swipe horizontally and no swipe vertically
            }// case MotionEvent.ACTION_UP:
        }
        return true;
    }

}