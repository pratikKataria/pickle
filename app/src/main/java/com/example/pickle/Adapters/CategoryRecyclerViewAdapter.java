package com.example.pickle.Adapters;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pickle.binding.GlideImageBindingAdapter;
import com.example.pickle.data.ProductModel;
import com.example.pickle.data.SharedPrefsUtils;
import com.example.pickle.databinding.CategoryProductCardViewBinding;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;

public class CategoryRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    Context context;
    ArrayList<ProductModel> productModelsList;
    public ArrayList<ProductModel> cartList = null;

    public CategoryRecyclerViewAdapter(Context context, ArrayList<ProductModel> productModelList, String category) {
        this.context = context;
        this.productModelsList = productModelList;

        String cartProducts = SharedPrefsUtils.getStringPreference(context, category, 0);
        ProductModel[] productModels = new Gson().fromJson(cartProducts, ProductModel[].class);


        if (cartList != null) {
            cartList = (ArrayList<ProductModel>) Arrays.asList(productModels);
        } else {
            if (productModels != null) {
                cartList = new ArrayList<>();
                for (ProductModel d : productModels) {
                    cartList.add(d);
                }
            }
        }

    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        RecyclerView.ViewHolder holder;
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        CategoryProductCardViewBinding view = CategoryProductCardViewBinding.inflate(layoutInflater, parent, false);

        holder = new ProductCardViewHolder(view);

        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        ProductModel model = productModelsList.get(position);


        ProductCardViewHolder currCardViewHolder = (ProductCardViewHolder) holder;
        currCardViewHolder.bind(model);

        currCardViewHolder._addToCartButton.setOnClickListener(view -> {
            if (cartList != null) {
                if (isItemPresentInList(model)) {
                    removeItem(model);
                    model.setQuantityCounter(1);
                    cartList.add(model);
                    SharedPrefsUtils.setStringPreference(context, model.getItemCategory(), new Gson().toJson(cartList));
                    ((Activity) context).invalidateOptionsMenu();
                    notifyDataSetChanged();
                } else {
                    model.setQuantityCounter(1);
                    cartList.add(model);
                    SharedPrefsUtils.setStringPreference(context, model.getItemCategory(), new Gson().toJson(cartList));
                    ((Activity) context).invalidateOptionsMenu();
                    notifyDataSetChanged();
                }
            } else {
                cartList = new ArrayList<>(Arrays.asList(model));
                removeItem(model);
                model.setQuantityCounter(1);
                cartList.add(model);
                SharedPrefsUtils.setStringPreference(context, model.getItemCategory(), new Gson().toJson(cartList));
                notifyDataSetChanged();
                ((Activity) context).invalidateOptionsMenu();
            }
        });

        currCardViewHolder._increaseCart.setOnClickListener(view -> {
            Toast.makeText(context, "clicked", Toast.LENGTH_SHORT).show();
            if (model.getQuantityCounter() >= 1) {
                int a = model.getQuantityCounter();
                a++;
                currCardViewHolder._qtyCounter.setText(Integer.toString(a));
                if (cartList != null) {
                    if (isItemPresentInList(model)) {
                        removeItem(model);
                        model.setQuantityCounter(a);
                        cartList.add(model);
                        SharedPrefsUtils.setStringPreference(context, model.getItemCategory(), new Gson().toJson(cartList));
                        notifyDataSetChanged();


                    } else {
                        model.setQuantityCounter(a);
                        cartList.add(model);
                        SharedPrefsUtils.setStringPreference(context, model.getItemCategory(), new Gson().toJson(cartList));
                        notifyDataSetChanged();

                    }
                }
//                Toast.makeText(context, "clicked  ---  " + currCardViewHolder._qtyCounter.getText().toString() + " === " + model.getQuantityCounter(), Toast.LENGTH_SHORT).show();
            }
        });

        currCardViewHolder._decreaseCart.setOnClickListener(view -> {

            if (model.getQuantityCounter() <= 1) {
                int a = model.getQuantityCounter();
                a--;

                Iterator<ProductModel> itr  = cartList.iterator();
                while (itr.hasNext()) {
                    ProductModel mdl = itr.next();
                    if (model.getItemId().equals(mdl.getItemId())) {
                        itr.remove();
                        model.setQuantityCounter(a);
                        notifyDataSetChanged();
                        SharedPrefsUtils.setStringPreference(context, model.getItemCategory(), new Gson().toJson(cartList));
                    }
                }

                ((Activity) context).invalidateOptionsMenu();

            } else {
                int a = model.getQuantityCounter();
                a--;
                currCardViewHolder._qtyCounter.setText(Integer.toString(a));
                removeItem(model);
                model.setQuantityCounter(a);
                cartList.add(model);
                SharedPrefsUtils.setStringPreference(context, model.getItemCategory(), new Gson().toJson(cartList));
                ((Activity) context).invalidateOptionsMenu();
                notifyDataSetChanged();
            }
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
                notifyDataSetChanged();
            }
        }
    }

    @Override
    public int getItemCount() {
        return productModelsList.size();
    }


    class ProductCardViewHolder extends RecyclerView.ViewHolder{

        private CategoryProductCardViewBinding binding;
        private ImageButton _increaseCart;
        private Button _decreaseCart;
        private Button _addToCartButton;

        private TextView _qtyCounter;

        public ProductCardViewHolder(@NonNull CategoryProductCardViewBinding binding) {
            super(binding.getRoot());
            this.binding = binding;

            _increaseCart = binding.increaseCartItem;
            _decreaseCart = binding.decreaseCartItem;
            _addToCartButton = binding.addToCartButton;
            _qtyCounter = binding.qtyCounter;
        }

        public void bind(ProductModel model) {
            binding.setProduct(model);
            binding.executePendingBindings();
        }

    }

}