package com.da39a.voluntariossv.ui.buscar;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.widget.NestedScrollView;
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
import com.da39a.voluntariossv.xml.CustomMapView;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class BuscarFragment extends Fragment implements OnMapReadyCallback, GoogleMap.OnMyLocationButtonClickListener, ValueEventListener, GoogleMap.OnMyLocationChangeListener {

    private static final String MAP_BUNDLE_KEY = "MAIN_MAP_VIEW";

    CustomMapView mapa;
    Bundle mapbundle;
    RecyclerView rcv;
    List<Aviso> avisos,temp;
    GoogleMap googlemap;
    Rcv_Busqueda adapter;
    DatabaseReference ref;
    LatLng currentLocation = null;
    SeekBar seekradio;
    TextView seekindicator;
    int radiomts = 1000;
    NestedScrollView nestedscrollview;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_buscar, container, false);

        init(root);
        if(savedInstanceState != null) mapbundle = savedInstanceState.getBundle(MAP_BUNDLE_KEY);
        solicitarPermisos();
        return root;
    }

    public void init(View v){
        mapbundle = null;
        temp = new ArrayList<>();
        avisos = new ArrayList<>();
        mapa = v.findViewById(R.id.mapa);
        ref = new Realtimedb().getAvisos();
        rcv = v.findViewById(R.id.rcv_resultados);
        seekradio = v.findViewById(R.id.seekradio);
        seekindicator = v.findViewById(R.id.seekindicator);
        nestedscrollview = v.findViewById(R.id.nestedscrollview);
        adapter = new Rcv_Busqueda(this.getContext(),avisos);

        mapa.onCreate(mapbundle);
    }

    @SuppressLint("MissingPermission")
    private void solicitarPermisos(){
        boolean finelocation = ActivityCompat.checkSelfPermission(getContext(),Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED;
        boolean coarselocation = ActivityCompat.checkSelfPermission(getContext(),Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED;

        if(finelocation && coarselocation){
            load();
        }else{
            requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION,Manifest.permission.ACCESS_FINE_LOCATION},1);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode == 1){
            if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                if(grantResults.length > 1 && grantResults[1] == PackageManager.PERMISSION_GRANTED){
                    load();
                }
            }
        }
    }

    public void load(){
        rcv.setHasFixedSize(true);
        rcv.setLayoutManager(new LinearLayoutManager(this.getContext()));
        rcv.setAdapter(adapter);

        seekradio.setOnSeekBarChangeListener(new SeekRadioChange());
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
    }



    @Override
    public void onDataChange(@NonNull DataSnapshot snapshot) {

        if(currentLocation != null){
            avisos.clear();
            googlemap.clear();
            for (DataSnapshot d : snapshot.getChildren()){
                Aviso aviso = new Aviso(d);
                Institucion institucion = aviso.getInstitucion();


                double metros = Calculos.getDistancia(currentLocation,new LatLng(institucion.getLatitud(),institucion.getLongitud()));
                boolean isExp = Calculos.isExpired(aviso.getExpiracion());

                if(metros <= radiomts && !isExp){
                    aviso.setExtra(Conversiones.metrosToDistanciaLabel(metros));
                    addPin(institucion);
                    avisos.add(aviso);
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

    public class SeekRadioChange implements SeekBar.OnSeekBarChangeListener{
        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {

        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
            switch (seekBar.getProgress()){
                case 1:
                    radiomts = 500;
                    seekindicator.setText("Radio de distancia: 500 metros");
                    break;
                case 2:
                    radiomts = 1000;
                    seekindicator.setText("Radio de distancia: 1 kilometro");
                    break;
                case 3:
                    radiomts = 2000;
                    seekindicator.setText("Radio de distancia: 2 kilometros");
                    break;
                case 4:
                    radiomts = 2500;
                    seekindicator.setText("Radio de distancia: 2.5 kilometros");
                    break;
                case 5:
                    radiomts = 5000;
                    seekindicator.setText("Radio de distancia: 5 kilometros");
                    break;
            }
            ref.addListenerForSingleValueEvent(BuscarFragment.this);
        }
    }

    public void addPin(Institucion institucion){
        LatLng coor = new LatLng(institucion.getLatitud(),institucion.getLongitud());
        googlemap.addMarker(new MarkerOptions().position(coor).title(institucion.getNombre()));
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
