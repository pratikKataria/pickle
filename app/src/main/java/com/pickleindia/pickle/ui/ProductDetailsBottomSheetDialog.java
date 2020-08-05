package com.pickleindia.pickle.ui;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ObservableInt;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.gson.Gson;
import com.pickleindia.pickle.R;
import com.pickleindia.pickle.cart.CartActivity;
import com.pickleindia.pickle.databinding.BottomSheetProductDetailsBinding;
import com.pickleindia.pickle.interfaces.ProductDetailsBottomSheetDialogListener;
import com.pickleindia.pickle.models.ProductModel;
import com.pickleindia.pickle.utils.SharedPrefsUtils;

public class ProductDetailsBottomSheetDialog extends BottomSheetDialogFragment {

    private ProductModel productModel;
    private ObservableInt currentQuantity = new ObservableInt(0);
    ProductDetailsBottomSheetDialogListener productDetailsBottomSheetDialogListener;

    public ProductDetailsBottomSheetDialog() { }

    public ProductDetailsBottomSheetDialog(ProductModel productModel) {
        this.productModel = productModel;
    }

    public ProductDetailsBottomSheetDialog(ProductModel productModel, ProductDetailsBottomSheetDialogListener productDetailsBottomSheetDialogListener) {
        this.productModel = productModel;
        this.productDetailsBottomSheetDialogListener = productDetailsBottomSheetDialogListener;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        BottomSheetProductDetailsBinding binding = DataBindingUtil.inflate(
                inflater,
                R.layout.bottom_sheet_product_details,
                container,
                false
        );


        binding.setProduct(productModel);
        binding.setFragment(this);
        binding.setCurrQty(currentQuantity);
        Log.e("Product Details Bottom ",  productModel.getQuantityCounter()+" ");
        binding.goToCartBtn.setOnClickListener(n -> {
             if (getActivity() != null) {
                 startActivity(new Intent(getActivity(), CartActivity.class));
             }
         });

        if (productModel != null) {
            currentQuantity.set(productModel.getQuantityCounter());
        }

        getDialog().setOnShowListener(dialog -> {
            BottomSheetDialog d = (BottomSheetDialog) dialog;
            FrameLayout bottomSheet = d.findViewById(com.google.android.material.R.id.design_bottom_sheet);
            if (bottomSheet == null)
                return;
            bottomSheet.setBackground(null);
        });

        return binding.getRoot();
    }

    public void increaseQty() {
        if (productModel == null && getActivity() == null) return;

        if (currentQuantity.get() < productModel.getItemMaxQtyPerUser()) {
            int temp = currentQuantity.get()+1;
            currentQuantity.set(temp);
            productModel.setQuantityCounter(currentQuantity.get());
            SharedPrefsUtils.setStringPreference(getActivity(), productModel.getItemId(), new Gson().toJson(productModel));
            productDetailsBottomSheetDialogListener.update(productModel);
        } else  {
            Toast.makeText(getActivity(), "max quantity reached", Toast.LENGTH_SHORT).show();
        }
    }

    public void decreaseQty() {
        if (productModel == null && getActivity() == null) return;

        currentQuantity.set(productModel.getQuantityCounter());
        if (currentQuantity.get() > 0) {
            int temp = currentQuantity.get()-1;
            currentQuantity.set(temp);
            productModel.setQuantityCounter(currentQuantity.get());

            if (currentQuantity.get() == 0) {
                SharedPrefsUtils.removeValuePreference(getActivity(), productModel.getItemId());
            } else {
                SharedPrefsUtils.setStringPreference(getActivity(), productModel.getItemId(), new Gson().toJson(productModel));
            }
            productDetailsBottomSheetDialogListener.update(productModel);
        }
    }

}
