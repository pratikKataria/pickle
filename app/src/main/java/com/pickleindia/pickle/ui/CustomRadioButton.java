package com.pickleindia.pickle.ui;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.CompoundButton;

import androidx.appcompat.widget.AppCompatRadioButton;

public class CustomRadioButton extends AppCompatRadioButton {

    private OnCheckedChangeListener onCheckedChangeListener;

    public CustomRadioButton(Context context) {
        super(context);
    }

    public CustomRadioButton(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
    }

    public CustomRadioButton(Context context, AttributeSet attributeSet, int defStyleAtter) {
        super(context, attributeSet, defStyleAtter);
    }


    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();

        setOwnCheckedChangeListener();
        setButtonDrawable(null);
    }

    public void setOwnCheckedChangeListener(OnCheckedChangeListener onCheckedChangeListener) {
        this.onCheckedChangeListener = onCheckedChangeListener;
    }

    public void setOwnCheckedChangeListener() {
        setOnCheckedChangeListener(new OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (onCheckedChangeListener != null) {

                    onCheckedChangeListener.onCheckedChanged(buttonView, isChecked);
                }
            }
        });
    }






















}
