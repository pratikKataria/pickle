package com.pickleindia.pickle.binding;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Build;
import android.widget.TextView;

import androidx.databinding.BindingAdapter;

import com.pickleindia.pickle.R;
import com.pickleindia.pickle.utils.OrderStatus;

import static com.pickleindia.pickle.utils.Constant.CAT_FOUR;
import static com.pickleindia.pickle.utils.Constant.CAT_THREE;
import static com.pickleindia.pickle.utils.Constant.CAT_ONE;
import static com.pickleindia.pickle.utils.Constant.CAT_TWO;

public class TextBindingAdapter {


    /**
     *
     * @param textView
     * accept boolean to set strike
     * @param strikeThrough
     */
    @BindingAdapter("strikeThrough")
    public static void strikeThrough(TextView textView, boolean strikeThrough) {
        if (strikeThrough) {
            textView.setPaintFlags(textView.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
        }else {
            textView.setPaintFlags(textView.getPaintFlags() & ~Paint.STRIKE_THRU_TEXT_FLAG);
        }
    }

    @BindingAdapter("orderStatusText")
    public static void statusIndicator(TextView textView, int status) {
        switch (status) {
            case OrderStatus.PROCESSING:
                textView.setText("Processing");
                break;
            case OrderStatus.CANCEL:
                textView.setText("Cancelled");
                textView.setTextColor(Color.RED);
                break;
            case OrderStatus.DELIVERED:
                textView.setText("Delivered");
                textView.setTextColor(textView.getContext().getResources().getColor(R.color.jungleGreen));
                break;
            case OrderStatus.ORDERED:
                textView.setText("Ordered");
                textView.setTextColor(textView.getContext().getResources().getColor(R.color.chartIdealBar));
                break;
        }
    }

    @BindingAdapter("productText")
    public static void setProductFragmentText(TextView textView, String type) {
        if (type == null) {
            return;
        }

        switch (type) {
            case CAT_ONE:
                textView.setText(CAT_ONE);
                break;
            case CAT_TWO:
                textView.setText(CAT_TWO);
                break;
            case CAT_FOUR:
                textView.setText(CAT_FOUR);
                break;
            case CAT_THREE:
                textView.setText(CAT_THREE);
                break;
        }

    }

}
