package com.example.pickle.Login;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.WindowManager;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;
import com.example.pickle.R;
import com.example.pickle.cart.CartActivity;
import com.example.pickle.ui.ZoomOutPageTransformer;
import com.example.pickle.Login.viewpager.ViewPagerAdapter;

public class CustomerDetailActivity extends AppCompatActivity {

    ViewPager2 viewPager;

    private RadioGroup _radioGroupOuter;
    private RadioGroup _radioGroupInner;
    private RadioGroup.OnCheckedChangeListener checkedChangeListener1;
    private RadioGroup.OnCheckedChangeListener checkedChangeListener2;
    private RadioButton _apartment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_detail);

        getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN
        );

        viewPager = findViewById(R.id.viewPager2);
        viewPager.setAdapter(createAdapter());
        viewPager.setUserInputEnabled(false);
        viewPager.setPageTransformer(new ZoomOutPageTransformer());

        _radioGroupOuter = findViewById(R.id.radioGroup_outer);
        _radioGroupInner = findViewById(R.id.radioGroup_inner);


        checkedChangeListener1 = (group, checkedId) -> {
            Log.e("Customer Details", " id" + (findViewById((group.getCheckedRadioButtonId()))).getTag().toString());
            viewPager.setCurrentItem(Integer.parseInt(((findViewById((group.getCheckedRadioButtonId())))).getTag().toString()));
            _radioGroupInner.setOnCheckedChangeListener(null);
            _radioGroupInner.clearCheck();
            _radioGroupInner.setOnCheckedChangeListener(checkedChangeListener2);
        };

        checkedChangeListener2 = (group, checkedId) -> {
            Log.e("Customer Details @2 ", " id" + (findViewById((group.getCheckedRadioButtonId()))).getTag().toString());
            viewPager.setCurrentItem(Integer.parseInt((findViewById((group.getCheckedRadioButtonId()))).getTag().toString()));
            _radioGroupOuter.setOnCheckedChangeListener(null);
            _radioGroupOuter.clearCheck();
            _radioGroupOuter.setOnCheckedChangeListener(checkedChangeListener1);
        };

        _radioGroupOuter.setOnCheckedChangeListener(checkedChangeListener1);
        _radioGroupInner.setOnCheckedChangeListener(checkedChangeListener2);

    }

    private ViewPagerAdapter createAdapter() {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager(), getLifecycle());
        return adapter;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}
