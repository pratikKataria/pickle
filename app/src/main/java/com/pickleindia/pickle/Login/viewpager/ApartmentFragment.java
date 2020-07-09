package com.pickleindia.pickle.Login.viewpager;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import com.pickleindia.pickle.R;
import com.pickleindia.pickle.databinding.FragmentApartmentBinding;
import com.pickleindia.pickle.models.Address;
import com.pickleindia.pickle.models.ApartmentDataModel;
import com.pickleindia.pickle.models.Customer;
import com.pickleindia.pickle.models.IndividualHouseDataModel;
import com.pickleindia.pickle.models.PersonalInformation;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

import static com.pickleindia.pickle.utils.Constant.APARTMENT;
import static com.pickleindia.pickle.utils.Constant.INDIVIDUAL;

/**
 * A simple {@link Fragment} subclass.
 */

public class ApartmentFragment extends Fragment {

    private FragmentApartmentBinding binding;

    public ApartmentFragment() {
        // Required empty public constructor
    }

    public static ApartmentFragment newInstance(int type, boolean updateAddress) {
        ApartmentFragment apartmentFragment = new ApartmentFragment();
        Bundle args = new Bundle();
        args.putBoolean("UPDATE_ADDRESS", updateAddress);
        args.putInt("ADDRESS_TYPE", type);
        apartmentFragment.setArguments(args);
        return apartmentFragment;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_apartment, container, false);
        boolean updateAddress = getArguments().getBoolean("UPDATE_ADDRESS", false);
        int addressType = getArguments().getInt("ADDRESS_TYPE", APARTMENT);
        binding.setUpdateAddress(updateAddress);
        binding.setType(addressType);

        binding.cdMbSave.setOnClickListener(n -> {
            if (!updateAddress && binding.cdEtUserName.getText().toString().trim().isEmpty()) {
                binding.cdEtUserName.setError("should not be empty");
                binding.cdEtUserName.requestFocus();
                return;
            }

            if (!(binding.cdEtPinCode.getText().toString().trim().length() == 6)) {
                binding.cdEtPinCode.setError("incorrect number");
                binding.cdEtPinCode.requestFocus();
                return;
            }

            if (binding.cdEtAddress.getText().toString().trim().isEmpty()) {
                binding.cdEtAddress.setError("should not be empty");
                binding.cdEtAddress.requestFocus();
                return;
            }

            if (updateAddress)
                atomicUpdate(buildAddress());
            else
                atomicUpdate(buildCustomerData(), buildAddress());
        });

        return binding.getRoot();
    }

    private Address buildAddress() {
        Address address = null;
        int addressType = getArguments().getInt("ADDRESS_TYPE");

        if (addressType == APARTMENT) {
            address = new ApartmentDataModel(
                    binding.cdEtApartment.getText().toString(),
                    binding.cdEtPinCode.getText().toString(),
                    binding.cdEtFlatHouseNo.getText().toString(),
                    binding.cdEtAddress.getText().toString(),
                    binding.cdEtInstruction.getText().toString(),
                    "apartment or gated society");
        } else if (addressType == INDIVIDUAL) {
            address = new IndividualHouseDataModel(
                    binding.cdEtPinCode.getText().toString(),
                    binding.cdEtAddress.getText().toString(),
                    binding.cdEtInstruction.getText().toString(),
                    binding.cdEtFlatHouseNo.getText().toString(),
                    "individualHouse");
        }

        return address;
    }

    private Customer buildCustomerData() {
        return new Customer(new PersonalInformation(
                binding.cdEtUserName.getText().toString(),
                FirebaseAuth.getInstance().getUid(),
                FirebaseInstanceId.getInstance().getToken(),
                new SimpleDateFormat("dd : MM : YYYY ").format(new Date()),
                FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber()));
    }

    private void atomicUpdate(Address apartmentDataModel) {
        binding.progressBar.setVisibility(View.VISIBLE);

        HashMap<String, Object> update = new HashMap<>();
        update.put("Addresses/" + FirebaseAuth.getInstance().getUid() + "/slot1", apartmentDataModel);

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
        ref.updateChildren(update).addOnSuccessListener(task -> {
            binding.progressBar.setVisibility(View.GONE);
            Toast.makeText(getActivity(), "details updated", Toast.LENGTH_SHORT).show();
            getActivity().finish();
        }).addOnFailureListener(e -> {
            Toast.makeText(getActivity(), "error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            binding.progressBar.setVisibility(View.GONE);
        });
    }

    private void atomicUpdate(Customer customer, Address apartmentDataModel) {
        binding.progressBar.setVisibility(View.VISIBLE);

        HashMap<String, Object> update = new HashMap<>();
        update.put("Customers/" + FirebaseAuth.getInstance().getUid(), customer);
        update.put("Addresses/" + FirebaseAuth.getInstance().getUid() + "/slot1", apartmentDataModel);

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
        ref.updateChildren(update).addOnSuccessListener(task -> {
            binding.progressBar.setVisibility(View.GONE);
            Toast.makeText(getActivity(), "details updated", Toast.LENGTH_SHORT).show();
            getActivity().finish();
        }).addOnFailureListener(e -> {
            Toast.makeText(getActivity(), "error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            binding.progressBar.setVisibility(View.GONE);
        });
    }
}
