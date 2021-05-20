package com.da39a.voluntariossv.ui.buscar;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
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
import com.da39a.voluntariossv.firebase.Realtimedb;
import com.da39a.voluntariossv.modelos.Aviso;
import com.da39a.voluntariossv.modelos.Institucion;
import com.da39a.voluntariossv.utils.Calculos;
import com.da39a.voluntariossv.utils.Conversiones;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class BuscarFragment extends Fragment implements OnMapReadyCallback, GoogleMap.OnMyLocationButtonClickListener, ValueEventListener, GoogleMap.OnMyLocationChangeListener {

    private static final String MAP_BUNDLE_KEY = "MAIN_MAP_VIEW";

    MapView mapa;
    Bundle mapbundle;
    RecyclerView rcv;
    List<Aviso> avisos,temp;
    GoogleMap googlemap;
    Rcv_Busqueda adapter;
    DatabaseReference ref;
    LatLng currentLocation = null;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_buscar, container, false);

        init(root);
        load(savedInstanceState);
        return root;
    }

    public void init(View v){
        mapbundle = null;
        temp = new ArrayList<>();
        avisos = new ArrayList<>();
        mapa = v.findViewById(R.id.mapa);
        ref = new Realtimedb().getAvisos();
        rcv = v.findViewById(R.id.rcv_resultados);
        adapter = new Rcv_Busqueda(this.getContext(),avisos);
    }

    public void load(Bundle savedInstanceState){

        solicitarPermisos();

        if(savedInstanceState != null){
            mapbundle = savedInstanceState.getBundle(MAP_BUNDLE_KEY);
        }

        rcv.setHasFixedSize(true);
        rcv.setLayoutManager(new LinearLayoutManager(this.getContext()));
        rcv.setAdapter(adapter);

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
        googleMap.setOnMyLocationChangeListener(this);
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
    public void onDataChange(@NonNull DataSnapshot snapshot) {
        if(snapshot.getKey().equals("avisos")){
            temp.clear();
            for (DataSnapshot d: snapshot.getChildren()) {
                temp.add(new Aviso(d));
            }
            new Realtimedb().getInstituciones().addListenerForSingleValueEvent(this);
        }else{
            avisos.clear();
            for (DataSnapshot d: snapshot.getChildren()) {
                int n = temp.size();
                for(int i = 0; i < n; i++){
                    Aviso av = temp.get(i);
                    if(d.getKey().equals(av.getInstitucionFK())){
                        Institucion institucion = new Institucion(d);

                        LatLng itPosicion = new LatLng(institucion.getLatitud(),institucion.getLongitud());
                        double metros = Calculos.getDistancia(currentLocation,itPosicion);

                        if(metros <= 2000){
                            av.setInstitucion(new Institucion(d));
                            av.setExtra(Conversiones.metrosToDistanciaLabel(metros));
                            avisos.add(av);
                        }

                    }
                }
            }
            adapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onMyLocationChange(@NonNull Location location) {
        if(currentLocation == null){
            currentLocation = new LatLng(location.getLatitude(),location.getLongitude());
            ref.addListenerForSingleValueEvent(this);
        }
    }

    @Override
    public boolean onMyLocationButtonClick() {
        currentLocation = null;
        return false;
    }

    @Override
    public void onCancelled(@NonNull DatabaseError error) {

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
