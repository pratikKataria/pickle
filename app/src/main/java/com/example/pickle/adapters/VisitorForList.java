package com.example.pickle.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.pickle.adapters.viewholders.AbstractViewHolder;
import com.example.pickle.adapters.viewholders.EmptyViewHolder;
import com.example.pickle.adapters.viewholders.OrdersViewHolder;
import com.example.pickle.databinding.CardviewEmptyOrderBinding;
import com.example.pickle.databinding.CardviewOrdersBinding;
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
              CardviewOrdersBinding cardviewOrdersBinding = CardviewOrdersBinding.bind(parent);
              createViewHolder = new OrdersViewHolder(cardviewOrdersBinding);
              break;
          case EmptyViewHolder.LAYOUT:
              CardviewEmptyOrderBinding cardviewEmptyOrderBinding = CardviewEmptyOrderBinding.bind(parent);
              createViewHolder = new EmptyViewHolder(cardviewEmptyOrderBinding);
              break;
      }
      return createViewHolder;
    }
}
