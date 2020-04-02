package com.example.pickle.activity.Main.NavigationFragment;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.pickle.Adapters.CategoryRecyclerViewAdapter;
import com.example.pickle.R;
import com.example.pickle.data.ProductModel;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 */
public class CartFragment extends Fragment {

    RecyclerView fruitsRecyclerView;
    ArrayList<ProductModel> cartList;
    CategoryRecyclerViewAdapter adapter;

    public CartFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_cart, container, false);
        init_recyclerView(view);
        return view;
    }

    private void init_recyclerView(View view) {
        fruitsRecyclerView = view.findViewById(R.id.recyclerView);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity(), RecyclerView.VERTICAL, false);
        adapter = new CategoryRecyclerViewAdapter(getActivity(), cartList);
        fruitsRecyclerView.setLayoutManager(linearLayoutManager);
        fruitsRecyclerView.setAdapter(adapter);
//        adapter.setOnItemClickListener(position -> {
//            EachItemDataModel eidm = fruitList.get(position);
//            startActivity(new Intent(FootwearActivity.this, PlaceItemOrderActivity.class).putExtra("DATA_MODEL", (Serializable) eidm));
//            Toast.makeText(FootwearActivity.this, "posittion" + position, Toast.LENGTH_SHORT).show();
//        });
    }

}
