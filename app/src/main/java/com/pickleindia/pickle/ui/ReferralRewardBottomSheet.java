package com.pickleindia.pickle.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.pickleindia.pickle.R;
import com.pickleindia.pickle.databinding.BottomSheetReferralRewardBinding;

public class ReferralRewardBottomSheet extends BottomSheetDialogFragment {

    BottomSheetOnButtonClickListener bottomSheetOnButtonClickListener;

    public ReferralRewardBottomSheet() {

    }

    public ReferralRewardBottomSheet(BottomSheetOnButtonClickListener bottomSheetOnButtonClickListener) {
        this.bottomSheetOnButtonClickListener = bottomSheetOnButtonClickListener;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        BottomSheetReferralRewardBinding bottomSheetReferralRewardBinding = DataBindingUtil.inflate(
                inflater,
                R.layout.bottom_sheet_referral_reward,
                container,
                false
        );

        if (getDialog() != null) {
            getDialog().setOnShowListener(dialog -> {
                BottomSheetDialog bottomSheetDialog = (BottomSheetDialog) dialog;
                FrameLayout bottomSheetLayout = bottomSheetDialog.findViewById(com.google.android.material.R.id.design_bottom_sheet);
                if (bottomSheetLayout == null) {
                    return;
                }

                bottomSheetLayout.setBackground(null);
            });
        }

        bottomSheetReferralRewardBinding.collectRewardMaterialButton.setOnClickListener(n -> {
            bottomSheetOnButtonClickListener.onClick();
        });

        return bottomSheetReferralRewardBinding.getRoot();
    }

    public interface BottomSheetOnButtonClickListener {
        void onClick();
    }

}
