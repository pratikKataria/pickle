package com.example.pickle.adapters.viewholders;

import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pickle.interfaces.Visitor;

public abstract class AbstractViewHolder<T> extends RecyclerView.ViewHolder {


    public AbstractViewHolder(@NonNull View itemView) {
        super(itemView);
    }

    public abstract void bind(T element);
}
