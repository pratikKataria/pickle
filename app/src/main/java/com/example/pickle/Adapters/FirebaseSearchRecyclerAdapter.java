package com.example.pickle.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pickle.data.ProductModel;
import com.example.pickle.databinding.CardViewSearchItemBinding;
import com.example.pickle.ui.SearchViewBottomSheetDialog;
import com.google.android.material.button.MaterialButton;

import java.util.ArrayList;

public class FirebaseSearchRecyclerAdapter extends RecyclerView.Adapter<FirebaseSearchRecyclerAdapter.ProductsViewHolder> {

    Context context;
    ArrayList<ProductModel> productModelArrayList;

    public FirebaseSearchRecyclerAdapter(Context context , ArrayList<ProductModel> productModelArrayList) {
        this.context = context;
        this.productModelArrayList = productModelArrayList;
    }


    @NonNull
    @Override
    public ProductsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        CardViewSearchItemBinding cardViewBinding = CardViewSearchItemBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);

        return new ProductsViewHolder(cardViewBinding, context);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductsViewHolder holder, int position) {
        ProductModel product = productModelArrayList.get(position);
        holder.setBinding(product);

        holder.addToCartBtn.setOnClickListener(view -> {
//            int qty = cartHandler.add(product);
//            product.setQuantityCounter(qty);
            notifyDataSetChanged();
        });


        holder.increaseQtyBtn.setOnClickListener(view -> {
//            int qty = cartHandler.increaseQty(product.getItemId());
//            product.setQuantityCounter(qty);
            notifyDataSetChanged();
        });

        holder.decreaseQtyBtn.setOnClickListener(view -> {
//            int qty = cartHandler.decreaseQty(product.getItemId());
//            product.setQuantityCounter(qty);
            notifyDataSetChanged();
        });
    }

    @Override
    public int getItemCount() {
        return productModelArrayList.size();
    }

    static class ProductsViewHolder extends  RecyclerView.ViewHolder {
        private CardViewSearchItemBinding itemView;
        private ProductModel product;
        private MaterialButton decreaseQtyBtn;
        private MaterialButton increaseQtyBtn;
        private MaterialButton addToCartBtn;
        private Context context;

        public ProductsViewHolder(@NonNull CardViewSearchItemBinding itemView, Context context) {
            super(itemView.getRoot());
            this.itemView = itemView;

            decreaseQtyBtn = itemView.decreaseCartItem;
            increaseQtyBtn = itemView.increaseCartItem;
            addToCartBtn = itemView.addToCartButton;
            this.context = context;

            CardView cardView = itemView.cardView;

            cardView.setOnClickListener(n -> {
                if (product != null) {
                    showBottomSheet(product);
                }
            });
        }

        void setBinding(ProductModel productModel) {
            product = productModel;
            itemView.setProduct(productModel);
        }

        private void showBottomSheet(ProductModel productModel) {
            SearchViewBottomSheetDialog bottomSheetDialog = new SearchViewBottomSheetDialog(productModel);
            bottomSheetDialog.show(((AppCompatActivity) context).getSupportFragmentManager(), "searchViewBottomSheet");
        }
    }

}
