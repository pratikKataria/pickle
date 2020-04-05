package com.example.pickle.Adapters;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pickle.data.Product;
import com.example.pickle.data.ProductModel;
import com.example.pickle.data.SharedPrefsUtils;
import com.example.pickle.databinding.CardViewCartBinding;
import com.google.android.gms.common.util.SharedPreferencesUtils;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;

public class CartRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    Context context;
    private ArrayList<ProductModel> cartList;

    public CartRecyclerViewAdapter(Context context, ArrayList<ProductModel> cartList) {
        this.context = context;
        this.cartList = cartList;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        RecyclerView.ViewHolder holder;
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        CardViewCartBinding view = CardViewCartBinding.inflate(layoutInflater, parent, false);

        holder = new ProductCardViewHolder(view);

        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        ProductModel model = cartList.get(position);


        ProductCardViewHolder currCardViewHolder = (ProductCardViewHolder) holder;
        currCardViewHolder.bind(model);

        currCardViewHolder._increaseCart.setOnClickListener(view -> {
            Toast.makeText(context, "clicked", Toast.LENGTH_SHORT).show();
            if (model.getQuantityCounter() >= 1) {
                int a = model.getQuantityCounter();
                a++;

                if (cartList != null) {

                    ArrayList<ProductModel> currListOfProductInCurrCategory = null;
                    String currCategoryJson = SharedPrefsUtils.getStringPreference(context, model.getItemCategory(), 0);
                    ProductModel [] arrayOfProductsInCurrCategory = new Gson().fromJson(currCategoryJson, ProductModel[].class);
                    if (arrayOfProductsInCurrCategory != null) {
                        currListOfProductInCurrCategory = new ArrayList<>(Arrays.asList(arrayOfProductsInCurrCategory));
                    }

                    if (currListOfProductInCurrCategory != null) {

                        Iterator<ProductModel> itr  = currListOfProductInCurrCategory.iterator();
                        while (itr.hasNext()) {
                            ProductModel mdl = itr.next();
                            if (model.getItemId().equals(mdl.getItemId())) {
                                itr.remove();
                            }
                        }
                        removeItem(model);
                        currCardViewHolder._qtyCounter.setText(Integer.toString(a));
                        model.setQuantityCounter(a);
                        cartList.add(position, model);
                        currListOfProductInCurrCategory.add(model);
                        SharedPrefsUtils.setStringPreference(context, model.getItemCategory(), new Gson().toJson(currListOfProductInCurrCategory));
                        notifyDataSetChanged();
                    }

                }
            }
        });

        currCardViewHolder._decreaseCart.setOnClickListener(view -> {
            if (model.getQuantityCounter() <= 1) {
                int a = model.getQuantityCounter();
                a--;

                ArrayList<ProductModel> currListOfProductInCurrCategory = null;
                String currCategoryJson = SharedPrefsUtils.getStringPreference(context, model.getItemCategory(), 0);
                ProductModel [] arrayOfProductsInCurrCategory = new Gson().fromJson(currCategoryJson, ProductModel[].class);
                if (arrayOfProductsInCurrCategory != null) {
                    currListOfProductInCurrCategory = new ArrayList<>(Arrays.asList(arrayOfProductsInCurrCategory));
                }

                if (currListOfProductInCurrCategory != null) {

                    Iterator<ProductModel> itr = currListOfProductInCurrCategory.iterator();
                    while (itr.hasNext()) {
                        ProductModel mdl = itr.next();
                        if (model.getItemId().equals(mdl.getItemId())) {
                            itr.remove();
                        }
                    }

                    removeItem(model);
                    model.setQuantityCounter(a);
                    currListOfProductInCurrCategory.remove(model);
                    SharedPrefsUtils.setStringPreference(context, model.getItemCategory(), new Gson().toJson(currListOfProductInCurrCategory));
                }
//                Iterator<ProductModel> itr  = cartList.iterator();
//                while (itr.hasNext()) {
//                    ProductModel mdl = itr.next();
//                    if (model.getItemId().equals(mdl.getItemId())) {
//                        itr.remove();
//                        model.setQuantityCounter(a);
//                        notifyDataSetChanged();
//                        SharedPrefsUtils.setStringPreference(context, model.getItemCategory(), new Gson().toJson(cartList));
//                    }
//                }

                ((Activity) context).invalidateOptionsMenu();

            } else {
                int a = model.getQuantityCounter();
                a--;

                ArrayList<ProductModel> currListOfProductInCurrCategory = null;
                String currCategoryJson = SharedPrefsUtils.getStringPreference(context, model.getItemCategory(), 0);
                ProductModel [] arrayOfProductsInCurrCategory = new Gson().fromJson(currCategoryJson, ProductModel[].class);
                if (arrayOfProductsInCurrCategory != null) {
                    currListOfProductInCurrCategory = new ArrayList<>(Arrays.asList(arrayOfProductsInCurrCategory));
                }

                if (currListOfProductInCurrCategory != null) {

                    Iterator<ProductModel> itr  = currListOfProductInCurrCategory.iterator();
                    while (itr.hasNext()) {
                        ProductModel mdl = itr.next();
                        if (model.getItemId().equals(mdl.getItemId())) {
                            itr.remove();
                        }
                    }
                    removeItem(model);
                    currCardViewHolder._qtyCounter.setText(Integer.toString(a));
                    model.setQuantityCounter(a);
                    cartList.add(position, model);
                    currListOfProductInCurrCategory.add(model);
                    SharedPrefsUtils.setStringPreference(context, model.getItemCategory(), new Gson().toJson(currListOfProductInCurrCategory));
                    notifyDataSetChanged();
                }

//                currCardViewHolder._qtyCounter.setText(Integer.toString(a));
//                removeItem(model);
//                model.setQuantityCounter(a);
//                cartList.add(model);
//                SharedPrefsUtils.setStringPreference(context, model.getItemCategory(), new Gson().toJson(cartList));
//                ((Activity) context).invalidateOptionsMenu();
//                notifyDataSetChanged();
            }
        });

        currCardViewHolder._deleteFromCart.setOnClickListener(view -> {
            removeItem(model);
            SharedPrefsUtils.setStringPreference(context, model.getItemCategory(), new Gson().toJson(cartList));
        });

    }

    public boolean isItemPresentInList(ProductModel model) {
        for (ProductModel m : cartList) {
            if (model.getItemId().equals(m.getItemId())) {
                return true;
            }
        }
        return false;
    }

    public void removeItem(ProductModel model) {
        Iterator<ProductModel> itr  = cartList.iterator();
        while (itr.hasNext()) {
            ProductModel mdl = itr.next();
            if (model.getItemId().equals(mdl.getItemId())) {
                itr.remove();
                ArrayList<ProductModel> putEmptyList = new ArrayList<>();
                SharedPrefsUtils.setStringPreference(context, model.getItemCategory(), new Gson().toJson(putEmptyList));

                notifyDataSetChanged();
            }
        }
    }

    @Override
    public int getItemCount() {
        return cartList.size();
    }


    class ProductCardViewHolder extends RecyclerView.ViewHolder {

        private CardViewCartBinding binding;
        private ImageButton _increaseCart;
        private Button _decreaseCart;
        private ImageButton _addToCartButton;
        private ImageButton _deleteFromCart;

        private TextView _qtyCounter;

        public ProductCardViewHolder(@NonNull CardViewCartBinding binding) {
            super(binding.getRoot());
            this.binding = binding;

            _increaseCart = binding.increaseCartItem;
            _decreaseCart = binding.decreaseCartItem;
            _addToCartButton = binding.addToCartButton;
            _qtyCounter = binding.qtyCounter;
            _deleteFromCart = binding.imageButtonDelete;
        }

        public void bind(ProductModel model) {
            binding.setProduct(model);
            binding.executePendingBindings();
        }

    }

}
