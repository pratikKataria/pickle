package com.example.pickle.binding;

import android.widget.AutoCompleteTextView;

import androidx.databinding.BindingAdapter;

import com.example.pickle.adapters.AutoCompleteAdapter;

import java.util.List;

public class AutocompleteBindingAdapter {

    @BindingAdapter("autoCompleteList")
    public static void autoComplete(AutoCompleteTextView autoCompleteTextView, List<String> list) {

        if (list == null ) {
            return;
        }

        AutoCompleteAdapter adapter = new AutoCompleteAdapter(
                autoCompleteTextView.getContext(),
                android.R.layout.simple_list_item_1,
                android.R.id.text1,
                list
        );

        autoCompleteTextView.setAdapter(adapter);
    }

}
