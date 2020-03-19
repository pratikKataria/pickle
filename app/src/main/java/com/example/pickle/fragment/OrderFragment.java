package com.example.pickle.fragment;


import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;
import com.example.pickle.R;
import com.example.pickle.activity.Main.MainActivity;
import com.google.android.material.appbar.AppBarLayout;
import com.synnapps.carouselview.CarouselView;

/**
 * A simple {@link Fragment} subclass.
 */
public class OrderFragment extends Fragment {


    private Toolbar toolbar;


    private boolean isExpanded = true;

    private int [] sampleImages = {
            R.drawable.sale_one,
            R.drawable.sale_two,
            R.drawable.sale_three,
            R.drawable.seal_four
    };

   CarouselView carouselView;

    public OrderFragment() {
        // Required empty public constructor
    }

    private void init_fields(View v) {
        toolbar = v.findViewById(R.id.fragment_order_toolbar);

        carouselView = v.findViewById(R.id.carouselView);

        final Typeface tf = ResourcesCompat.getFont(getContext(), R.font.pacifico_regular);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.menu_main, menu);
        Log.e("order fragment ", "invocation 1 menu");

        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_order, container, false);
        Log.e("order fragment ", "invocation 1");

        init_fields(view);

        setToolbar();

        carouselView.setPageCount(sampleImages.length);

        carouselView.setImageListener((position, imageView) -> {
            imageView.setImageResource(sampleImages[position]);
        });


//        gridView.setAdapter(new ImageAdapter(getContext()));
//
//        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//
//            }
//        });

        return view;
    }

    private void setToolbar() {
        ((AppCompatActivity)getActivity()).setSupportActionBar(toolbar);

        ((AppCompatActivity)getActivity()).getSupportActionBar().setTitle("Pickle India");

        ((AppCompatActivity)getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        ((AppCompatActivity)getActivity()).getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            ((MainActivity)(getActivity())).openDrawer();
        }
        return super.onOptionsItemSelected(item);
    }
}

//abstract class AppBarStateChangeListener implements AppBarLayout.OnOffsetChangedListener {
//
//    public enum State {
//        EXPANDED,
//        COLLAPSED,
//        IDLE
//    }
//
//    private State mCurrentState = State.IDLE;
//
//
//    public abstract void onStateChanged(AppBarLayout appBarLayout, State state);
//
//    @Override
//    public void onOffsetChanged(AppBarLayout appBarLayout, int i) {
//        if (i == 0) {
//            if (mCurrentState != State.EXPANDED)
//                onStateChanged(appBarLayout, State.EXPANDED);
//            mCurrentState = State.EXPANDED;
//        } else if (Math.abs(i) >= appBarLayout.getTotalScrollRange()) {
//            if (mCurrentState != State.COLLAPSED) {
//                onStateChanged(appBarLayout, State.COLLAPSED);
//            }
//            mCurrentState = State.COLLAPSED;
//        } else {
//            if (mCurrentState != State.IDLE) {
//                onStateChanged(appBarLayout, State.IDLE);
//            }
//            mCurrentState = State.IDLE;
//        }
//    }
//
//}
