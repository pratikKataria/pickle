package com.example.pickle.models;

import android.widget.ImageView;

import androidx.annotation.DrawableRes;

import com.example.pickle.R;
import com.example.pickle.interfaces.Visitable;
import com.example.pickle.interfaces.Visitor;

public class EmptyState implements Visitable {

    @DrawableRes
    private int backgroundRes = R.drawable.crd_empty_order_bg;

    @DrawableRes
    private int bgImageRes = R.drawable.empty_cart_img;

    public EmptyState() { }

    public EmptyState(int backgroundRes, int bgImageRes) {
        this.backgroundRes = backgroundRes;
        this.bgImageRes = bgImageRes;
    }

    public int getBackgroundRes() {
        return backgroundRes;
    }

    public int getBgImageRes() {
        return bgImageRes;
    }

    @Override
    public int accept(Visitor visitor) {
        return visitor.type(this);
    }
}
