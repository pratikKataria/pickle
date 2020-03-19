package com.example.pickle;

import android.graphics.Rect;
import android.view.View;

import androidx.recyclerview.widget.RecyclerView;

public class SpacesItemDecoration extends RecyclerView.ItemDecoration {
    private int space;
    private int layoutType;

    public SpacesItemDecoration(int space) {
        this.space = space;
    }

    public SpacesItemDecoration(int space, int layoutType) {
        this.space = space;
        this.layoutType = layoutType;
    }

    @Override
    public void getItemOffsets(Rect outRect, View view,
                               RecyclerView parent, RecyclerView.State state) {
        outRect.left = space+5;
        outRect.right = space;
        outRect.bottom = space;

        if (layoutType != 0) {
            if (parent.getChildLayoutPosition(view) == 0) {
                outRect.left = space;
            } else {
                outRect.left = 0;
            }

        }

        // Add top margin only for the first item to avoid double space between items
        if (parent.getChildLayoutPosition(view) == 0) {
            outRect.top = space;
        } else {
            outRect.top = space;
        }
    }
}
