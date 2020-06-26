package com.example.pickle.binding;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Build;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.BindingAdapter;

import com.example.pickle.R;
import com.example.pickle.utils.OrderStatus;

import static com.example.pickle.utils.Constant.BEVERAGES;
import static com.example.pickle.utils.Constant.DAIRY;
import static com.example.pickle.utils.Constant.FRUITS;
import static com.example.pickle.utils.Constant.VEGETABLES;

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
            case FRUITS:
                textView.setText(FRUITS);
                break;
            case VEGETABLES:
                textView.setText(VEGETABLES);
                break;
            case BEVERAGES:
                textView.setText(BEVERAGES);
                break;
            case DAIRY:
                textView.setText(DAIRY);
                break;
        }

    }

    @BindingAdapter("locationText")
    public static void setLocationText(TextView textView, String type) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            boolean checkPermission = textView.getContext().checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED;
            //if true then permission is granted
            if (checkPermission) {
                textView.setText("Request Location Update");
            } else {
                textView.setText("Request Location Permission");
            }
        } else {

        }

    }

}
