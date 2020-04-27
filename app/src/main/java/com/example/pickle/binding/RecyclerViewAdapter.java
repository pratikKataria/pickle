package com.example.pickle.binding;

import androidx.databinding.BindingAdapter;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.example.pickle.Adapters.FirebaseSearchRecyclerAdapter;
import com.example.pickle.data.ProductModel;

import java.util.ArrayList;
import java.util.List;

public class RecyclerViewAdapter {

    @BindingAdapter("list")
    public static void productList(RecyclerView recyclerView, List<ProductModel> list){
        if (list == null) {
            return;
        }

//        RecyclerView.LayoutManager layoutManager = recyclerView.getLayoutManager();
//        if (layoutManager == null ) {
//            recyclerView.setLayoutManager(new StaggeredGridLayoutManager(2, LinearLayoutManager.VERTICAL));
//        }
//
//        FirebaseSearchRecyclerAdapter adapter = (FirebaseSearchRecyclerAdapter) recyclerView.getAdapter();
//        if (adapter == null) {
//            adapter = new FirebaseSearchRecyclerAdapter(recyclerView.getContext(), (ArrayList<ProductModel>) list);
//            recyclerView.setAdapter(adapter);
//        }
    }
}
