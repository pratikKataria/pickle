package com.example.pickle.binding;

import android.graphics.Paint;
import android.widget.TextView;

import androidx.databinding.BindingAdapter;

public class StrikeThrough {

    @BindingAdapter("strikeThrough")
    public static void strikeThrough(TextView textView, boolean strikeThrough) {
        if (strikeThrough) {
            textView.setPaintFlags(textView.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
        }else {
            textView.setPaintFlags(textView.getPaintFlags() & ~Paint.STRIKE_THRU_TEXT_FLAG);
        }
    }

}
