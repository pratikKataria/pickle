package com.example.pickle.models;

import androidx.annotation.DrawableRes;

import com.example.pickle.R;
import com.example.pickle.interfaces.Visitable;
import com.example.pickle.interfaces.Visitor;

public class EmptyState implements Visitable {

    @DrawableRes
    private int backgroundRes = R.drawable.crd_empty_order_bg;

    @DrawableRes
    private int backgroundImageRes = R.drawable.empty_cart_img;

    private String heading = "Whoops";
    private String description = "its look like that no ongoing orders";

    public EmptyState() { }

    public EmptyState(int backgroundRes, int bgImageRes, String heading, String description) {
        this.backgroundRes = backgroundRes;
        this.backgroundImageRes = bgImageRes;
        this.heading = heading;
        this.description = description;
    }

    public int getBackgroundRes() {
        return backgroundRes;
    }

    public int getBackgroundImageRes() {
        return backgroundImageRes;
    }

    @Override
    public int accept(Visitor visitor) {
        return visitor.type(this);
    }

    public String getHeading() {
        return heading;
    }

    public String getDescription() {
        return description;
    }
}
