package com.da39a.voluntariossv;

import android.os.Bundle;

import com.da39a.voluntariossv.firebase.Realtimedb;
import com.da39a.voluntariossv.modelos.Institucion;
import com.da39a.voluntariossv.utils.CustomAlerts;
import com.da39a.voluntariossv.utils.Localbase;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.database.DatabaseReference;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.View;
import android.widget.EditText;
import android.widget.Spinner;

import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Map;

public class Citar extends AppCompatActivity implements View.OnClickListener {

    String uidVoluntario,avisoId;
    DatabaseReference ref;
    FloatingActionButton fab;
    Spinner spin_hora,spin_minutos,spin_jornada;
    EditText et_encargado,et_mensaje,et_fecha,et_oficina;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_citar);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        init();
        load();
    }

    public void init(){
        fab = findViewById(R.id.fab);
        spin_hora = findViewById(R.id.cita_hora);
        et_fecha = findViewById(R.id.cita_fecha);
        et_oficina = findViewById(R.id.cita_oficina);
        et_mensaje = findViewById(R.id.cita_mensaje);
        spin_jornada = findViewById(R.id.cita_jornada);
        spin_minutos = findViewById(R.id.cita_minutos);
        et_encargado = findViewById(R.id.cita_encargado);

        avisoId = getIntent().getExtras().getString("avisoId");
        uidVoluntario = getIntent().getExtras().getString("uid");

        ref = new Realtimedb().getCitasUsuario(uidVoluntario);
    }

    public void load(){
        fab.setOnClickListener(this);
    }

    public void showDatePicker(View v){
        DatePickerFragment newFragment = DatePickerFragment.newInstance((datePicker, year, month, day) -> {
            et_fecha.setText(String.format("%d/%d/%d",day,month,year));
        });
        newFragment.show(getSupportFragmentManager(), "datePicker");
    }

    @Override
    public void onClick(View v) {
        String msg = validar();
        if(msg.equalsIgnoreCase("ok")){

            Institucion miInstitucion = new Localbase(this).getInstitucion();
            Map<String,Object> institucion = new HashMap<>();
            institucion.put(miInstitucion.getId(),miInstitucion.toMap());

            Map<String,Object> data = new HashMap<>();
            data.put("fecha",et_fecha.getText().toString());
            data.put("hora",spin_hora.getSelectedItem().toString() + ":" + spin_minutos.getSelectedItem().toString() + " " + spin_jornada.getSelectedItem().toString());
            data.put("mensaje",et_mensaje.getText().toString());
            data.put("encargado",et_encargado.getText().toString());
            data.put("oficina",et_oficina.getText().toString());
            data.put("institucion",institucion);
            data.put("avisoId",avisoId);

            ref.push().setValue(data).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    limpiar();
                    CustomAlerts ca = new CustomAlerts(Citar.this);
                    ca.setType(CustomAlerts.MODALTYPE.SUCCESS);
                    ca.setTitle("Cita Programada!");
                    ca.setMensage("Se envio una notificación de la cita al voluntario");
                    ca.show();
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    CustomAlerts ca = new CustomAlerts(Citar.this);
                    ca.setType(CustomAlerts.MODALTYPE.WARNING);
                    ca.setTitle("Error!");
                    ca.setMensage("No se pudo programar la cita, intente de nuevo");
                    ca.show();
                }
            });
        }
    }

    public String validar(){

        if(et_encargado.getText().toString().isEmpty()){
            return "El campo de nombre del encargado esta vacio";
        }

        if(et_fecha.getText().toString().isEmpty()){
            return "El campo de fecha esta vacio";
        }

        if(et_oficina.getText().toString().isEmpty()){
            return "El campo de oficina o salón de encunetro esta vacio";
        }

        return "ok";
    }

    public void limpiar(){
        et_mensaje.setText("");
        et_fecha.setText("");
        et_encargado.setText("");
        et_oficina.setText("");
    }

}