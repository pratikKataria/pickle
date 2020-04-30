package com.example.pickle.binding;

import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

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
