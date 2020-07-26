package com.pickleindia.pickle.about;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;

import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.google.android.gms.oss.licenses.OssLicensesActivity;
import com.google.android.gms.oss.licenses.OssLicensesMenuActivity;
import com.pickleindia.pickle.R;
import com.pickleindia.pickle.databinding.FragmentAboutBinding;

public class AboutFragment extends Fragment {


    public AboutFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        FragmentAboutBinding binding = DataBindingUtil.inflate(
                inflater,
                R.layout.fragment_about,
                container,
                false
        );

        binding.setActivity(getActivity());

        WebView webView = binding.webView;
        webView.loadUrl("https://www.pickleinda.com/about.html");

        binding.license.setOnClickListener(n -> {
            startActivity(new Intent(getActivity(), OssLicensesMenuActivity.class));
        });

        return binding.getRoot();
    }

    @Override
    public void onStart() {
        super.onStart();
        getActivity().getWindow().setStatusBarColor(getResources().getColor(R.color.colorAccent));
    }

    @Override
    public void onStop() {
        super.onStop();
        getActivity().getWindow().setStatusBarColor(Color.WHITE);
    }
}