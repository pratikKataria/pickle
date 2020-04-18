package com.example.pickle.Adapters;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.Filterable;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;

public class AutoCompleteAdapter extends ArrayAdapter<String> implements Filterable {

    private ArrayList<String> itemList;
    private ArrayList<String> dynamicValues;
    private ArrayFilter filter;

    public AutoCompleteAdapter(@NonNull Context context, int resource, int textViewResourceId, List<String> objects) {
        super(context, resource, textViewResourceId, objects);
        itemList = (ArrayList<String>) objects;
        dynamicValues = new ArrayList<>(itemList);
    }

    @Override
    public int getCount() {
        return itemList.size();
    }

    @Nullable
    @Override
    public String getItem(int position) {
        return itemList.get(position);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        return super.getView(position, convertView, parent);
    }

    @NonNull
    @Override
    public Filter getFilter() {
        if (filter == null) {
            filter = new ArrayFilter();
        }
        return filter;
    }

    private class ArrayFilter extends  Filter {

        private Object lock;

        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            FilterResults results = new FilterResults();

            if (dynamicValues == null) {
                synchronized (lock) {
                    dynamicValues = new ArrayList<>(itemList);
                }
            }

            if (constraint == null || constraint.length() == 0) {
                synchronized ( lock ) {
                    ArrayList<String> suggestions = new ArrayList<>(dynamicValues);
                    results.values = suggestions;
                    results.count = suggestions.size();
                }
            } else {

                final String prefixString = constraint.toString().toString();

                ArrayList<String> values = dynamicValues;
                int count = values.size();


                ArrayList<String> newValues = new ArrayList<>(count);
                for (int i = 0;  i < count; i++) {
                    String item = values.get(i);
                    if (item.toLowerCase().contains(prefixString)) {
                        newValues.add(item);
                    }
                }


                Log.e("AutoCompleAdatper " , newValues +"");

                for (String value : values) {
                    if (StringUtils.getJaroWinklerDistance(value, (String) constraint) > 0.7)
                        newValues.add(value);
                }

                results.values = newValues;
                results.count = newValues.size();

            }
            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
                if (results.values != null) {
                    itemList = (ArrayList<String>) results.values;
                } else {
                    itemList = new ArrayList<>();
                }

                if (results.count > 0) {
                    notifyDataSetChanged();;
                } else {
                    notifyDataSetChanged();
                }
        }
    }

}
