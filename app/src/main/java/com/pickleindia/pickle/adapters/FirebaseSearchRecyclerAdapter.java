package com.pickleindia.pickle.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.pickleindia.pickle.adapters.viewholders.ProductViewHolder;
import com.pickleindia.pickle.databinding.CardViewProductSuggestionBinding;
import com.pickleindia.pickle.databinding.CardViewProductSuggestionLoadingBinding;
import com.pickleindia.pickle.databinding.CardViewSearchItemBinding;
import com.pickleindia.pickle.models.ProductModel;
import com.pickleindia.pickle.product.ProductViewModel;

import java.util.ArrayList;

public class FirebaseSearchRecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    Context context;
    ArrayList<ProductModel> productModelArrayList;
    private int viewType;
    private byte EMPTY_VIEW = -1;

    public FirebaseSearchRecyclerAdapter(Context context , ArrayList<ProductModel> productModelArrayList, int viewType) {
        this.context = context;
        this.productModelArrayList = productModelArrayList;
        this.viewType = viewType;
    }


    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == 0) {
            CardViewSearchItemBinding cardViewBinding = CardViewSearchItemBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
            return new ProductViewHolder(cardViewBinding, context);
        } else if (viewType == EMPTY_VIEW) {
            CardViewProductSuggestionLoadingBinding cardViewProductSuggestionLoadingBinding = CardViewProductSuggestionLoadingBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
            return new EmptyView(cardViewProductSuggestionLoadingBinding.getRoot());
        }else {
            CardViewProductSuggestionBinding cardViewProductSuggestionBinding = CardViewProductSuggestionBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
            return new ProductViewHolder(cardViewProductSuggestionBinding, context);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

        if (holder instanceof ProductViewHolder) {
            ProductModel product = productModelArrayList.get(position);
            ProductViewModel productViewModel = new ProductViewModel();
            productViewModel.setProduct(product);

            ProductViewHolder productViewHolder = (ProductViewHolder) holder;
            if (viewType == 0) {
                productViewHolder.setCardViewSearchBinding(productViewModel);
            } else {
                productViewHolder.setCardViewSuggestionBinding(productViewModel);
            }
        } else {
            EmptyView emptyView = (EmptyView) holder;
        }

    }



    @Override
    public int getItemCount() {
        if (productModelArrayList.size() == 0) {
            return 1;
        } else
            return productModelArrayList.size();
    }

    @Override
    public int getItemViewType(int position) {
        if (productModelArrayList.size() == 0) {
            return EMPTY_VIEW;
        }
        return viewType;
    }

    static class EmptyView extends  RecyclerView.ViewHolder {
        public EmptyView(@NonNull View itemView) {
            super(itemView);
        }
    }
}
