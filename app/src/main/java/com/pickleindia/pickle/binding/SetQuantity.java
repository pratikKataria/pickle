package com.pickleindia.pickle.binding;

import android.widget.TextView;

import androidx.databinding.BindingAdapter;

public class SetQuantity {
    @BindingAdapter({"setQuantity", "maxQuantity"})
    public static  void setQuantity(TextView textView, int qty, int maxQty) {
        if (textView == null) {
            return;
        }

        if (qty <= maxQty) {
            textView.setText(String.valueOf(qty));
        }
    }
}
