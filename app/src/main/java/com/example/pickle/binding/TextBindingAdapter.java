package com.example.pickle.binding;

import android.graphics.Color;
import android.graphics.Paint;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.databinding.BindingAdapter;

import com.example.pickle.R;
import com.example.pickle.interfaces.OrderStatus;
import com.example.pickle.utils.DateUtils;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import static com.example.pickle.utils.Constant.ORDERS_CANCELLED;
import static com.example.pickle.utils.Constant.ORDERS_DELIVERED;

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
                textView.setTextColor(Color.GREEN);
                break;
            case OrderStatus.ORDERED:
                textView.setText("Ordered");
                textView.setTextColor(Color.YELLOW);
                break;
        }
    }

}
