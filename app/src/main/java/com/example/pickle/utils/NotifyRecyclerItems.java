package com.example.pickle.utils;

import android.util.Log;

import androidx.recyclerview.widget.RecyclerView;

public class NotifyRecyclerItems {

    public static void notifyItemInsertedAt(RecyclerView recyclerView, int index) {
        if (recyclerView != null && recyclerView.getAdapter() != null)
            recyclerView.getAdapter().notifyItemInserted(index);
    }

    public static void notifyItemRemovedAt(RecyclerView recyclerView, int index) {
        if (recyclerView != null && recyclerView.getAdapter() != null)
            recyclerView.getAdapter().notifyItemRemoved(index);
    }

    public static void notifyItemChangedAt(RecyclerView recyclerView, int index) {
        if (recyclerView != null && recyclerView.getAdapter() != null)
            recyclerView.getAdapter().notifyItemChanged(index);
    }

    public static void notifyDataSetChanged(RecyclerView recyclerView) {
        Log.e("image load timer", System.currentTimeMillis()+" notified");
        if (recyclerView != null && recyclerView.getAdapter() != null)
            recyclerView.getAdapter().notifyDataSetChanged();
    }
}
