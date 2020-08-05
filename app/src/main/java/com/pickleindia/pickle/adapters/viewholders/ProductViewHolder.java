package com.pickleindia.pickle.adapters.viewholders;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.pickleindia.pickle.databinding.CardViewProductSuggestionBinding;
import com.pickleindia.pickle.databinding.CardViewSearchItemBinding;
import com.pickleindia.pickle.interfaces.ProductDetailsBottomSheetDialogListener;
import com.pickleindia.pickle.models.ProductModel;
import com.pickleindia.pickle.product.ProductViewModel;
import com.pickleindia.pickle.ui.ProductDetailsBottomSheetDialog;

public class ProductViewHolder extends  RecyclerView.ViewHolder implements ProductDetailsBottomSheetDialogListener {

    private CardViewSearchItemBinding searchViewBinding;
    private CardViewProductSuggestionBinding suggestionBinding;
    private Context context;

    public ProductViewHolder(@NonNull CardViewSearchItemBinding searchViewBinding, Context context) {
        super(searchViewBinding.getRoot());
        this.searchViewBinding = searchViewBinding;
        this.context = context;

        searchViewBinding.cardView.setOnClickListener(n -> showBottomSheet(searchViewBinding.getProductViewModel().getProduct()));
    }

    public ProductViewHolder(CardViewProductSuggestionBinding suggestionBinding, Context context) {
        super(suggestionBinding.getRoot());
        this.suggestionBinding = suggestionBinding;
        this.context = context;

        suggestionBinding.cardView.setOnClickListener(n -> showBottomSheet(suggestionBinding.getProductViewModel().getProduct()));
    }

    public void setCardViewSearchBinding(ProductViewModel productViewModel) {
        searchViewBinding.setProductViewModel(productViewModel);
        searchViewBinding.executePendingBindings();
    }

    public void setCardViewSuggestionBinding(ProductViewModel productViewModel) {
        suggestionBinding.setProductViewModel(productViewModel);
        suggestionBinding.executePendingBindings();
    }

    private void showBottomSheet(ProductModel product) {
        ProductDetailsBottomSheetDialog bottomSheetDialog = new ProductDetailsBottomSheetDialog(product, this);
        bottomSheetDialog.show(((AppCompatActivity) context).getSupportFragmentManager(), "searchViewBottomSheet");
    }

    @Override
    public void update(ProductModel productModel) {
        if (suggestionBinding != null ) {
            ProductViewModel productViewModel = suggestionBinding.getProductViewModel();
            productViewModel.setProduct(productModel);
            productViewModel.updateIcon(context);
        }

        if (searchViewBinding != null) {
            ProductViewModel productViewModel = searchViewBinding.getProductViewModel();
            productViewModel.setProduct(productModel);
            productViewModel.updateIcon(context);
        }
    }
}

