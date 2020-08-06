package com.pickleindia.pickle.orders;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.transition.MaterialSharedAxis;
import com.pickleindia.pickle.R;
import com.pickleindia.pickle.adapters.VisitorForList;
import com.pickleindia.pickle.databinding.FragmentOrdersBinding;
import com.pickleindia.pickle.interfaces.Visitable;
import com.pickleindia.pickle.main.FirebaseSearchActivity;
import com.pickleindia.pickle.models.EmptyState;
import com.pickleindia.pickle.models.Operation;
import com.pickleindia.pickle.models.Orders;
import com.pickleindia.pickle.models.OrdersDetails;
import com.pickleindia.pickle.utils.NotifyRecyclerItems;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import static com.pickleindia.pickle.utils.Constant.ADD;
import static com.pickleindia.pickle.utils.Constant.MODIFIED;
import static com.pickleindia.pickle.utils.Constant.REMOVE;
import static com.pickleindia.pickle.utils.OrderStatus.CANCEL;


/**
 * A simple {@link Fragment} subclass.
 */
public class OrdersPlacedFragment extends Fragment {

    private FragmentOrdersBinding binding;
    private ArrayList<Visitable> ordersList;
    private ArrayList<Visitable> pastOrdersList;
    private OrdersViewModel ordersViewModel;
    private Timer timer;
    private boolean firstRun = true;

    TimerTask timerTask = new TimerTask() {
        @Override
        public void run() {
            if (binding != null && getActivity() != null) {
                getActivity().runOnUiThread(() -> {
                    if (!firstRun) {
                        binding.orderProgressBar.setVisibility(View.GONE);
                        binding.pastOrderProgress.setVisibility(View.GONE);
                        if (ordersList.isEmpty())
                            Toast.makeText(getActivity(), "No current orders or swipe to refresh", Toast.LENGTH_SHORT).show();
                        if (pastOrdersList.isEmpty()) {
                            Toast.makeText(getActivity(), "No past orders or swipe to refresh", Toast.LENGTH_SHORT).show();
                        }
                        timer.cancel();
                    }
                    firstRun = false;
                });
            }
        }
    };

    public OrdersPlacedFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        binding = DataBindingUtil.inflate(
                inflater,
                R.layout.fragment_orders,
                container,
                false);

        setEnterTransition(MaterialSharedAxis.create(MaterialSharedAxis.X, true));

        ordersList = new ArrayList<>();
        pastOrdersList = new ArrayList<>();
        binding.setOrdersList(ordersList);
        binding.setPastOrdersList(pastOrdersList);
        binding.setVisitor(new VisitorForList());
        binding.setActivity(getActivity());
        binding.setIntent(new Intent(getActivity(), FirebaseSearchActivity.class).addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY));

        pastOrdersList.add(new EmptyState(R.drawable.crd_empty_past_order_bg, R.drawable.empty_past_orders_img, "Your past order's", "shows the history of order's past 6 month's"));

        ordersViewModel = new ViewModelProvider(this).get(OrdersViewModel.class);

        orders();
        updateHeaderView();

        binding.swipeToRefresh.setOnRefreshListener(() -> {
            ordersList.clear();
            binding.orderProgressBar.setVisibility(View.VISIBLE);
            binding.recyclerView.getRecycledViewPool().clear();
            updateHeaderView();
            orders();
        });

        timer = new Timer();
        timer.scheduleAtFixedRate(timerTask, 0, 10000);

        binding.loadMoreOrders.setOnClickListener(n -> addNewOrders());

        binding.executePendingBindings();
        return binding.getRoot();
    }

    private void orders() {
        LiveData<Operation> firebaseQueryLiveData = ordersViewModel.currentOrders();

        firebaseQueryLiveData.observe(getViewLifecycleOwner(), operation -> {
            updateHeaderView();
            addOrdersToType(operation);
            binding.orderProgressBar.setVisibility(View.GONE);
            if (binding.swipeToRefresh.isRefreshing()) {
                binding.swipeToRefresh.setRefreshing(false);
            }
        });
    }

    private void addNewOrders() {
        binding.pastOrderProgress.setVisibility(View.VISIBLE);
        if (!pastOrdersList.isEmpty() && pastOrdersList.get(pastOrdersList.size()-1) instanceof Orders) {
            Orders orders = (Orders) pastOrdersList.get(pastOrdersList.size() -1);
            ordersViewModel.loadMoreOrders(orders.getDate()+"_"+orders.getOrderId()).observe(getViewLifecycleOwner(), operation -> {
                addOrdersToType(operation);
                binding.pastOrderProgress.setVisibility(View.GONE);
            });
        }
    }

    private void addOrdersToType(Operation operation) {
        switch (operation.mode) {
            case ADD:
                Orders newOrders = (Orders) operation.aClass;
                if (newOrders.isPastOrder) addPastOrders(newOrders);
                else addOrders(newOrders);
                break;
            case MODIFIED:
                Orders orderModified = (Orders) operation.aClass;
                removeProduct(orderModified.getOrderId());
                break;
            case REMOVE:
                Log.e("ORDER PLACED FRAGMENT", "removed");
                break;
        }
    }

    private void addOrders(Orders addOrdersDetails) {
        if (!ordersList.contains(addOrdersDetails)) {
            ordersList.add(addOrdersDetails);
            NotifyRecyclerItems.notifyItemRemovedAt(binding.recyclerView, ordersList.indexOf(addOrdersDetails));
        }
    }

    private void addPastOrders(Orders pastOrderDetails) {
        if (!pastOrdersList.contains(pastOrderDetails)) {
            pastOrdersList.add(pastOrderDetails);
            NotifyRecyclerItems.notifyItemInsertedAt(binding.recyclerViewPastOrders, pastOrdersList.size());
        }
    }

    private void removeProduct(String orderId) {
        //start from 1 because at 0 index there is empty state card holder
        for (int i = 1; i < ordersList.size(); i++) {
            OrdersDetails currentOrdersDetails = (OrdersDetails) ordersList.get(i);
            if (orderId.equals(currentOrdersDetails.orderId)) {
                short removeIndex = (short) ordersList.indexOf(currentOrdersDetails);
                ordersList.remove(currentOrdersDetails);
                currentOrdersDetails.status = CANCEL;
                ordersList.add(currentOrdersDetails);
                binding.recyclerView.getAdapter().notifyItemMoved(removeIndex, ordersList.size() - 1);
                NotifyRecyclerItems.notifyItemInsertedAt(binding.recyclerView, 0);
                updateHeaderView();
            }
        }
    }

    private void updateHeaderView() {
        if (ordersList.size() == 0) {
            ordersList.add(new EmptyState(R.drawable.crd_empty_order_bg, R.drawable.empty_cart_img, "Whoops", "its look like their is no ongoing orders"));
            NotifyRecyclerItems.notifyDataSetChanged(binding.recyclerView);
        } else if (ordersList.size() == 1) {
            ordersList.remove(0);
            ordersList.add(0, new EmptyState(R.drawable.crd_order_bg, R.drawable.pablo_delivery_transparent, "Today's Orders", "your orders will be delivered as soon as possible"));
            NotifyRecyclerItems.notifyDataSetChanged(binding.recyclerView);
        }
    }
}
