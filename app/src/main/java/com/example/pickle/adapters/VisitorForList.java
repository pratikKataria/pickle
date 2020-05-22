package com.example.pickle.adapters;

import android.view.View;

import com.example.pickle.adapters.viewholders.AbstractViewHolder;
import com.example.pickle.adapters.viewholders.EmptyViewHolder;
import com.example.pickle.adapters.viewholders.OrdersViewHolder;
import com.example.pickle.interfaces.Visitor;
import com.example.pickle.models.EmptyState;
import com.example.pickle.models.OrdersDetails;

public class VisitorForList implements Visitor {
    @Override
    public int type(EmptyState emptyState) {
        return EmptyViewHolder.LAYOUT;
    }

    @Override
    public int type(OrdersDetails ordersDetails) {
        return OrdersViewHolder.LAYOUT;
    }

    @Override
    public AbstractViewHolder createViewHolder(View parent, int type) {
      AbstractViewHolder createViewHolder = null;
      switch (type) {
          case OrdersViewHolder.LAYOUT:
              createViewHolder = new OrdersViewHolder(parent);
              break;
          case EmptyViewHolder.LAYOUT:
              createViewHolder = new EmptyViewHolder(parent);
              break;
      }
      return createViewHolder;
    }
}
