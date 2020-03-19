package com.example.pickle.fragment;


import android.graphics.Typeface;
import android.os.Bundle;
import android.util.DisplayMetrics;
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
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pickle.Adapters.GridRecyclerViewAdapter;
import com.example.pickle.R;
import com.example.pickle.Utility;
import com.example.pickle.activity.Main.MainActivity;
import com.example.pickle.data.GridItem;
import com.google.android.material.appbar.AppBarLayout;
import com.synnapps.carouselview.CarouselView;
import android.content.Context;
import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class OrderFragment extends Fragment {


    private Toolbar toolbar;
    RecyclerView recyclerView;
    List<GridItem> list;


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

        recyclerView = v.findViewById(R.id.recyclerView);

        list = new ArrayList<>();


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

        populateList();

        int mNoOfColumns = Utility.calculateNoOfColumns(getContext(), 130 );
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getActivity(), mNoOfColumns);
        recyclerView.setLayoutManager(gridLayoutManager);
        GridRecyclerViewAdapter gridRecyclerViewAdapter = new GridRecyclerViewAdapter(getActivity(), list);
        recyclerView.setAdapter(gridRecyclerViewAdapter);

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

    private void populateList() {

        list.add(new GridItem("fruits", R.drawable.ic_fruit));
        list.add(new GridItem("vegetables", R.drawable.ic_vegetables));
        list.add(new GridItem("beverages", R.drawable.ic_beverages));
        list.add(new GridItem("dairy", R.drawable.ic_dairy));
        list.add(new GridItem("dairy", R.drawable.ic_dairy));

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
