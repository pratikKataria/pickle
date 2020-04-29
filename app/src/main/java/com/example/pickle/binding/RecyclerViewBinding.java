package com.example.pickle.binding;

import androidx.databinding.BindingAdapter;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.example.pickle.Adapters.FirebaseSearchRecyclerAdapter;
import com.example.pickle.data.ProductModel;

import java.util.ArrayList;
import java.util.List;

public class RecyclerViewBinding {

    @BindingAdapter({"list", "viewType"})
    public static void orderRecyclerBinding(RecyclerView recyclerView, List<ProductModel> list, int viewType) {
        if (list == null) {
            return;
        }

        RecyclerView.LayoutManager  layoutManager = recyclerView.getLayoutManager();
        if (layoutManager == null) {
            if (viewType == 0) {
                recyclerView.setLayoutManager(new StaggeredGridLayoutManager(2, RecyclerView.VERTICAL));
            } else {
                recyclerView.setLayoutManager(new LinearLayoutManager(recyclerView.getContext(), RecyclerView.HORIZONTAL, false));
            }
        }

        FirebaseSearchRecyclerAdapter firebaseSearchRecyclerAdapter = (FirebaseSearchRecyclerAdapter) recyclerView.getAdapter();
        if (firebaseSearchRecyclerAdapter == null) {
            firebaseSearchRecyclerAdapter = new FirebaseSearchRecyclerAdapter(recyclerView.getContext(), (ArrayList<ProductModel>) list, viewType);
            recyclerView.setAdapter(firebaseSearchRecyclerAdapter);
        }
    }
}
