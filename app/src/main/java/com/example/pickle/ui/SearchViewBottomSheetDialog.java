package com.example.pickle.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;

import com.example.pickle.R;
import com.example.pickle.databinding.BottomSheetSearchViewBinding;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

public class SearchViewBottomSheetDialog extends BottomSheetDialogFragment {

    private BottomSheetSearchViewBinding searchViewBinding;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        searchViewBinding = DataBindingUtil.inflate(
                inflater,
                R.layout.bottom_sheet_search_view,
                container,
                false
        );

        return searchViewBinding.getRoot();
    }
}
