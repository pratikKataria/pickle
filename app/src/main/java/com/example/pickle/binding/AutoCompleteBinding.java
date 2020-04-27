package com.example.pickle.binding;

import android.widget.AutoCompleteTextView;

import androidx.databinding.BindingAdapter;

import com.example.pickle.Adapters.AutoCompleteAdapter;

import java.util.List;

public class AutoCompleteBinding {
    @BindingAdapter("autoCompleteAdapter")
    public static void _(AutoCompleteTextView autoCompleteTextView, List<String> suggestionList) {
        if (autoCompleteTextView.getAdapter() == null) {
            return;
        }

        AutoCompleteAdapter adapter = new AutoCompleteAdapter(
                autoCompleteTextView.getContext(),
                android.R.layout.simple_list_item_1,
                android.R.id.text1,
                suggestionList
        );

        autoCompleteTextView.setAdapter(adapter);
    }
}
