package com.pickleindia.pickle.interfaces;

import android.view.View;

import com.pickleindia.pickle.models.EmptyState;
import com.pickleindia.pickle.models.LoadingModel;
import com.pickleindia.pickle.models.Orders;
import com.pickleindia.pickle.models.OrdersDetails;
import com.pickleindia.pickle.adapters.viewholders.AbstractViewHolder;

public interface Visitor {
    int type(EmptyState emptyState);

    int type(OrdersDetails ordersDetails);

    int type(LoadingModel loadingModel);

    int type(Orders orders);

    AbstractViewHolder createViewHolder(View parent, int type);
}
