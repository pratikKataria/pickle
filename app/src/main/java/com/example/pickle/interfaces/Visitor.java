package com.example.pickle.interfaces;

import android.view.View;

import com.example.pickle.models.EmptyState;
import com.example.pickle.models.OrdersDetails;
import com.example.pickle.adapters.viewholders.AbstractViewHolder;

public interface Visitor {
    int type(EmptyState emptyState);

    int type(OrdersDetails ordersDetails);

    AbstractViewHolder createViewHolder(View parent, int type);
}
