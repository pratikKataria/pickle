
package com.pickleindia.pickle.combo;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;

import com.google.android.material.transition.MaterialContainerTransform;
import com.google.android.material.transition.MaterialSharedAxis;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.pickleindia.pickle.DataBinderMapperImpl;
import com.pickleindia.pickle.R;
import com.pickleindia.pickle.cart.CartActivity;
import com.pickleindia.pickle.databinding.FragmentComboOfferBinding;
import com.pickleindia.pickle.models.OfferCombo;
import com.pickleindia.pickle.models.ProductModel;
import com.pickleindia.pickle.utils.NotifyRecyclerItems;

import java.security.PrivateKey;
import java.util.ArrayList;

public class ComboOfferFragment extends Fragment {
    public static String OFFER_COMBO = "offer_combo";
    private OfferCombo offerCombo;
    private ArrayList<ProductModel> comboProductsList = new ArrayList<>();
    private GetDataCallBack getDataCallBack;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            offerCombo = (OfferCombo) getArguments().get(OFFER_COMBO);
        }
        Log.e(OFFER_COMBO, offerCombo.toString());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        FragmentComboOfferBinding binding = DataBindingUtil.inflate(
                inflater,
                R.layout.fragment_combo_offer,
                container,
                false
        );
        setEnterTransition(MaterialSharedAxis.create(MaterialSharedAxis.X, true));
        binding.setOfferCombo(offerCombo);
        binding.setComboProducts(comboProductsList);
        binding.setActivity(getActivity());

        String[] ids_cat = offerCombo.getProductIds_cat().split(" ");
        String[] id = new String[ids_cat.length];
        String[] cat = new String[ids_cat.length];
        for (int i = 0; i < ids_cat.length; i++) {
            id[i] = ids_cat[i].split("_")[0];
            cat[i] = ids_cat[i].split("_")[1];
            Log.e("prduct mOdel ", id[i] + " " + cat[i] + " split " + ids_cat[i].split("_"));
        }

        getDataCallBack = new GetDataCallBack() {
            @Override
            public void received(int index, ProductModel offerProduct) {
                comboProductsList.add(offerProduct);
                NotifyRecyclerItems.notifyItemChangedAt(binding.comboOfferRecyclerView, index);

                if (index < ids_cat.length)
                    next(index);
            }

            @Override
            public void next(int index) {
                getComboProducts(cat[index], id[index], index);
            }
        };
        getDataCallBack.next(0);


        Integer[] counter = new Integer[offerCombo.getMaxQty()];
        for (int maxQtyPerUser = 0; maxQtyPerUser < offerCombo.getMaxQty(); maxQtyPerUser++ ){
            counter[maxQtyPerUser] = maxQtyPerUser+1;
        }

        ArrayAdapter<Integer> adapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_item, counter);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.qtySpinner.setAdapter(adapter);
        binding.qtySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                counter[0] = 1;
                offerCombo.qtyCounter = counter[position];
                binding.totalPriceCounter.setText("Your combo price: \u20b9"+ offerCombo.getTotalPrice() + "x" + offerCombo.qtyCounter + " = \u20b9"+ (offerCombo.qtyCounter*offerCombo.getTotalPrice()));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) { }
        });

        binding.addToCart.setOnClickListener(n -> {
            Bundle bundle = new Bundle();
            bundle.putParcelable("combo_def", offerCombo);
            bundle.putParcelableArrayList(OFFER_COMBO, comboProductsList);
            startActivity(new Intent(getActivity(), CartActivity.class).putExtras(bundle));
        });

        return binding.getRoot();
    }

    private void getComboProducts(String cat, String id, int currIndex) {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Products").child(cat).child(id);
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ProductModel productModel = snapshot.getValue(ProductModel.class);

                if (productModel != null) {
                    productModel.isCombo = true;
                    productModel.setItemId(productModel.getItemId() + "_" + "C");
                    productModel.setItemBasePrice(0);
                    productModel.setItemSellPrice(0);
                    productModel.setItemAvailability(true);
                    getDataCallBack.received(currIndex+1, productModel);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    interface GetDataCallBack {
        void received(int index, ProductModel offerProduct);
        void next(int index);
    }
}