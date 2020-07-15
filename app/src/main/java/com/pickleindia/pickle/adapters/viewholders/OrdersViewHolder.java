package com.pickleindia.pickle.adapters.viewholders;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.databinding.ObservableInt;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.pickleindia.pickle.R;
import com.pickleindia.pickle.databinding.CardviewOrdersBinding;
import com.pickleindia.pickle.models.Orders;
import com.pickleindia.pickle.ui.OrderDetailsBottomSheet;

import static com.pickleindia.pickle.utils.Constant.ORDERS_DETAILS;

public class OrdersViewHolder extends AbstractViewHolder<Orders> {
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
    public void bind(Orders element) {
        binding.setOrderDetails(element);
        loadThumbImage(element.getOrderDetailsIds(), element.getOrderId());

        final int  totalProductsInt= element.getOrderDetailsIds().split(" ").length;
        String totalProducts = totalProductsInt > 1? "Total products: "+ totalProductsInt : "Total product: "+ totalProductsInt;
        binding.totalProducts.setText(totalProducts);

        element.totalProduct = totalProductsInt;

        binding.cardViewOrders.setOnClickListener(n -> {
            OrderDetailsBottomSheet orderDetailsBottomSheet = new OrderDetailsBottomSheet(element.getOrderId(), element);
            orderDetailsBottomSheet.show(((AppCompatActivity)context).getSupportFragmentManager(), "orderDetails");
        });

        binding.executePendingBindings();
    }

    private void loadThumbImage(String ids, String key) {
        String[] id = ids.split(" ");
        ImageView[] thumbImage = {binding.image1, binding.image2, binding.image3, binding.image4};

        if (id.length == 1) {
            thumbImage[0].setScaleX(0.80F);
            thumbImage[0].setScaleY(0.80F);
            thumbImage[1].setVisibility(View.GONE);
            thumbImage[2].setVisibility(View.GONE);
            thumbImage[3].setVisibility(View.GONE);
        } else {
            thumbImage[0].setScaleX(1F);
            thumbImage[0].setScaleY(1F);
            thumbImage[1].setVisibility(View.VISIBLE);
            thumbImage[2].setVisibility(View.VISIBLE);
            thumbImage[3].setVisibility(View.VISIBLE);
        }

        final ObservableInt index = new ObservableInt(0);
        for (int i = 0; i < 4; i++) {
            if (i <= id.length-1) {
                DatabaseReference imagesLoader = FirebaseDatabase.getInstance().getReference(ORDERS_DETAILS).child(key).child(id[i]).child("itemThumbImage");
                ValueEventListener valueEventListener = new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot imageSnapshot) {
                        Glide.with(context).load(imageSnapshot.getValue(String.class)).into(thumbImage[index.get()]);
                        index.set(index.get() + 1);
                        binding.executePendingBindings();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                };
                imagesLoader.addListenerForSingleValueEvent(valueEventListener);
            }
        }
    }

}
