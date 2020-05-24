package com.example.pickle.orders;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModelProvider;

import com.example.pickle.R;
import com.example.pickle.adapters.VisitorForList;
import com.example.pickle.databinding.FragmentOrdersPlacedBinding;
import com.example.pickle.interfaces.Visitable;
import com.example.pickle.models.EmptyState;
import com.example.pickle.models.Operation;
import com.example.pickle.models.Orders;
import com.example.pickle.models.OrdersDetails;

import java.util.ArrayList;

import static com.example.pickle.interfaces.OrderStatus.CANCEL;
import static com.example.pickle.utils.Constant.ADD;
import static com.example.pickle.utils.Constant.MODIFIED;
import static com.example.pickle.utils.Constant.REMOVE;


/**
 * A simple {@link Fragment} subclass.
 */
public class OrdersPlacedFragment extends Fragment {

    private FragmentOrdersPlacedBinding binding;
    private ArrayList<Visitable> ordersList;
    private ArrayList<Visitable> pastOrdersList;


    public OrdersPlacedFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        binding = DataBindingUtil.inflate(
                inflater,
                R.layout.fragment_orders_placed,
                container,
                false);

        ordersList = new ArrayList<>();
        pastOrdersList = new ArrayList<>();
        binding.setOrdersList(ordersList);
        binding.setPastOrdersList(pastOrdersList);
        binding.setVisitor(new VisitorForList());
        updateHeaderView();

        pastOrdersList.add(new EmptyState(R.drawable.crd_empty_past_order_bg, R.drawable.empty_past_orders_img, "Your past order's", "shows the history of order's past 6 month's"));


        OrdersViewModel ordersViewModel = new ViewModelProvider(this).get(OrdersViewModel.class);
        LiveData<Operation> firebaseQueryLiveData =  ordersViewModel.getLiveData();

        firebaseQueryLiveData.observe(getViewLifecycleOwner(), operation -> {
            updateHeaderView();
            switch (operation.mode) {
                case ADD:
                    OrdersDetails newOrdersDetails = (OrdersDetails) operation.aClass;
                    if (newOrdersDetails.isPastOrder) {
                        addPastOrders(newOrdersDetails);
                    } else {
                        addProduct(newOrdersDetails);
                    }
                    break;
                case MODIFIED:
                    Orders orderModified = (Orders) operation.aClass;
                    if (orderModified.getOrderStatus() == CANCEL) {
                        removeProduct(orderModified.getOrderId());
                    }
                    break;
                case REMOVE:
                    Log.e("ORDER PLACED FRAGMENT", "removed");
                    break;
            }

        });


        return binding.getRoot();
    }



    private void addProduct(OrdersDetails addedOrdersDetails) {
        ordersList.add(addedOrdersDetails);
        notifyChanges();
    }

    private void addPastOrders(OrdersDetails pastOrderDetails) {
        pastOrdersList.add(pastOrderDetails);
        notifyChanges();
    }

    private void removeProduct(String orderId) {
        //start from 1 because at 0 index there is empty state card holder
        for (int i = 1; i < ordersList.size(); i++) {
            OrdersDetails currentOrdersDetails = (OrdersDetails) ordersList.get(i);
            if (orderId.equals(currentOrdersDetails.orderId)) {
                currentOrdersDetails.isPastOrder = true;
                currentOrdersDetails.status = CANCEL;
                ordersList.remove(currentOrdersDetails);
                pastOrdersList.add(currentOrdersDetails);
                notifyChanges();
                updateHeaderView();
            }
        }
    }


    private void notifyChanges() {
        try {
            binding.recyclerView.getAdapter().notifyDataSetChanged();
            binding.recyclerViewPastOrders.getAdapter().notifyDataSetChanged();
        } catch (NullPointerException npe) {
            Log.e(OrdersPlacedFragment.class.getName(), npe.getMessage());
        }
    }


    private void updateHeaderView() {
        if (ordersList.size() == 0) {
            ordersList.add(new EmptyState(R.drawable.crd_empty_order_bg, R.drawable.empty_cart_img, "Whoops", "its look like that no ongoing orders"));
            notifyChanges();
        } else if (ordersList.size() == 2) {
            ordersList.remove(0);
            ordersList.add(0, new EmptyState(R.drawable.crd_order_bg, R.drawable.pablo_delivery_transparent, "Today's Orders", "your orders will be delivered as soon as possible"));
            notifyChanges();
        }
    }
}
