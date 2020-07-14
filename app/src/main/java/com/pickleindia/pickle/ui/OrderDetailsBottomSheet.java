package com.pickleindia.pickle.ui;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.pickleindia.pickle.R;
import com.pickleindia.pickle.adapters.VisitorForList;
import com.pickleindia.pickle.databinding.BottomSheetOrderDetailsBinding;
import com.pickleindia.pickle.interfaces.Visitable;
import com.pickleindia.pickle.models.Orders;
import com.pickleindia.pickle.models.OrdersDetails;
import com.pickleindia.pickle.utils.NotifyRecyclerItems;

import java.util.ArrayList;

import static com.pickleindia.pickle.utils.Constant.ORDERS_DETAILS;

public class OrderDetailsBottomSheet extends BottomSheetDialogFragment {

    private String orderId;
    private Orders orders;
    private ArrayList<Visitable> orderedProductList;

    private DatabaseReference databaseReference;
    private ValueEventListener valueEventListener;

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
        BottomSheetOrderDetailsBinding orderBinding = DataBindingUtil.inflate(inflater,
                R.layout.bottom_sheet_order_details,
                container,
                false);

        setNullBackground();
        orderedProductList = new ArrayList<>();
        orderBinding.setOrdersList(orderedProductList);
        orderBinding.setVisitor(new VisitorForList());
        orderBinding.setOrders(orders);

        int calcFinalTotal = (orders.getSubTotal() + orders.getShipping()) - orders.getPcoinsSpent();

        if (calcFinalTotal > 0) {
            orderBinding.finalTotalAmount.setText(String.valueOf(calcFinalTotal));
        } else {
            orderBinding.finalTotalAmount.setText("invalid");
        }

        databaseReference = FirebaseDatabase.getInstance().getReference(ORDERS_DETAILS).child(orderId);
        valueEventListener = databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    OrdersDetails ordersDetails = dataSnapshot.getValue(OrdersDetails.class);
                    orderedProductList.add(ordersDetails);
                    NotifyRecyclerItems.notifyItemInsertedAt(orderBinding.recyclerView, orderedProductList.indexOf(ordersDetails));
                }
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

    @Override
    public void onStop() {
        super.onStop();
        if (valueEventListener != null) {
            databaseReference.removeEventListener(valueEventListener);
        }
    }
}
