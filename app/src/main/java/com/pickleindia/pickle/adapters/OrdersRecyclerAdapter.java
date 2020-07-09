package com.pickleindia.pickle.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.pickleindia.pickle.adapters.viewholders.AbstractViewHolder;
import com.pickleindia.pickle.interfaces.Visitable;
import com.pickleindia.pickle.interfaces.Visitor;
import java.util.ArrayList;


public class OrdersRecyclerAdapter extends RecyclerView.Adapter<AbstractViewHolder> {

    private Context context;
    private ArrayList<Visitable> visitableArrayList;
    private Visitor visitor;

    public OrdersRecyclerAdapter(Context context, ArrayList<Visitable> visitableArrayList, Visitor visitor) {
        this.context = context;
        this.visitableArrayList = visitableArrayList;
        this.visitor = visitor;
    }

    @NonNull
    @Override
    public AbstractViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(viewType, parent, false);
        return visitor.createViewHolder(view, viewType);
    }

    @Override
    public void onBindViewHolder(@NonNull AbstractViewHolder holder, int position) {
        holder.bind(visitableArrayList.get(position));
    }


    @Override
    public int getItemCount() {
        return visitableArrayList.size();
    }

    @Override
    public int getItemViewType(int position) {
        return visitableArrayList.get(position).accept(visitor);
    }
}
