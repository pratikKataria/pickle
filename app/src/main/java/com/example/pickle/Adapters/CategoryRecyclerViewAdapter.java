package com.example.pickle.Adapters;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewPropertyAnimator;
import android.view.animation.AnticipateOvershootInterpolator;
import android.view.animation.OvershootInterpolator;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pickle.activity.Main.EmptyActivity;
import com.example.pickle.data.ProductModel;
import com.example.pickle.databinding.ActivityEmptyBinding;
import com.example.pickle.databinding.CardViewCategoryProductBinding;
import com.example.pickle.ui.SearchViewBottomSheetDialog;
import com.example.pickle.utils.SharedPrefsUtils;
import com.google.android.material.button.MaterialButton;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;

public class CategoryRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context context;
    private ArrayList<ProductModel> productModelsList;
    private ArrayList<ProductModel> cartList = null;

    private static final byte EMPTY_VIEW = 32;
    private static final byte CARD_VIEW = 55;

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
        if (viewType == EMPTY_VIEW) {
            ActivityEmptyBinding view = ActivityEmptyBinding.inflate(layoutInflater, parent, false);
            holder = new EmptyView(view.getRoot());
        } else {
            CardViewCategoryProductBinding view = CardViewCategoryProductBinding.inflate(layoutInflater, parent, false);
            holder = new ProductCardViewHolder(view);
        }

        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

        if (holder instanceof ProductCardViewHolder) {
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
                if (model.getQuantityCounter() >= 1 && model.getQuantityCounter() < model.getItemMaxQtyPerUser()) {
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
                }else {
                    Toast.makeText(context, "can't add more items", Toast.LENGTH_SHORT).show();
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

            ((ProductCardViewHolder) holder).binding.cardView.setOnClickListener(
                    n -> ((ProductCardViewHolder) holder).showDialog(productModelsList.get(position))
            );
        } else {
            //empty view
            EmptyView emptyView = (EmptyView) holder;
        }


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
        if (productModelsList.size() == 0) {
            return 1;
        } else {
            return productModelsList.size();
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (productModelsList.size() == 0) {
            return EMPTY_VIEW;
        } else {
            return CARD_VIEW;
        }
    }

    class EmptyView extends  RecyclerView.ViewHolder {

        public EmptyView(@NonNull View itemView) {
            super(itemView);
        }
    }

    class ProductCardViewHolder extends RecyclerView.ViewHolder{

        CardViewCategoryProductBinding binding;
        private MaterialButton _increaseCart;
        private MaterialButton _decreaseCart;
        private MaterialButton _addToCartButton;

        private TextView _qtyCounter;

        public ProductCardViewHolder(@NonNull CardViewCategoryProductBinding binding) {
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

        private void showDialog(ProductModel product) {
            SearchViewBottomSheetDialog searchViewBottomSheetDialog = new SearchViewBottomSheetDialog(product);
            searchViewBottomSheetDialog.show(((AppCompatActivity)context).getSupportFragmentManager(), "searchViewBottomSheet");
        }

    }

}
