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
import com.example.pickle.databinding.CategoryProductCardViewBinding;

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
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        CategoryProductCardViewBinding view = CategoryProductCardViewBinding.inflate(layoutInflater, parent, false);

        holder = new ProductCardViewHolder(view);

        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        ProductModel model = productModelsList.get(position);
        ((ProductCardViewHolder) holder).bind(model);
    }

    @Override
    public int getItemCount() {
        return productModelsList.size();
    }


    class ProductCardViewHolder extends RecyclerView.ViewHolder {

        private CategoryProductCardViewBinding binding;

        public ProductCardViewHolder(@NonNull CategoryProductCardViewBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(ProductModel model) {
            binding.setProduct(model);
            binding.executePendingBindings();
        }

    }

}
