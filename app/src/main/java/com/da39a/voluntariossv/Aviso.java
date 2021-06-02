package com.da39a.voluntariossv;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;

import com.da39a.voluntariossv.adapters.Rcv_Aplicantes;
import com.da39a.voluntariossv.firebase.Realtimedb;
import com.da39a.voluntariossv.modelos.Institucion;
import com.da39a.voluntariossv.modelos.Voluntario;
import com.da39a.voluntariossv.utils.Conversiones;
import com.da39a.voluntariossv.utils.CustomAlerts;
import com.da39a.voluntariossv.utils.Localbase;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;
import com.google.firebase.database.ValueEventListener;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class Aviso extends AppCompatActivity implements ValueEventListener, View.OnClickListener {

    Bundle bundle;
    String avisoId;
    CustomAlerts alerts;
    Button btn_solicitar;
    Rcv_Aplicantes adapter;
    LatLng currentLocation;
    List<String> aplicantes;
    FloatingActionButton fab;
    RecyclerView rcv_aplicantes;
    List<Voluntario> voluntarios;
    FusedLocationProviderClient fused;
    CollapsingToolbarLayout collapsingToolbar;
    DatabaseReference refAviso, refFavoritos, refVoluntarios;
    LinearLayout btn_ubicacion, btn_telefono, aplicantes_content;
    TextView tv_descripcion, tv_titulo, tv_expiracion, tv_telefono, tv_vacantes, tv_institucion_nombre, tv_rubro, tv_rangoedad;
    boolean isInstitucion = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_aviso);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        collapsingToolbar = (CollapsingToolbarLayout) findViewById(R.id.toolbar_layout);
        fused = LocationServices.getFusedLocationProviderClient(this);

        init();
        load();
        mutableView();
    }


    public void init() {
        fab = findViewById(R.id.fab);
        collapsingToolbar.setTitle(".");

        btn_solicitar = findViewById(R.id.btn_aplicar);
        btn_telefono = findViewById(R.id.btn_aviso_telefono);
        btn_ubicacion = findViewById(R.id.btn_aviso_ubicacion);

        tv_rubro = findViewById(R.id.aviso_rubro);
        tv_titulo = findViewById(R.id.aviso_titulo);
        tv_rangoedad = findViewById(R.id.aviso_edad);
        tv_telefono = findViewById(R.id.aviso_telefono);
        tv_expiracion = findViewById(R.id.aviso_expiracion);
        tv_descripcion = findViewById(R.id.aviso_descripcion);
        tv_vacantes = findViewById(R.id.aviso_vacantes_disponibles);
        tv_institucion_nombre = findViewById(R.id.aviso_institucion_nombre);

        aplicantes_content = findViewById(R.id.aplicantes_content);
        rcv_aplicantes = findViewById(R.id.rcv_aplicantes);
        voluntarios = new ArrayList<>();

        bundle = getIntent().getExtras().getBundle("parametros");
        avisoId = bundle.getString("avisoId");

        alerts = new CustomAlerts(this);
        refAviso = new Realtimedb().getAviso(avisoId);
        refVoluntarios = new Realtimedb().getVoluntarios();
        refFavoritos = new Realtimedb().getFavoritosUsuario(FirebaseAuth.getInstance().getUid());
    }

    public void load() {
        refAviso.addListenerForSingleValueEvent(this);
        refFavoritos.addListenerForSingleValueEvent(this);
        btn_solicitar.setOnClickListener(this);
        fab.setOnClickListener(this);
        activeLocation();
    }

    public void mutableView(){
        isInstitucion = new Localbase(this).getUsuario().getTipo().equalsIgnoreCase("institucion");
        if(isInstitucion){
            fab.setImageDrawable(this.getResources().getDrawable(R.drawable.ic_close));
            btn_solicitar.setVisibility(View.GONE);

            //RCV
            adapter = new Rcv_Aplicantes(this,voluntarios);
            rcv_aplicantes.setHasFixedSize(true);
            rcv_aplicantes.setLayoutManager(new LinearLayoutManager(this));
            rcv_aplicantes.setAdapter(adapter);
        }else{
            aplicantes_content.setVisibility(View.GONE);
        }
    }

    public void activeLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        fused.getLastLocation().addOnSuccessListener(new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                currentLocation = new LatLng(location.getLatitude(), location.getLongitude());
            }
        });
    }

    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.btn_aplicar){
            aplicarVacante();
        }else{
            if(isInstitucion){
                CustomAlerts ca = new CustomAlerts(this);
                ca.setTitle("Eliminar Aviso?");
                ca.setMensage("Desea eliminar este aviso permanentemente?");
                ca.setType(CustomAlerts.MODALTYPE.INFO);
                ca.setPositive(new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        refAviso.setValue(null);
                        Aviso.this.finish();
                    }
                });
                ca.show();
            }else{
                refFavoritos.addListenerForSingleValueEvent(new SaveFavorite());
            }
        }
    }

    public void aplicarVacante(){
        Map<String,Object> data = new HashMap<>();
        data.put(FirebaseAuth.getInstance().getCurrentUser().getUid(),true);

        CustomAlerts ca = new CustomAlerts(this);

        refAviso.child("aplicantes").setValue(data).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                ca.setTitle("Solicitud enviada!");
                ca.setMensage("Has solicitado aplicar a esta solicitud de voluntarios exitosamente!");
                ca.setType(CustomAlerts.MODALTYPE.SUCCESS);
                ca.show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                ca.setTitle("Error!");
                ca.setMensage("Ocurrio un problema inesperado");
                ca.setType(CustomAlerts.MODALTYPE.WARNING);
                ca.show();
            }
        });
    }

    private void fabChangeState(boolean state){
        if(state){
            fab.setTag("true");
            fab.setColorFilter(Color.WHITE);
        }else{
            fab.setTag("false");
            fab.setColorFilter(Color.parseColor("#c2185c"));
        }
    }

    private void toastMsg(String msg){
        Toast.makeText(this,msg,Toast.LENGTH_LONG).show();
    }

    @Override
    public void onDataChange(@NonNull DataSnapshot snapshot) {
        String parent = snapshot.getRef().getParent().getKey();
        if(parent.equalsIgnoreCase("avisos")){
            com.da39a.voluntariossv.modelos.Aviso aviso = new com.da39a.voluntariossv.modelos.Aviso(snapshot);
            tv_institucion_nombre.setText(aviso.getInstitucion().getNombre());
            tv_rubro.setText(aviso.getInstitucion().getRubro());
            tv_titulo.setText(aviso.getTitulo());
            tv_descripcion.setText(aviso.getDescripcion());
            tv_vacantes.setText(aviso.getVacantes()+"");
            tv_telefono.setText(aviso.getInstitucion().getTelefono());
            tv_expiracion.setText(Conversiones.milisToLargeDateString(aviso.getExpiracion()));
            tv_rangoedad.setText(aviso.getEdadmin() + " - " + aviso.getEdadmax() + " años");
            btn_telefono.setOnClickListener(new PhoneIntent(aviso.getInstitucion().getTelefono()));
            btn_ubicacion.setOnClickListener(new GoogleMapIntent(new LatLng(aviso.getInstitucion().getLatitud(),aviso.getInstitucion().getLongitud())));

            if(isInstitucion){
                aplicantes = aviso.getAplicantes();
                refVoluntarios.addListenerForSingleValueEvent(new LoadAplicantes());
            }

        }else if(parent.equalsIgnoreCase("favoritos")){
            fabChangeState(snapshot.child(avisoId).exists());
        }
    }

    @Override
    public void onCancelled(@NonNull DatabaseError error) {
        alerts.setType(CustomAlerts.MODALTYPE.DANGER);
        alerts.setTitle("No se pudo obtener el aviso");
        alerts.setMensage("El aviso no existe o no tienes conexion a internet para cargar los datos");
        alerts.show();
    }

    private class PhoneIntent implements View.OnClickListener{
        String phone;
        public PhoneIntent(String phone) {
            this.phone = phone;
        }
        @Override
        public void onClick(View v) {
            Aviso.this.startActivity(new Intent(Intent.ACTION_DIAL, Uri.fromParts("tel",phone,null)));
        }
    }

    private class GoogleMapIntent implements View.OnClickListener{
        LatLng destino;

        public GoogleMapIntent(LatLng destino) {
            this.destino = destino;
        }

        @Override
        public void onClick(View v) {
            if(currentLocation != null){
                String uri = String.format(Locale.ENGLISH, "https://www.google.com/maps/dir/?api=1&origin=%f,%f&destination=%f,%f&travelmode=walking",currentLocation.latitude, currentLocation.longitude,  destino.latitude, destino.longitude);
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
                intent.setPackage("com.google.android.apps.maps");
                startActivity(intent);
            }
        }
    }

    private class SaveFavorite implements ValueEventListener{
        @Override
        public void onDataChange(@NonNull DataSnapshot snapshot) {
            if(snapshot.child(avisoId).exists()){
                refFavoritos.child(avisoId).setValue(null);
                fabChangeState(false);
                toastMsg("Eliminado de favoritos");
            }else{
                refFavoritos.child(avisoId).setValue(true);
                fabChangeState(true);
                toastMsg("Agregado a  favoritos");
            }
        }
        @Override
        public void onCancelled(@NonNull DatabaseError error) {

        }
    }

    private class LoadAplicantes implements ValueEventListener{
        @Override
        public void onDataChange(@NonNull DataSnapshot snapshot) {
            for(DataSnapshot d : snapshot.getChildren()){
                if(aplicantes.contains(d.getKey())){
                    voluntarios.add(new Voluntario(d));
                }
            }
            adapter.notifyDataSetChanged();
        }
        @Override
        public void onCancelled(@NonNull DatabaseError error) {

        }
    }

}