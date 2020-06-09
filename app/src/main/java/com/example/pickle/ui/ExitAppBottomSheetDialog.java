package com.example.pickle.ui;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;

import com.example.pickle.R;
import com.example.pickle.databinding.BottomSheetQuitAppBinding;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

public class ExitAppBottomSheetDialog extends BottomSheetDialogFragment {

    private BottomSheetOnButtonClickListener bottomSheetOnButtonClickListener;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        BottomSheetQuitAppBinding bottomSheetQuitAppBinding = DataBindingUtil.inflate(
                inflater,
                R.layout.bottom_sheet_quit_app,
                container,
                false
        );


        getDialog().setOnShowListener(dialog -> {
            BottomSheetDialog bottomSheetDialog = (BottomSheetDialog) dialog;
            FrameLayout bottomSheetLayout = bottomSheetDialog.findViewById(com.google.android.material.R.id.design_bottom_sheet);
            if (bottomSheetLayout == null) {
                return;
            }
            bottomSheetLayout.setBackground(null);
        });

        bottomSheetQuitAppBinding.exitButton.setOnClickListener(n-> bottomSheetOnButtonClickListener.onExit());
        bottomSheetQuitAppBinding.cancelButton.setOnClickListener(n -> getDialog().dismiss());
        return bottomSheetQuitAppBinding.getRoot();
    }

    public interface BottomSheetOnButtonClickListener {
        void onExit();
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);

        try {
            bottomSheetOnButtonClickListener = (BottomSheetOnButtonClickListener) context;
        } catch (Exception xe) {
            Log.e(ExitAppBottomSheetDialog.class.getName(), xe.getMessage()+"");
        }
    }
}
