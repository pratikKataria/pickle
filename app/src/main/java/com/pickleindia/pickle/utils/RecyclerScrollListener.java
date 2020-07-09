package com.pickleindia.pickle.utils;

import android.widget.AbsListView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class RecyclerScrollListener extends RecyclerView.OnScrollListener {

    boolean isScrolling = false;
    int visibleItemCount;
    int totalItemCount;
    int pastVisibleCount;

    LastItemVisibleListener lastItemVisibleListener;

    public RecyclerScrollListener() { }

    public RecyclerScrollListener(LastItemVisibleListener lastItemVisibleListener) {
        this.lastItemVisibleListener = lastItemVisibleListener;
    }

    @Override
    public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
        super.onScrollStateChanged(recyclerView, newState);
        if (newState == AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL) {
            isScrolling = true;
        }
    }

    @Override
    public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
        super.onScrolled(recyclerView, dx, dy);

        LinearLayoutManager layoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();

        visibleItemCount = layoutManager.getChildCount();
        totalItemCount = layoutManager.getItemCount();
        pastVisibleCount = layoutManager.findFirstCompletelyVisibleItemPosition();

        if (dy > 0 && (visibleItemCount + pastVisibleCount >= totalItemCount)) {
            lastItemVisibleListener.loadMoreItems();
        }

        if (dy < 0) {
            lastItemVisibleListener.stopLoading();
        }

    }

    public interface LastItemVisibleListener {
        void loadMoreItems();
        void stopLoading();
    }
}
