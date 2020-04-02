package com.example.pickle.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pickle.R;
import com.example.pickle.data.Product;
import com.example.pickle.data.ProductModel;

import java.util.ArrayList;

public class CategoryRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    Context context;
    ArrayList<ProductModel> productModelsList;

    public CategoryRecyclerViewAdapter(Context context, ArrayList<ProductModel> productModelList) {
        this.context = context;
        this.productModelsList = productModelList;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        RecyclerView.ViewHolder holder;
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.category_product_card_view, parent, false);

        holder = new ProductCardViewHolder(view);

        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return productModelsList.size();
    }


    class ProductCardViewHolder extends RecyclerView.ViewHolder {

        public ProductCardViewHolder(@NonNull View itemView) {
            super(itemView);
        }

    }

}
