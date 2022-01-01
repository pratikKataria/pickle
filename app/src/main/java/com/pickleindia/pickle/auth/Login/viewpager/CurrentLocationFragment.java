package com.pickleindia.pickle.auth.Login.viewpager;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import com.google.android.material.snackbar.Snackbar;
import com.pickleindia.pickle.auth.Login.CustomerDetailActivity;
import com.pickleindia.pickle.R;
 import com.pickleindia.pickle.databinding.FragmentCurrentLocationBinding;
import com.pickleindia.pickle.models.CurrentAddress;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.pickleindia.pickle.utils.SnackbarNoSwipeBehavior;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;

import mumayank.com.airlocationlibrary.AirLocation;

/**
 * A simple {@link Fragment} subclass.
 */
public class CurrentLocationFragment extends Fragment {

    private AirLocation airLocation;
    private AlertDialog alertDialog;
    private FragmentCurrentLocationBinding currentLocationBinding;

    public CurrentLocationFragment() {
        // Required empty public constructor
    }

    public static CurrentLocationFragment newInstance(boolean updateAddress) {
        CurrentLocationFragment currentLocationFragment = new CurrentLocationFragment();
        Bundle args = new Bundle();
        args.putBoolean("UPDATE_ADDRESS", updateAddress);
        currentLocationFragment.setArguments(args);
        return currentLocationFragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        currentLocationBinding = DataBindingUtil.inflate(
                inflater,
                R.layout.fragment_current_location,
                container,
                false
        );
        currentLocationBinding.setUpdateAddress(getArguments().getBoolean("UPDATE_ADDRESS", false));
        showDialogWhenUserDeniedPermissionAndSelectedNeverShow();
        currentLocationBinding.lottieAnimation.setMaxProgress(.50F);
        currentLocationBinding.lottieAnimation.playAnimation();

        if (getActivity() != null) {
            airLocation = new AirLocation(getActivity(), new AirLocation.Callback() {
                @Override
                public void onSuccess(@NotNull ArrayList<Location> arrayList) {
                    for (Location location : arrayList) {
                        if (location.getLatitude() != 0 && location.getLongitude() != 0) {
                            currentLocationBinding.lottieAnimation.setMinAndMaxProgress(0.60F, 0.75F);
                            currentLocationBinding.lottieAnimation.playAnimation();
                            uploadLocation(location.getLatitude(), location.getLongitude());
                        } else {
                            Toast.makeText(getActivity(), "unable to find location : press Request/Update", Toast.LENGTH_LONG).show();
                        }
                    }
                }

                @Override
                public void onFailure(@NotNull AirLocation.LocationFailedEnum locationFailedEnum) {

                }
            }, true, 500, "permission is required to get the current location");
        }

        currentLocationBinding.updateBtn.setOnClickListener(n -> {
            showDialogWhenUserDeniedPermissionAndSelectedNeverShow();
            if (isLocationEnabled()) {
                currentLocationBinding.updateBtn.setText("Request/Update");
            }
            airLocation.start();
        });
        return currentLocationBinding.getRoot();
    }

    private void uploadLocation(double latitude, double longitude) {
        boolean updateAddress = getArguments().getBoolean("UPDATE_ADDRESS", false);
        CurrentAddress currentAddress = buildCurrentAddress(latitude, longitude);
        if (updateAddress) {
            atomicUpdate(currentAddress);
        } else {
            if (currentLocationBinding.cdEtUserName.getText().toString().trim().isEmpty()) {
                currentLocationBinding.cdEtUserName.setError("should not be empty");
                currentLocationBinding.cdEtUserName.requestFocus();
                return;
            }
            atomicUpdate(currentLocationBinding.cdEtUserName.getText().toString(), currentAddress);
        }
    }

    private CurrentAddress buildCurrentAddress(double latitude, double longitude) {
        String latitudeLongitude = latitude + "," + longitude;
        return new CurrentAddress(latitudeLongitude);
    }

