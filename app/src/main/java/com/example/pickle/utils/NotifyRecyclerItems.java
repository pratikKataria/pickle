package com.example.pickle.utils;

import androidx.recyclerview.widget.RecyclerView;

public class NotifyRecyclerItems {

    public static void notifyItemInsertedAt(RecyclerView recyclerView, int index) {
        if (recyclerView.getAdapter() == null) {
            return;
        }
        recyclerView.getAdapter().notifyItemInserted(index);
    }

    public static void notifyItemRemovedAt(RecyclerView recyclerView, int index) {
        if (recyclerView.getAdapter() == null) {
            return;
        }
        recyclerView.getAdapter().notifyItemRemoved(index);
    }
}
