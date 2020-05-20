package com.example.pickle.binding;
import androidx.databinding.BindingAdapter;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;
import com.example.pickle.Adapters.CartRecyclerViewAdapter;
import com.example.pickle.Adapters.CategoryRecyclerViewAdapter;
import com.example.pickle.Adapters.FirebaseSearchRecyclerAdapter;
import com.example.pickle.Adapters.OrdersRecyclerAdapter;
import com.example.pickle.models.OrdersDetails;
import com.example.pickle.models.ProductModel;

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

    @BindingAdapter("productList")
    public static void categoryRecyclerViewAdapter(RecyclerView recyclerView, List<ProductModel> productList) {
        if (productList == null) {
            return;
        }

        RecyclerView.LayoutManager layoutManager = recyclerView.getLayoutManager();
        if (layoutManager == null) {
            layoutManager = new LinearLayoutManager(recyclerView.getContext(), RecyclerView.VERTICAL, false);
            recyclerView.setLayoutManager(layoutManager);
        }

        CategoryRecyclerViewAdapter categoryRecyclerViewAdapter = (CategoryRecyclerViewAdapter) recyclerView.getAdapter();
        if (categoryRecyclerViewAdapter == null) {
            categoryRecyclerViewAdapter = new CategoryRecyclerViewAdapter(recyclerView.getContext(), (ArrayList<ProductModel>) productList);
            recyclerView.setAdapter(categoryRecyclerViewAdapter);
        }
    }

    @BindingAdapter("carProductList")
    public static void cartViewRecyclerBinding(RecyclerView recyclerView, List<ProductModel> list) {

        if (list == null) {
            return;
        }

        RecyclerView.LayoutManager layoutManager = recyclerView.getLayoutManager();
        if (layoutManager == null) {
            layoutManager = new LinearLayoutManager(recyclerView.getContext(), RecyclerView.VERTICAL, false);
            recyclerView.setLayoutManager(layoutManager);
        }

        CartRecyclerViewAdapter cartRecyclerViewAdapter = (CartRecyclerViewAdapter) recyclerView.getAdapter();
        if (cartRecyclerViewAdapter == null) {
            cartRecyclerViewAdapter = new CartRecyclerViewAdapter(recyclerView.getContext(), (ArrayList<ProductModel>)list);
            recyclerView.setAdapter(cartRecyclerViewAdapter);
        }
    }

    @BindingAdapter("orderPlacedList")
    public static void orderPlaced(RecyclerView recyclerView, List<OrdersDetails> list) {

        if (list == null) {
            return;
        }

        RecyclerView.LayoutManager layoutManager = recyclerView.getLayoutManager();
        if (layoutManager == null) {
            layoutManager = new StaggeredGridLayoutManager(2, RecyclerView.VERTICAL);
            recyclerView.setLayoutManager(layoutManager);
        }

        OrdersRecyclerAdapter cartRecyclerViewAdapter = (OrdersRecyclerAdapter) recyclerView.getAdapter();
        if (cartRecyclerViewAdapter == null) {
            cartRecyclerViewAdapter = new OrdersRecyclerAdapter(recyclerView.getContext(), (ArrayList<OrdersDetails>)list);
            recyclerView.setAdapter(cartRecyclerViewAdapter);
        }
    }
}
