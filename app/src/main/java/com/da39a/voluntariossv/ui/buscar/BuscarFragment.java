package com.da39a.voluntariossv.ui.buscar;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.da39a.voluntariossv.R;
import com.da39a.voluntariossv.adapters.Rcv_Busqueda;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;

public class BuscarFragment extends Fragment implements OnMapReadyCallback, GoogleMap.OnMyLocationButtonClickListener {

    private static final String MAP_BUNDLE_KEY = "MAIN_MAP_VIEW";

    MapView mapa;
    GoogleMap googlemap;
    Bundle mapbundle;
    RecyclerView rcv;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_buscar, container, false);
        init(root);
        load(savedInstanceState);
        return root;
    }

    public void init(View v){
        rcv = v.findViewById(R.id.rcv_resultados);
        mapa = v.findViewById(R.id.mapa);
        mapbundle = null;
    }

    public void load(Bundle savedInstanceState){

        solicitarPermisos();

        if(savedInstanceState != null){
            mapbundle = savedInstanceState.getBundle(MAP_BUNDLE_KEY);
        }

        rcv.setHasFixedSize(true);
        rcv.setLayoutManager(new LinearLayoutManager(this.getContext()));
        rcv.setAdapter(new Rcv_Busqueda(this.getContext()));

        mapa.onCreate(mapbundle);
        mapa.getMapAsync(this);
    }


    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        Bundle savedMapBundle = outState.getBundle(MAP_BUNDLE_KEY);
        if(savedMapBundle == null){
            savedMapBundle = new Bundle();
            outState.putBundle(MAP_BUNDLE_KEY,savedMapBundle);
        }
        mapa.onSaveInstanceState(savedMapBundle);
    }

    @SuppressLint("MissingPermission")
    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        googlemap = googleMap;
        googlemap.setMyLocationEnabled(true);
        googleMap.setOnMyLocationButtonClickListener(this);
        googlemap.setMinZoomPreference(15);
        LatLng position = new LatLng(13.670048,-89.293314);
        googlemap.moveCamera(CameraUpdateFactory.newLatLng(position));
        mapa.onResume();
    }

    @SuppressLint("MissingPermission")
    private void solicitarPermisos(){
        if(ActivityCompat.checkSelfPermission(getContext(),Manifest.permission.ACCESS_FINE_LOCATION)!= PackageManager.PERMISSION_GRANTED){
            requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION,Manifest.permission.ACCESS_FINE_LOCATION},1);
        }
    }


    @Override
    public boolean onMyLocationButtonClick() {
        return false;
    }

    @Override
    public void onResume() {
        super.onResume();
        mapa.onResume();
    }

    @Override
    public void onStart() {
        super.onStart();
        mapa.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();
        mapa.onStop();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapa.onLowMemory();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mapa.onDestroy();
    }

    @Override
    public void onPause() {
        super.onPause();
        mapa.onPause();
    }
}
