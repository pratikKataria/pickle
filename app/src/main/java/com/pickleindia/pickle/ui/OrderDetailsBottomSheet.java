package com.pickleindia.pickle.ui;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.databinding.DataBindingUtil;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.pickleindia.pickle.R;
import com.pickleindia.pickle.adapters.VisitorForList;
import com.pickleindia.pickle.databinding.BottomSheetOrderDetailsBinding;
import com.pickleindia.pickle.interfaces.Visitable;
import com.pickleindia.pickle.models.Orders;
import com.pickleindia.pickle.models.OrdersDetails;
import com.pickleindia.pickle.utils.DateUtils;
import com.pickleindia.pickle.utils.NotifyRecyclerItems;
import com.pickleindia.pickle.utils.PriceFormatUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static com.pickleindia.pickle.utils.Constant.ORDERS;
import static com.pickleindia.pickle.utils.Constant.ORDERS_CANCELLED;
import static com.pickleindia.pickle.utils.Constant.ORDERS_DETAILS;
import static com.pickleindia.pickle.utils.OrderStatus.CANCEL;
import static com.pickleindia.pickle.utils.OrderStatus.ORDERED;

public class OrderDetailsBottomSheet extends BottomSheetDialogFragment {

    private String orderId;
    private Orders orders;
    private ArrayList<Visitable> orderedProductList;

    private DatabaseReference databaseReference;
    private ValueEventListener valueEventListener;
    private BottomSheetOrderDetailsBinding orderBinding;

    public OrderDetailsBottomSheet() {

    }

    public OrderDetailsBottomSheet(String orderId, Orders orders) {
        if (orderId != null) this.orderId = orderId;
        else this.orderId = "";

        this.orders = orders;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        orderBinding = DataBindingUtil.inflate(inflater,
                R.layout.bottom_sheet_order_details,
                container,
                false);

        setNullBackground();
        orderedProductList = new ArrayList<>();
        orderBinding.setOrdersList(orderedProductList);
        orderBinding.setVisitor(new VisitorForList());
        orderBinding.setOrders(orders);
        orderBinding.setOrderCancellationNote("");

        double calcFinalTotal = PriceFormatUtils.getDoubleFormat((orders.getSubTotal() + orders.getShipping() + orders.getComboPrice()) - orders.getPcoinsSpent());

        if (calcFinalTotal > 0) {
            orderBinding.finalTotalAmount.setText(PriceFormatUtils.getStringFormattedPrice(calcFinalTotal));
        } else {
            orderBinding.finalTotalAmount.setText("invalid");
        }

        databaseReference = FirebaseDatabase.getInstance().getReference(ORDERS_DETAILS).child(orderId);
        orderBinding.ordersLoadingProgressBar.setVisibility(View.VISIBLE);
        valueEventListener = databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    OrdersDetails ordersDetails = dataSnapshot.getValue(OrdersDetails.class);
                    orderedProductList.add(ordersDetails);
                    NotifyRecyclerItems.notifyDataSetChanged(orderBinding.recyclerView);
                }
                orderBinding.ordersLoadingProgressBar.setVisibility(View.GONE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e(OrderDetailsBottomSheet.class.getName(), error.getMessage() + " ");
            }
        });

        orderBinding.closeImageButton.setOnClickListener(n -> {
            if (getDialog() == null) return;
            getDialog().dismiss();
        });

        if (orders != null && orders.getOrderStatus() == ORDERED) {
            if (orders.getDeliveryTime().equals(getString(R.string.morningDeliveryTime))) {
                if (DateUtils.orderTimeIsAfter(orders.getDate(), "12:00 AM") && !DateUtils.orderTimeIsAfter(orders.getDate(), "7:00 AM")) {
                    orderBinding.setOrderCancellationNote("morning order cannot be canceled after 12 AM");
                }
            } else if (orders.getDeliveryTime().equals(getString(R.string.eveningDeliveryTime))) {
                int hour = 15, minute = 30, second = 0;
                if (DateUtils.orderDateIsBeforeCancelDate(orders.getDate(), 0, hour, minute, 0)) {
                    Log.d("OrderDetails", "order Date is before");
                } else if (DateUtils.orderTimeIsAfter(orders.getDate(), "4:00 PM") && DateUtils.orderDateIsBeforeCancelDate(orders.getDate(), 1, hour, minute, second)) {
                    Log.d("OrderDetails", "order Date is before");
                } else {
                    Log.d("OrderDetails", "order Date is after evening delivery timer");
                    orderBinding.setOrderCancellationNote("evening order cannot be canceled after " + Math.abs(hour - 12) + ":" + minute + " PM");
                }
            }
        }

        orderBinding.cancelMaterialButton.setOnClickListener(v -> {
            if (getDialog() != null)
                getDialog().setCancelable(false);
            showAlertDialog();
        });

        orderBinding.executePendingBindings();
        return orderBinding.getRoot();
    }

    private void setNullBackground() {
        if (getDialog() != null) {
            getDialog().setOnShowListener(dialog -> {
                BottomSheetDialog bottomSheetDialog = (BottomSheetDialog) dialog;
                FrameLayout frameLayout = bottomSheetDialog.findViewById(com.google.android.material.R.id.design_bottom_sheet);
                if (frameLayout == null) {
                    return;
                }
                frameLayout.setBackground(null);
            });
        }
    }

    private void showAlertDialog() {
        AlertDialog alertDialog = null;
        MaterialAlertDialogBuilder materialAlertDialogBuilder = new MaterialAlertDialogBuilder(getActivity(), R.style.AlertDialogTheme);
        materialAlertDialogBuilder.setTitle("Would like to cancel the order?");
        materialAlertDialogBuilder.setMessage("Canceling the order would take some time to and notifies after a while.");
        materialAlertDialogBuilder.setPositiveButton("Back", (dialog, which) -> {
            if (getDialog() != null) {
                getDialog().setCancelable(true);
            }
        }).setNegativeButton("Cancel Order", (dialog, which) -> cancelOrder());

        alertDialog = materialAlertDialogBuilder.create();
        alertDialog.show();
    }

    private void cancelOrder() {
        orderBinding.orderCancelProgressBar.setVisibility(View.VISIBLE);
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();

        Map<String, Object> atomicUpdate = new HashMap<>();
        atomicUpdate.put(ORDERS + "/" + orders.getOrderId() + "/" + "orderStatus", CANCEL);
        atomicUpdate.put(ORDERS_CANCELLED + "/" + orders.getOrderId() + "/" + "orderId", orders.getOrderId());
        atomicUpdate.put(ORDERS_CANCELLED + "/" + orders.getOrderId() + "/" + "date", ServerValue.TIMESTAMP);

        databaseReference.updateChildren(atomicUpdate).addOnSuccessListener(task -> {
            orderBinding.orderCancelProgressBar.setVisibility(View.GONE);
            Toast.makeText(getActivity(), "order cancel successfully", Toast.LENGTH_SHORT).show();
            orderBinding.cancelMaterialButton.setVisibility(View.GONE);
            if (getDialog() != null)
                getDialog().setCancelable(true);
        }).addOnFailureListener(taskFailed -> {
            Toast.makeText(getActivity(), "failed to cancel order", Toast.LENGTH_SHORT).show();
            if (getDialog() != null)
                getDialog().setCancelable(true);
            orderBinding.orderCancelProgressBar.setVisibility(View.GONE);
        });
    }

    @Override
    public void onPause() {
        super.onPause();
        dismiss();
    }

    @Override
    public void onStop() {
        super.onStop();
        if (valueEventListener != null) {
            databaseReference.removeEventListener(valueEventListener);
        }
    }
}
