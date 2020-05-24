package com.example.pickle.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.pickle.models.ProductModel;
import com.example.pickle.product.ProductViewModel;
import com.example.pickle.databinding.CardViewProductSuggestionBinding;
import com.example.pickle.databinding.CardViewSearchItemBinding;
import com.example.pickle.ui.ProductViewHolder;
import java.util.ArrayList;

public class FirebaseSearchRecyclerAdapter extends RecyclerView.Adapter<ProductViewHolder> {

    Context context;
    ArrayList<ProductModel> productModelArrayList;
    private int viewType;

    public FirebaseSearchRecyclerAdapter(Context context , ArrayList<ProductModel> productModelArrayList, int viewType) {
        this.context = context;
        this.productModelArrayList = productModelArrayList;
        this.viewType = viewType;
    }


    @NonNull
    @Override
    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == 0) {
            CardViewSearchItemBinding cardViewBinding = CardViewSearchItemBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
            return new ProductViewHolder(cardViewBinding, context);
        } else {
            CardViewProductSuggestionBinding cardViewProductSuggestionBinding = CardViewProductSuggestionBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
            return new ProductViewHolder(cardViewProductSuggestionBinding, context);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull ProductViewHolder holder, int position) {

        ProductModel product = productModelArrayList.get(position);
        ProductViewModel productViewModel = new ProductViewModel();
        productViewModel.setProduct(product);
        if (viewType == 0) {
            holder.setCardViewSearchBinding(productViewModel);
        } else {
            holder.setCardViewSuggestionBinding(productViewModel);
        }

    }

    @Override
    public int getItemCount() {
        return productModelArrayList.size();
    }

    @Override
    public int getItemViewType(int position) {
        return viewType;
    }
}
