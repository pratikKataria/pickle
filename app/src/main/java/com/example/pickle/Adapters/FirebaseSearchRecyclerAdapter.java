package com.example.pickle.Adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.ImageView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import com.example.pickle.data.ProductModel;
import com.example.pickle.databinding.CardViewSearchItemBinding;
import com.example.pickle.ui.SearchViewBottomSheetDialog;
import com.example.pickle.utils.CartHandler;
import com.example.pickle.utils.SharedPrefsUtils;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class FirebaseSearchRecyclerAdapter extends FirebaseRecyclerAdapter<ProductModel, FirebaseSearchRecyclerAdapter.FirebaseSearchViewHolder> {

    Context context;
    private CartHandler cartHandler;


    public FirebaseSearchRecyclerAdapter(@NonNull FirebaseRecyclerOptions<ProductModel> options, Context context) {
        super(options);
        this.context = context;
        cartHandler = new CartHandler(context);
    }

    public FirebaseSearchRecyclerAdapter(@NonNull FirebaseRecyclerOptions<ProductModel> options, Context context, Map<String, ProductModel> cartMap) {
        super(options);
        this.context = context;
        cartHandler = new CartHandler(context, cartMap);
    }

    @Override
    protected void onBindViewHolder(@NonNull FirebaseSearchViewHolder holder, int position, @NonNull ProductModel product) {

        holder.setBinding(product);

        holder.addToCartBtn.setOnClickListener(view -> {
            int qty = cartHandler.add(product);
            product.setQuantityCounter(qty);
            notifyDataSetChanged();
        });


        holder.increaseQtyBtn.setOnClickListener(view -> {
            int qty = cartHandler.increaseQty(product.getItemId());
            product.setQuantityCounter(qty);
            notifyDataSetChanged();
        });

        holder.decreaseQtyBtn.setOnClickListener(view -> {
            int qty = cartHandler.decreaseQty(product.getItemId());
            product.setQuantityCounter(qty);
            notifyDataSetChanged();
        });
    }

    @Override
    public int getItemCount() {
        return super.getItemCount();
    }

    @NonNull
    @Override
    public FirebaseSearchViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        CardViewSearchItemBinding cardViewBinding = CardViewSearchItemBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);

        return new FirebaseSearchViewHolder(cardViewBinding, context);
    }

    public static class FirebaseSearchViewHolder extends RecyclerView.ViewHolder {

        private CardViewSearchItemBinding itemView;
        private ProductModel product;
        private MaterialButton decreaseQtyBtn;
        private MaterialButton increaseQtyBtn;
        private MaterialButton addToCartBtn;
        private Context context;

        public FirebaseSearchViewHolder(@NonNull CardViewSearchItemBinding itemView, Context context) {
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
