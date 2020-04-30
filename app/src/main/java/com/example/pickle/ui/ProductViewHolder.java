package com.example.pickle.ui;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pickle.data.ProductModel;
import com.example.pickle.data.ProductViewModel;
import com.example.pickle.databinding.CardViewProductSuggestionBinding;
import com.example.pickle.databinding.CardViewSearchItemBinding;

public class ProductViewHolder extends  RecyclerView.ViewHolder {

    private CardViewSearchItemBinding binding;
    private CardViewProductSuggestionBinding suggestionBinding;
    private Context context;

    public ProductViewHolder(@NonNull CardViewSearchItemBinding searchViewBinding, Context context) {
        super(searchViewBinding.getRoot());
        this.binding = searchViewBinding;
        this.context = context;

        searchViewBinding.cardView.setOnClickListener(n -> showBottomSheet(suggestionBinding.getProductViewModel().getProduct()));
    }

    public ProductViewHolder(CardViewProductSuggestionBinding suggestionBinding, Context context) {
        super(suggestionBinding.getRoot());
        this.suggestionBinding = suggestionBinding;
        this.context = context;

        suggestionBinding.cardView.setOnClickListener(n -> showBottomSheet(suggestionBinding.getProductViewModel().getProduct()));
    }

    public void setCardViewSearchBinding(ProductViewModel productViewModel) {
        binding.setProductViewModel(productViewModel);
    }

    public void setCardViewSuggestionBinding(ProductViewModel productViewModel) {
        suggestionBinding.setProductViewModel(productViewModel);
    }

    private void showBottomSheet(ProductModel product) {
        SearchViewBottomSheetDialog bottomSheetDialog = new SearchViewBottomSheetDialog(product);
        bottomSheetDialog.show(((AppCompatActivity) context).getSupportFragmentManager(), "searchViewBottomSheet");
    }
}

