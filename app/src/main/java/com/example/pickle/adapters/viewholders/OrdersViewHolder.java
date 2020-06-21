package com.example.pickle.adapters.viewholders;

import android.content.Context;
import android.view.View;
import android.widget.Toast;
import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;

import com.example.pickle.R;
import com.example.pickle.databinding.CardviewOrdersBinding;
import com.example.pickle.utils.OrderStatus;
import com.example.pickle.models.OrdersDetails;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import java.util.HashMap;
import java.util.Map;
import static com.example.pickle.utils.Constant.ORDERS;
import static com.example.pickle.utils.Constant.ORDERS_CANCELLED;
import static com.example.pickle.utils.Constant.PRODUCT;
import static com.example.pickle.utils.OrderStatus.CANCEL;

public class OrdersViewHolder extends AbstractViewHolder<OrdersDetails> {
    @LayoutRes
    public static final int LAYOUT = R.layout.cardview_orders;

    private CardviewOrdersBinding binding;
    private Context context;

    public OrdersViewHolder(CardviewOrdersBinding binding) {
        super(binding.getRoot());
        this.binding = binding;
        context = binding.getRoot().getContext();
    }

    @Override
    public void bind(OrdersDetails element) {
        binding.setOrderDetails(element);
        setName(element.getItemCategory(), element.getItemId());
        binding.cancelButtonMb.setOnClickListener(n -> showAlertDialog());
        binding.executePendingBindings();
    }

    private void setName(String productCategory, String productId) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference(PRODUCT +"/"+ productCategory+"/"+productId+"/itemName");
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String name = dataSnapshot.getValue(String.class);
                binding.setName(name);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void showAlertDialog() {
        AlertDialog alertDialog = null;
        MaterialAlertDialogBuilder materialAlertDialogBuilder = new MaterialAlertDialogBuilder(context, R.style.AlertDialogTheme);
        materialAlertDialogBuilder.setTitle("Would like to cancel the order?");
        materialAlertDialogBuilder.setMessage("Canceling the order would take some time to and notifies after a while.");
        materialAlertDialogBuilder.setPositiveButton("Cancel Order", (dialog, which) -> {
                cancelOrder(binding.getOrderDetails().orderId);
        }).setNegativeButton("Back", (dialog, which) -> { });

        alertDialog = materialAlertDialogBuilder.create();
        alertDialog.show();
    }

    private void cancelOrder(String ordersId) {
        binding.progressBar.setVisibility(View.VISIBLE);
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();

        Map<String, Object> atomicUpdate = new HashMap<>();
        atomicUpdate.put(ORDERS + "/" + ordersId + "/" + "orderStatus", CANCEL);
        atomicUpdate.put(ORDERS_CANCELLED + "/" + ordersId + "/" + "orderId", ordersId);
        atomicUpdate.put(ORDERS_CANCELLED + "/" + ordersId + "/" + "date", ServerValue.TIMESTAMP);

        databaseReference.updateChildren(atomicUpdate).addOnSuccessListener(task -> {
            Toast.makeText(context, "order cancel successfully", Toast.LENGTH_SHORT).show();
            binding.orderStatusTv.setText("processing cancellation");
            binding.getOrderDetails().status = CANCEL;
            binding.cancelButtonMb.setVisibility(View.GONE);
            binding.progressBar.setVisibility(View.GONE);
        }).addOnFailureListener(taskFailed -> {
            Toast.makeText(context, "failed to cancel order", Toast.LENGTH_SHORT).show();
            binding.progressBar.setVisibility(View.GONE);
        });
    }
}
