package com.example.pickle.adapters.viewholders;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.LayoutRes;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.example.pickle.R;
import com.example.pickle.models.EmptyState;

public class EmptyViewHolder extends  AbstractViewHolder<EmptyState>{

    private Context context;
    private ImageView imageView;
    private ConstraintLayout constraintLayout;

    @LayoutRes
    public static final int LAYOUT = R.layout.cardview_empty_order;

    public EmptyViewHolder(View itemView) {
        super(itemView);
        context = itemView.getContext();
        imageView = itemView.findViewById(R.id.cardview_empty_order_img);
        constraintLayout = itemView.findViewById(R.id.cardview_empty_order_constraint_l);
    }

    @Override
    public void bind(EmptyState element) {
        imageView.setImageDrawable(context.getResources().getDrawable(element.getBgImageRes()));
        constraintLayout.setBackground(context.getResources().getDrawable(element.getBackgroundRes()));
    }
}
