package com.pickleindia.pickle.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;

import com.pickleindia.pickle.R;
import com.pickleindia.pickle.cart.CartActivity;
import com.pickleindia.pickle.databinding.BottomSheetProductDetailsBinding;
import com.pickleindia.pickle.models.ProductModel;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

public class ProductDetailsBottomSheetDialog extends BottomSheetDialogFragment {

    private ProductModel productModel;

    public ProductDetailsBottomSheetDialog() {

    }

    public ProductDetailsBottomSheetDialog(ProductModel productModel) {
        this.productModel = productModel;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        BottomSheetProductDetailsBinding searchViewBinding = DataBindingUtil.inflate(
                inflater,
                R.layout.bottom_sheet_product_details,
                container,
                false
        );


        searchViewBinding.setProduct(productModel);
        searchViewBinding.goToCartBtn.setOnClickListener(n -> {
             if (getActivity() != null) {
                 startActivity(new Intent(getActivity(), CartActivity.class));
             }
         });

        getDialog().setOnShowListener(dialog -> {
            BottomSheetDialog d = (BottomSheetDialog) dialog;
            FrameLayout bottomSheet = d.findViewById(com.google.android.material.R.id.design_bottom_sheet);
            if (bottomSheet == null)
                return;
            bottomSheet.setBackground(null);
        });

        return searchViewBinding.getRoot();
    }


}
