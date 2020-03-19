package com.example.pickle.Adapters;

import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.content.Context;
import android.widget.GridLayout;
import android.widget.GridView;
import android.widget.ImageView;

import com.example.pickle.R;

public class ImageAdapter extends BaseAdapter {

    private Context context;

    public ImageAdapter(Context c) {
        context = c;
    }

    @Override
    public int getCount() {
        return imageThumbs.length;
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ImageView  imageView;

        if (convertView == null) {
            imageView = new ImageView(context);
            imageView.setLayoutParams(new GridView.LayoutParams(250,250));
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            imageView.setPadding(8,8,8,8);
        } else {
            imageView = (ImageView) convertView;
        }
        imageView.setImageDrawable(context.getResources().getDrawable(imageThumbs[position]));
        return imageView;
    }

    public Integer [] imageThumbs = {
            R.drawable.ic_beverages,
            R.drawable.ic_fruit,
            R.drawable.ic_dairy,
            R.drawable.ic_vegetables
    };
}
