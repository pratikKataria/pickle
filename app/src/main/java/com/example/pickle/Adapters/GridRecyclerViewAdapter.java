package com.example.pickle.Adapters;

import android.media.Image;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.content.Context;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pickle.R;
import com.example.pickle.data.GridItem;

import java.util.List;

public class GridRecyclerViewAdapter extends RecyclerView.Adapter<GridRecyclerViewAdapter.GridViewHolder> {

    private Context context;
    private List<GridItem> gridItemList;

    public GridRecyclerViewAdapter(Context context, List<GridItem> gridItemList) {
        this.context = context;
        this.gridItemList = gridItemList;
    }

    @NonNull
    @Override
    public GridViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.grid_card_view_layout, parent, false);
        return new GridViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull GridViewHolder holder, int position) {
        holder.setData(gridItemList.get(position).getName(), gridItemList.get(position).getImage());
    }

    @Override
    public int getItemCount() {
        return gridItemList.size();
    }

    class GridViewHolder extends RecyclerView.ViewHolder {

        TextView textViewTitle;
        ImageView imageView;
        CardView cardView;

        public GridViewHolder(@NonNull View itemView) {
            super(itemView);

            textViewTitle = itemView.findViewById(R.id.grid_view_tv_text);
            imageView = itemView.findViewById(R.id.grid_view_iv_image);
            cardView = itemView.findViewById(R.id.cardView);

            cardView.setElevation(0);

        }

        public void setData(String text, int imageResource) {
            textViewTitle.setText(text);
            imageView.setImageDrawable(context.getResources().getDrawable(imageResource));
        }
    }

}



