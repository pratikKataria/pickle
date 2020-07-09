package com.pickleindia.pickle.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.pickleindia.pickle.models.ProductModel;
import com.pickleindia.pickle.product.ProductViewModel;
import com.pickleindia.pickle.databinding.CardViewCartBinding;

import java.util.ArrayList;

public class CartRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context context;
    private ArrayList<ProductModel> cartList;

    public CartRecyclerViewAdapter(Context context, ArrayList<ProductModel> cartList) {
        this.context = context;
        this.cartList = cartList;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        CardViewCartBinding view = CardViewCartBinding.inflate(layoutInflater, parent, false);

        return new CartProductViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        CartProductViewHolder currCardViewHolder = (CartProductViewHolder) holder;
        ProductViewModel productViewModel = new ProductViewModel();
        productViewModel.setProduct(cartList.get(position));
        currCardViewHolder.setProductModel(productViewModel);
    }

    @Override
    public int getItemCount() {
        return cartList.size();
    }

    public void updateCartItemsList(ProductModel product) {
        int indexOfItemRemoved = cartList.indexOf(product);
        cartList.remove(product);
        notifyItemRemoved(indexOfItemRemoved);
    }

    static class CartProductViewHolder extends RecyclerView.ViewHolder {

        private CardViewCartBinding binding;

        public CartProductViewHolder(@NonNull CardViewCartBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void setProductModel(ProductViewModel viewModel) {
            binding.setProductViewModel(viewModel);
            binding.executePendingBindings();
        }

    }
}
