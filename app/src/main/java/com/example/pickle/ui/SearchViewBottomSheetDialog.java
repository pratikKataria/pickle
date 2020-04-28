package com.example.pickle.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;

import com.example.pickle.R;
import com.example.pickle.activity.Main.Options.CartViewActivity;
import com.example.pickle.data.ProductModel;
import com.example.pickle.databinding.BottomSheetSearchViewBinding;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.button.MaterialButton;

public class SearchViewBottomSheetDialog extends BottomSheetDialogFragment {

    private BottomSheetSearchViewBinding searchViewBinding;
    private ProductModel productModel;
    private MaterialButton decreaseQtyBtn;
    private ImageView       increaseQtyBtn;
    private MaterialButton addToCartBtn;

    public SearchViewBottomSheetDialog() {

    }

    public SearchViewBottomSheetDialog(ProductModel productModel) {
        this.productModel = productModel;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        searchViewBinding = DataBindingUtil.inflate(
                inflater,
                R.layout.bottom_sheet_search_view,
                container,
                false
        );


        searchViewBinding.setProduct(productModel);
        searchViewBinding.goToCartBtn.setOnClickListener(n -> {
             if (getActivity() != null) {
                 startActivity(new Intent(getActivity(), CartViewActivity.class));
                 getActivity().finish();
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
