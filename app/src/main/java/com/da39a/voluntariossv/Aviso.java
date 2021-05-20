package com.da39a.voluntariossv;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;

import com.da39a.voluntariossv.firebase.Realtimedb;
import com.da39a.voluntariossv.modelos.Institucion;
import com.da39a.voluntariossv.utils.Conversiones;
import com.da39a.voluntariossv.utils.CustomAlerts;
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

import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

public class Aviso extends AppCompatActivity implements ValueEventListener, View.OnClickListener {

    Bundle bundle;
    String avisoId;
    CustomAlerts alerts;
    FloatingActionButton fab;
    DatabaseReference refAviso,refInstitucion, refFavoritos;
    CollapsingToolbarLayout collapsingToolbar;
    TextView tv_descripcion, tv_titulo, tv_fecha, tv_telefono, tv_vacantes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_aviso);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        collapsingToolbar = (CollapsingToolbarLayout) findViewById(R.id.toolbar_layout);

        init(); /* AND */ load();
    }

    public void init(){
        fab = findViewById(R.id.fab);
        tv_fecha = findViewById(R.id.aviso_fecha);
        tv_titulo = findViewById(R.id.aviso_titulo);
        tv_vacantes = findViewById(R.id.aviso_vacantes);
        tv_telefono = findViewById(R.id.aviso_telefono);
        tv_descripcion = findViewById(R.id.aviso_descripcion);

        bundle = getIntent().getExtras().getBundle("parametros");
        avisoId = bundle.getString("avisoId");

        alerts = new CustomAlerts(this);
        refAviso = new Realtimedb().getAviso(avisoId);
        refInstitucion = new Realtimedb().getInstitucion(bundle.getString("institucionId"));
        refFavoritos = new Realtimedb().getFavoritosUsuario(FirebaseAuth.getInstance().getUid());
    }

    public void load(){
        fab.setOnClickListener(this);
        refAviso.addListenerForSingleValueEvent(this);
        refInstitucion.addListenerForSingleValueEvent(this);
        refFavoritos.addListenerForSingleValueEvent(this);
    }

    @Override
    public void onClick(View v) {
        refFavoritos.addListenerForSingleValueEvent(new SaveFavorite());
    }

    private void fabChangeState(boolean state){
        String msg = "";
        if(state){
            fab.setTag("true");
            fab.setColorFilter(Color.WHITE);
            msg = "Guardado en favoritos";
        }else{
            fab.setTag("false");
            fab.setColorFilter(Color.parseColor("#c2185c"));
            msg = "Eliminado de favoritos";
        }
        Toast.makeText(this,msg,Toast.LENGTH_LONG).show();
    }

    @Override
    public void onDataChange(@NonNull DataSnapshot snapshot) {
        String parent = snapshot.getRef().getParent().getKey();
        if(parent.equalsIgnoreCase("avisos")){
            com.da39a.voluntariossv.modelos.Aviso aviso = new com.da39a.voluntariossv.modelos.Aviso(snapshot);
            tv_titulo.setText(aviso.getTitulo());
            tv_descripcion.setText(aviso.getDescripcion());
            tv_vacantes.setText("Vacantes disponibles: " + aviso.getVacantes());
            tv_fecha.setText("Publicado:" + Conversiones.milisToDateString(aviso.getFecha()));
        }else if(parent.equalsIgnoreCase("instituciones")){
            Institucion institucion = new Institucion(snapshot);
            collapsingToolbar.setTitle(institucion.getNombre());
            tv_telefono.setOnClickListener(new PhoneIntent(institucion.getTelefono()));
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

    private class SaveFavorite implements ValueEventListener{
        @Override
        public void onDataChange(@NonNull DataSnapshot snapshot) {
            if(snapshot.child(avisoId).exists()){
                refFavoritos.child(avisoId).setValue(null);
                fabChangeState(false);
            }else{
                refFavoritos.child(avisoId).setValue(true);
                fabChangeState(true);
            }
        }
        @Override
        public void onCancelled(@NonNull DatabaseError error) {

        }
    }

}