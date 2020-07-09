package com.pickleindia.pickle.Login;

import android.os.Bundle;
import android.util.Log;
import android.view.WindowManager;
import android.widget.RadioGroup;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import com.pickleindia.pickle.Login.viewpager.ViewPagerAdapter;
import com.pickleindia.pickle.R;
import com.pickleindia.pickle.ui.ZoomOutPageTransformer;

public class CustomerDetailActivity extends AppCompatActivity {

    ViewPager2 viewPager;

    private RadioGroup radioGroupApartment;
    private RadioGroup _radioGroupInner;
    private RadioGroup.OnCheckedChangeListener checkedChangeListenerApartment;
    private RadioGroup.OnCheckedChangeListener checkedChangeListener2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_detail);

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);

        viewPager = findViewById(R.id.viewPager2);
        viewPager.setAdapter(createAdapter());
        viewPager.setUserInputEnabled(false);
        viewPager.setPageTransformer(new ZoomOutPageTransformer());

        radioGroupApartment = findViewById(R.id.radioGroup_apartment);
        _radioGroupInner = findViewById(R.id.radioGroup_inner);


        checkedChangeListenerApartment = (group, checkedId) -> {
            Log.e("Customer Details", " id" + (findViewById((group.getCheckedRadioButtonId()))).getTag().toString());
            viewPager.setCurrentItem(Integer.parseInt(((findViewById((group.getCheckedRadioButtonId())))).getTag().toString()));
            _radioGroupInner.setOnCheckedChangeListener(null);
            _radioGroupInner.clearCheck();
            _radioGroupInner.setOnCheckedChangeListener(checkedChangeListener2);
        };

        checkedChangeListener2 = (group, checkedId) -> {
            Log.e("Customer Details @2 ", " id" + (findViewById((group.getCheckedRadioButtonId()))).getTag().toString());
            viewPager.setCurrentItem(Integer.parseInt((findViewById((group.getCheckedRadioButtonId()))).getTag().toString()));
            radioGroupApartment.setOnCheckedChangeListener(null);
            radioGroupApartment.clearCheck();
            radioGroupApartment.setOnCheckedChangeListener(checkedChangeListenerApartment);
        };

        radioGroupApartment.setOnCheckedChangeListener(checkedChangeListenerApartment);
        _radioGroupInner.setOnCheckedChangeListener(checkedChangeListener2);

    }

    private ViewPagerAdapter createAdapter() {
        ViewPagerAdapter adapter;
        if (getIntent().getExtras() != null && getIntent().getExtras().getBoolean("UPDATE_ADDRESS", false)) {
            adapter = new ViewPagerAdapter(getSupportFragmentManager(), getLifecycle(), true);
        } else {
            adapter = new ViewPagerAdapter(getSupportFragmentManager(), getLifecycle());
        }
        return adapter;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}
