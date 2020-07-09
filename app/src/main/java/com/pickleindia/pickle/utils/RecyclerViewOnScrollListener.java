package com.pickleindia.pickle.utils;

import android.annotation.SuppressLint;
import android.util.Log;
import android.widget.AbsListView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class RecyclerViewOnScrollListener extends RecyclerView.OnScrollListener {

    boolean isScrooling;

    public RecyclerViewOnScrollListener() { }

    @Override
    public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
        super.onScrollStateChanged(recyclerView, newState);
        if (newState == AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL) {
            isScrooling = true;
        }
    }

    @SuppressLint("LongLogTag")
    @Override
    public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
        super.onScrolled(recyclerView, dx, dy);
        Log.e("RecyclerViewOnScrollListener", dx + " " + dy);
        LinearLayoutManager layoutManager = ((LinearLayoutManager) recyclerView.getLayoutManager());
        if (layoutManager != null) {
            int firstVisibleProductPosition = layoutManager.findFirstVisibleItemPosition();
            int visibleProductCount = layoutManager.getChildCount();
            int totalProductCount = layoutManager.getItemCount();

            if (isScrooling && (firstVisibleProductPosition + visibleProductCount == totalProductCount)) {
                isScrooling = false;
            }
        }
    }
}
