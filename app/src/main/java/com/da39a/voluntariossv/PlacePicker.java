package com.da39a.voluntariossv;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.view.View;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class PlacePicker extends FragmentActivity implements OnMapReadyCallback, GoogleMap.OnMyLocationChangeListener, GoogleMap.OnMapClickListener, View.OnClickListener {

    private GoogleMap mMap;
    private LatLng position;
    FloatingActionButton fab;
    Intent resultIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_place_picker);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);


        fab = findViewById(R.id.fabPicker);
        fab.setOnClickListener(this);
        fab.hide();

        resultIntent = new Intent();
    }


    @SuppressLint("MissingPermission")
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setMyLocationEnabled(true);
        mMap.setMinZoomPreference(12);
        mMap.setOnMapClickListener(this);
        mMap.setOnMyLocationChangeListener(this);
    }


    @Override
    public void onMyLocationChange(@NonNull Location location) {
        if(position == null){
            position = new LatLng(location.getLatitude(),location.getLongitude());
            //mMap.moveCamera(CameraUpdateFactory.newLatLng(position));
            mMap.addMarker(new MarkerOptions().position(position).title("Tu Ubicación"));
        }
    }

    @Override
    public void onMapClick(@NonNull LatLng latLng) {
        mMap.clear();
        position = latLng;
        mMap.addMarker(new MarkerOptions().position(position).title("Mi institución"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(position));

        fab.show();

        resultIntent.putExtra("latitud",position.latitude);
        resultIntent.putExtra("longitud",position.longitude);

    }

    @Override
    public void onClick(View v) {
        setResult(0,resultIntent);
        finish();
    }
}