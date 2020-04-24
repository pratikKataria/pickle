package com.example.pickle.activity.Login.viewpager;

import android.Manifest;
import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import com.airbnb.lottie.LottieAnimationView;
import com.example.pickle.LocationTracker;
import com.example.pickle.R;
import com.example.pickle.activity.Main.MainActivity;
import com.example.pickle.data.CurrentAddress;
import com.example.pickle.data.Customer;
import com.example.pickle.data.PersonalInformation;
import com.example.pickle.utils.CurrentLocationUtils;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;

import java.text.SimpleDateFormat;
import java.util.Date;

import static android.content.Context.LOCATION_SERVICE;

/**
 * A simple {@link Fragment} subclass.
 */
public class CurrentLocationFragment extends Fragment implements LocationListener, ValueAnimator.AnimatorUpdateListener {


    private TextView textView;
    private TextView locationFound;
    private ObjectAnimator animationY;
    private AnimatorSet bouncer;
    private ObjectAnimator animationX;
    private ObjectAnimator fadeOut;
    private ObjectAnimator fadeIn;
    private LottieAnimationView lottie;
    private AnimatorSet newAnimSet;
    private MaterialButton updateBtn;
    private CurrentLocationUtils currentLocationUtils;
    private LocationTracker locationTracker;
    private ProgressBar progressBar;

    private static final long MIN_TIME_BW_UPDATES = 1000 * 60 * 1;
    private static final long MIN_DISTANCE_CHANGE_FOR_UPDATES = 10;
    private String location;
    private EditText username;


    public CurrentLocationFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_current_location, container, false);

        lottie = view.findViewById(R.id.lottie_current_location);
        locationFound = view.findViewById(R.id.location_found);
        textView = view.findViewById(R.id.textView);
        updateBtn = view.findViewById(R.id.updateBtn);

        username = view.findViewById(R.id.cd_et_userName);
        progressBar = view.findViewById(R.id.progressBar);

        locationTracker = new LocationTracker(getActivity());

        if(!locationTracker.canGetLocation())
            locationTracker.showSettingsAlert();
        updateBtn.animate().alphaBy(1).translationY(-25).setDuration(1000).start();
        updateBtn.setOnClickListener(n -> {
            lottie.playAnimation();
            lottie.addAnimatorUpdateListener(this);
            lottie.setSpeed(2);
            LocationManager locationManager = (LocationManager) getActivity().getSystemService(LOCATION_SERVICE);
            if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {

                if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                }
                locationManager.requestLocationUpdates(
                        LocationManager.GPS_PROVIDER,
                        MIN_TIME_BW_UPDATES,
                        MIN_DISTANCE_CHANGE_FOR_UPDATES, this);

                if (locationManager != null) {
                    Location loc;
                    loc = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                    if (loc != null) {
                        location = loc.getLatitude() + "," + loc.getLongitude();
                        Log.e("new Location ", location);
                        if (username.getText().toString().isEmpty()) {
                            username.setError("should not be empty");
                            username.requestFocus();
                            return;
                        }
                        updateBtn.setText("Updating");
                        if (loc.getLatitude() > 0 && loc.getLongitude() > 0) {
                            uploadLocation();
                        } else
                            Toast.makeText(getActivity(), "updating... retry", Toast.LENGTH_SHORT).show();
                        playLottieAnimation();
                    } else
                        Toast.makeText(getActivity(), "updating... retry", Toast.LENGTH_SHORT).show();
                    playLottieAnimation();
                } else
                    Toast.makeText(getActivity(), "updating... retry", Toast.LENGTH_SHORT).show();
                playLottieAnimation();
            } else {
                updateBtn.setText("Retry");
                playLottieAnimation();
                locationTracker.showSettingsAlert();
            }

        });

        return view;
    }

    private void uploadLocation() {

        Customer customer = new Customer(
                new PersonalInformation(
                        username.getText().toString(),
                        FirebaseAuth.getInstance().getUid(),
                        FirebaseInstanceId.getInstance().getToken(),
                        new SimpleDateFormat("dd : MM : YYYY ").format(new Date()),
                        FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber()
                )
        );

        progressBar.setVisibility(View.VISIBLE);

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Customers").child(FirebaseAuth.getInstance().getUid());
        ref.child("personalInformation").setValue(customer).addOnSuccessListener(task -> {
            updateAddress();
        }).addOnFailureListener(e -> {
            Toast.makeText(getActivity(), "error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            updateBtn.setText("update");
            progressBar.setVisibility(View.GONE);
        });
    }

    private void updateAddress() {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Addresses");
        ref.child(FirebaseAuth.getInstance().getUid()).child("slot1").setValue(new CurrentAddress(location)).addOnSuccessListener(task -> {
            progressBar.setVisibility(View.GONE);
            updateBtn.setText("update");
            Toast.makeText(getActivity(), "details updated", Toast.LENGTH_SHORT).show();

            startActivity(new Intent(getActivity(), MainActivity.class));
            getActivity().finish();
        }).addOnFailureListener(e -> {
            Toast.makeText(getActivity(), "error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            progressBar.setVisibility(View.GONE);
        });
    }

    private void playLottieAnimation() {
        lottie.playAnimation();
        lottie.addAnimatorUpdateListener(this);
    }

    @Override
    public void onResume() {
        super.onResume();

        animationY = ObjectAnimator.ofFloat(textView, "translationY", -50f);
        animationY.setInterpolator(new AccelerateInterpolator());
        animationY.setDuration(1000);

        animationX = ObjectAnimator.ofFloat(textView, "translationX", -125f);
        animationX.setDuration(1000);

        fadeOut = ObjectAnimator.ofFloat(textView, "alpha", 1f, 0f);
        fadeOut.setDuration(2000);

        fadeIn = ObjectAnimator.ofFloat(textView, "alpha", 0f, 1f);
        fadeIn.setDuration(2000);

        bouncer = new AnimatorSet();
        bouncer.play(animationY).before(animationX);
        bouncer.setTarget(textView);
        bouncer.play(animationX).after(11000).with(fadeOut);
        bouncer.start();

        playLottieAnimation();

        bouncer.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
                Log.e("current location fragment ", "" + animation.getDuration());
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                locationFound.setVisibility(View.VISIBLE);
                newAnimSet = new AnimatorSet();
                newAnimSet.play(animationY.setDuration(0));
                newAnimSet.play(animationX.setDuration(1000)).with(fadeIn);
                newAnimSet.setTarget(locationFound);
                newAnimSet.start();
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });

    }

    @Override
    public void onPause() {
        super.onPause();
        updateBtn.setText("update");
        Log.e("onpause" , "paused........... ");
    }

    @Override
    public void onLocationChanged(Location location) {

    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }

    @Override
    public void onAnimationUpdate(ValueAnimator animation) {
        int progress = (int) (Float.parseFloat(animation.getAnimatedValue().toString()) * 100);
        if (progress > 75) {
            lottie.pauseAnimation();
        }
    }
}
