package com.example.pickle.Adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pickle.activity.Main.FirebaseSearchActivity;
import com.example.pickle.data.ProductModel;
import com.example.pickle.databinding.CardViewSearchItemBinding;
import com.example.pickle.ui.SearchViewBottomSheetDialog;
import com.example.pickle.utils.SharedPrefsUtils;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.material.button.MaterialButton;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class FirebaseSearchRecyclerAdapter extends FirebaseRecyclerAdapter<ProductModel, FirebaseSearchRecyclerAdapter.FirebaseSearchViewHolder> {

    Context context;
    private Map<String, ProductModel> cartMap;


    public FirebaseSearchRecyclerAdapter(@NonNull FirebaseRecyclerOptions<ProductModel> options, Context context) {
        super(options);
        this.context = context;
        cartMap = new HashMap<>();

    }

    public FirebaseSearchRecyclerAdapter(@NonNull FirebaseRecyclerOptions<ProductModel> options, Context context, Map<String, ProductModel> cartMap) {
        super(options);
        this.context = context;
        this.cartMap = cartMap;

    }

    @Override
    protected void onBindViewHolder(@NonNull FirebaseSearchViewHolder holder, int position, @NonNull ProductModel model) {

        Log.e("FirebaseSearchActivity", "Firebase view holder");

        holder.setBinding(model);
        Log.e("FirebaseSearch", cartMap.size() + "");

        holder.addToCartBtn.setOnClickListener(view -> {
            if (cartMap.isEmpty()) {
                model.setQuantityCounter(1);
                cartMap.put(model.getItemId(), model);
                Log.e("FirebaseSearch", cartMap.values() + "");
                Log.e("FirebaseSearch", cartMap.size() + "");
                SharedPrefsUtils.setStringPreference(context, model.getItemCategory(), new Gson().toJson(new ArrayList<>(cartMap.values())));
                notifyDataSetChanged();
            } else {
                if (!cartMap.containsKey(model.getItemCategory())) {
                    model.setQuantityCounter(1);
                    cartMap.put(model.getItemId(), model);
                    Log.e("FirebaseSearch", cartMap.values() + "");
                    Log.e("FirebaseSearch", cartMap.size() + "");
                    SharedPrefsUtils.setStringPreference(context, model.getItemCategory(), new Gson().toJson(new ArrayList<>(cartMap.values())));
                    notifyDataSetChanged();
                }

            }
        });


        holder.increaseQtyBtn.setOnClickListener(view -> {
            if (!cartMap.isEmpty() && cartMap.containsKey(model.getItemId())) {
                int initQty = model.getQuantityCounter();
                model.setQuantityCounter(initQty + 1);
                cartMap.put(model.getItemId(), model);
                Log.e("FirebaseSearch", cartMap.values() + "");
                Log.e("FirebaseSearch", cartMap.size() + "");
                SharedPrefsUtils.setStringPreference(context, model.getItemCategory(), new Gson().toJson(new ArrayList<>(cartMap.values())));
                notifyDataSetChanged();
            }
        });

        holder.decreaseQtyBtn.setOnClickListener(view -> {
            if (!cartMap.isEmpty() && cartMap.containsKey(model.getItemId())) {
                int initQty = model.getQuantityCounter();
                if (initQty > 1) {
                    model.setQuantityCounter(initQty - 1);
                    cartMap.put(model.getItemId(), model);
                    Log.e("FirebaseSearch", cartMap.values() + "");
                    Log.e("FirebaseSearch", cartMap.size() + "");
                    SharedPrefsUtils.setStringPreference(context, model.getItemCategory(), new Gson().toJson(new ArrayList<>(cartMap.values())));
                    notifyDataSetChanged();
                } else if (initQty == 1) {
                    model.setQuantityCounter(initQty - 1);
                    cartMap.remove(model.getItemId());
                    Log.e("FirebaseSearch", cartMap.values() + "");
                    SharedPrefsUtils.setStringPreference(context, model.getItemCategory(), new Gson().toJson(new ArrayList<>(cartMap.values())));
                    Log.e("FirebaseSearch", cartMap.size() + "");
                    notifyDataSetChanged();
                }
            }
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
        private ImageView increaseQtyBtn;
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
