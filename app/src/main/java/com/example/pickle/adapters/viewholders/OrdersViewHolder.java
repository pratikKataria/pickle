package com.example.pickle.adapters.viewholders;

import android.view.View;

import androidx.annotation.LayoutRes;

import com.example.pickle.R;
import com.example.pickle.databinding.CardviewOrdersBinding;
import com.example.pickle.models.OrdersDetails;

public class OrdersViewHolder extends AbstractViewHolder<OrdersDetails> {
    @LayoutRes
    public static final int LAYOUT = R.layout.cardview_orders;

    public OrdersViewHolder(CardviewOrdersBinding binding) {
        super(binding.getRoot());
    }

    @Override
    public void bind(OrdersDetails element) {

    }
}
