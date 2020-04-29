package com.example.pickle.ui;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pickle.data.ProductModel;
import com.example.pickle.databinding.CardViewProductSuggestionBinding;
import com.example.pickle.databinding.CardViewSearchItemBinding;
import com.google.android.material.button.MaterialButton;

public class ProductViewHolder extends  RecyclerView.ViewHolder {

    private CardViewSearchItemBinding binding;
    CardViewProductSuggestionBinding suggestionBinding;
    private Context context;
    private ProductModel product;
    private MaterialButton decreaseQtyBtn;
    private MaterialButton increaseQtyBtn;
    private MaterialButton addToCartBtn;


    public ProductViewHolder(@NonNull CardViewSearchItemBinding binding, Context context) {
        super(binding.getRoot());
        this.binding = binding;

        decreaseQtyBtn = binding.decreaseCartItem;
        increaseQtyBtn = binding.increaseCartItem;
        addToCartBtn = binding.addToCartButton;
        this.context = context;

        CardView cardView = binding.cardView;

        cardView.setOnClickListener(n -> {
            if (product != null) {
                showBottomSheet(product);
            }
        });
    }

    public ProductViewHolder(CardViewProductSuggestionBinding suggestionBinding, Context context) {
        super(suggestionBinding.getRoot());
        this.suggestionBinding = suggestionBinding;
        this.context = context;

        CardView cardView = suggestionBinding.cardView;
        cardView.setOnClickListener(n -> {
            if (product != null) {
                showBottomSheet(product);
            }
        });
    }

    public void setBinding(ProductModel productModel) {
        product = productModel;
        binding.setProduct(productModel);
    }

    public void setSuggestionBinding(ProductModel product) {
        this.product = product;
        suggestionBinding.setProduct(product);
    }

    private void showBottomSheet(ProductModel productModel) {
        SearchViewBottomSheetDialog bottomSheetDialog = new SearchViewBottomSheetDialog(productModel);
        bottomSheetDialog.show(((AppCompatActivity) context).getSupportFragmentManager(), "searchViewBottomSheet");
    }
}

