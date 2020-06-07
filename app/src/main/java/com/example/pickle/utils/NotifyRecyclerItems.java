package com.example.pickle.utils;

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
        if (recyclerView != null && recyclerView.getAdapter() != null)
            recyclerView.getAdapter().notifyDataSetChanged();
    }
}