    private void atomicUpdate(CurrentAddress currentAddress) {
        currentLocationBinding.progressBar.setVisibility(View.VISIBLE);

        HashMap<String, Object> update = new HashMap<>();
        String uid = FirebaseAuth.getInstance().getUid();

        update.put("Addresses/" + uid + "/slot2", currentAddress);
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
        ref.updateChildren(update).addOnSuccessListener(task -> {
            currentLocationBinding.progressBar.setVisibility(View.GONE);
            Toast.makeText(getActivity(), "location updated", Toast.LENGTH_SHORT).show();
            showSnackBar();
        }).addOnFailureListener(e -> {
            Toast.makeText(getActivity(), "error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            currentLocationBinding.progressBar.setVisibility(View.GONE);
        });
    }

    private void atomicUpdate(String customerName, CurrentAddress currentAddress) {
        currentLocationBinding.progressBar.setVisibility(View.VISIBLE);

        HashMap<String, Object> update = new HashMap<>();
        String uid = FirebaseAuth.getInstance().getUid();

        update.put("Customers/" + uid +"/personalInformation/username", customerName);
        update.put("Addresses/" + uid + "/slot2", currentAddress);

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
        ref.updateChildren(update).addOnSuccessListener(task -> {
            currentLocationBinding.progressBar.setVisibility(View.GONE);
            Toast.makeText(getActivity(), "location updated", Toast.LENGTH_SHORT).show();
            showSnackBar();
        }).addOnFailureListener(e -> {
            Toast.makeText(getActivity(), "error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            currentLocationBinding.progressBar.setVisibility(View.GONE);
        });
    }

    private void showSnackBar() {
        Snackbar snackbar = Snackbar.make(currentLocationBinding.coordinatorLayout, "Address details is mandatory with current location ", Snackbar.LENGTH_INDEFINITE);
        snackbar.setBehavior(new SnackbarNoSwipeBehavior());
        snackbar.setText("Address details is mandatory with current location");
        snackbar.setAction("fill Address", v -> {
            if (getActivity() instanceof CustomerDetailActivity) {
                ((CustomerDetailActivity) getActivity()).switchPage();
                snackbar.dismiss();
            }
        });
        snackbar.show();
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        airLocation.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == 1101) {
            for (int i = 0; i < permissions.length; i++) {
                boolean isLocationPermissionGranted = permissions[i].equals(Manifest.permission.ACCESS_FINE_LOCATION) && grantResults[i] == PackageManager.PERMISSION_GRANTED;
                if (!isLocationPermissionGranted) {
                    if (!shouldShowRequestPermissionRationale(permissions[i])) {
                        showAlertDialog();
                    }
                }
            }
        }

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        airLocation.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    private void showDialogWhenUserDeniedPermissionAndSelectedNeverShow() {
        requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1101);
    }

    public void showAlertDialog() {
        if (alertDialog != null && alertDialog.isShowing())
            return;

        MaterialAlertDialogBuilder materialAlertDialogBuilder = new MaterialAlertDialogBuilder(getActivity(), R.style.AlertDialogTheme);
        materialAlertDialogBuilder
                .setTitle("Permission Required")
                .setMessage("This Permission is required to the get the current location, please open the setting app to grant permission")
                .setPositiveButton("Open", (dialog, which) -> {
                    Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                    Uri uri = Uri.fromParts("package", getActivity().getPackageName(), null);
                    intent.setData(uri);
                    startActivity(intent);
                }).setNegativeButton("Cancel", ((dialog, which) -> {
        }));
        alertDialog = materialAlertDialogBuilder.create();
        alertDialog.show();
    }

    public boolean isLocationEnabled() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            LocationManager locationManager = getActivity().getSystemService(LocationManager.class);
            return locationManager != null && locationManager.isLocationEnabled();
        } else {
            int mode = 0;
            try {
                mode = Settings.Secure.getInt(getActivity().getContentResolver(), Settings.Secure.LOCATION_MODE);
            } catch (Settings.SettingNotFoundException e) {
                e.printStackTrace();
            }
            return (mode != Settings.Secure.LOCATION_MODE_OFF);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (isLocationEnabled()) {
            currentLocationBinding.updateBtn.setText("Request/Update");
        } else {
            currentLocationBinding.updateBtn.setText("Request Permission/Enable Location");
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (alertDialog != null) {
            alertDialog.dismiss();
            alertDialog = null;
        }
    }
}
