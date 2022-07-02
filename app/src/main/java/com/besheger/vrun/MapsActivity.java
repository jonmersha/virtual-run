package com.besheger.vrun;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.besheger.vrun.databinding.ActivityMapsBinding;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, LocationListener {

    private GoogleMap mMap;
    private ActivityMapsBinding binding;
    private TextView speed;
    private TextView locationName;

    private LocationManager locationManager;
    private String provider;
    private Location location;
    private boolean mapReady;
    private AdView adView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {
            }
        });




        binding = ActivityMapsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        adView = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        adView.loadAd(adRequest);

        adView.setAdListener(new AdListener() {
            @Override
            public void onAdClicked() {
                // Code to be executed when the user clicks on an ad.
            }

            @Override
            public void onAdClosed() {
                // Code to be executed when the user is about to return
                // to the app after tapping on an ad.
            }
            @Override
            public void onAdFailedToLoad(LoadAdError adError) {
                // Code to be executed when an ad request fails.
                 Toast.makeText(MapsActivity.this, "Adds loaded"+adError.toString(), Toast.LENGTH_LONG).show();
            }
            @Override
            public void onAdImpression() {
                // Code to be executed when an impression is recorded
                // for an ad.
            }
            @Override
            public void onAdLoaded() {
                // Code to be executed when an ad finishes loading.

                Toast.makeText(MapsActivity.this, "Adds loaded", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onAdOpened() {
                // Code to be executed when an ad opens an overlay that
                // covers the screen.
            }
        });




        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        speed=findViewById(R.id.textView2);
        locationName=findViewById(R.id.textView);


        /////////////////////////location Setting
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        Criteria criteria = new Criteria();
        provider = locationManager.getBestProvider(criteria, true);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            String[] permission={Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.ACCESS_COARSE_LOCATION};
            ActivityCompat.requestPermissions(MapsActivity.this, permission,1);
            return;
        }

        location = locationManager.getLastKnownLocation(provider);
        locationManager.requestLocationUpdates(provider,1000,5,this);
        if (location != null) {
            //  Toast.makeText(this, "Provider " + provider + "Has been selected.", Toast.LENGTH_SHORT).show();
            onLocationChanged(location);
        } else {
            Toast.makeText(this, "Location not Enabled", Toast.LENGTH_SHORT).show();
        }
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        this.mMap.setMinZoomPreference(17.0f);
        mapReady=true;
        if(location != null){
            String locationsInfo="Addis Ababa";
            Geocoder geo=new Geocoder(getBaseContext().getApplicationContext(), Locale.getDefault());
            List<Address> addresses = null;
            try {
                addresses = geo.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
                locationsInfo = addresses.get(0).getFeatureName()
                        + "," + addresses.get(0).getLocality()
                        + ", " + addresses.get(0).getCountryName();
                locationName.setText(locationsInfo);
            } catch (IOException e) {
                e.printStackTrace();
            }
            LatLng Addis = new LatLng(location.getLatitude(), location.getLongitude());
            mMap.addMarker(new MarkerOptions().position(Addis).title(locationsInfo));
            mMap.moveCamera(CameraUpdateFactory.newLatLng(Addis));
        }
    }

    @Override
    public void onLocationChanged(@NonNull Location location) {
        speed.setText(3.6*location.getSpeed()+"");
        if(this.mapReady)
        {
            Geocoder geo=new Geocoder(getBaseContext().getApplicationContext(), Locale.getDefault());
            List<Address> addresses = null;
            try {
                addresses = geo.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
                String locationsInfo = addresses.get(0).getFeatureName()
                        + "," + addresses.get(0).getLocality()
                        + ", " + addresses.get(0).getAdminArea()
                        + ", " + addresses.get(0).getCountryName();
                locationName.setText(locationsInfo);
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }



}