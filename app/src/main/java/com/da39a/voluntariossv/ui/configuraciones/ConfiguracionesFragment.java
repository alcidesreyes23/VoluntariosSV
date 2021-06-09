package com.da39a.voluntariossv.ui.configuraciones;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.da39a.voluntariossv.DatePickerFragment;
import com.da39a.voluntariossv.Login;
import com.da39a.voluntariossv.R;
import com.da39a.voluntariossv.firebase.Realtimedb;
import com.da39a.voluntariossv.modelos.Voluntario;
import com.da39a.voluntariossv.utils.Calculos;
import com.da39a.voluntariossv.utils.Constantes;
import com.da39a.voluntariossv.utils.Conversiones;
import com.da39a.voluntariossv.utils.CustomAlerts;
import com.da39a.voluntariossv.utils.Permisos;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;


public class ConfiguracionesFragment extends Fragment {

    //region DECLARACION DE VARIABLES
    Context ctx;
    Button btn_signout,btnActualizar;
    EditText edtTelefono, edtFecha, edtNombre, edtOcupacion;
    ImageButton btnCalendar;
    RadioButton rM, rF;
    CustomAlerts alerts;
    long fechaLong;
    ImageView perfil;
    static  final int  GALLERY_INTENT = 1;
    Uri uriPerfil;
    boolean estado;
    String genero;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_configuraciones, container, false);
        ctx = (this.getContext());
        init(root);


        return root;
    }


    //region METODO INIT
    public  void  init(View v){
        //EditText
        edtTelefono = v.findViewById(R.id.telefono);
        edtFecha = v.findViewById(R.id.direccion);
        edtNombre = v.findViewById(R.id.nombre);
        edtOcupacion = v.findViewById(R.id.ocupacion);
        //Buttons
        btnActualizar = v.findViewById(R.id.btnRegistrar);
        btnCalendar  = v.findViewById(R.id.btnCal);
        //ImageView
        perfil = v.findViewById(R.id.imvPerfil);
        //RadioGroup
        rM = v.findViewById(R.id.radio_m);
        rF = v.findViewById(R.id.radio_f);
        //Alerts
        alerts =  new CustomAlerts(v.getContext());
        //Eventos Click
        edtFecha.setOnClickListener(view -> showDatePickerDialog());
        btnActualizar.setOnClickListener(view -> Actualizar());
        perfil.setOnClickListener(v1 -> {

            if(CheckPermisos()){
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("image/*");
                startActivityForResult(intent, GALLERY_INTENT);
            }else{
                new Permisos(ConfiguracionesFragment.this.getActivity()).CheckPermisos();
            }

        });


        //Cargar datos de perfil
        LoadUser();
    }
    //endregion

    public boolean CheckPermisos(){
        return ActivityCompat.checkSelfPermission(this.getActivity(),Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;
    }


    //Para obtener la url de la imagen desde la galeria
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode){
            case GALLERY_INTENT:
                if (resultCode == Activity.RESULT_OK && data != null){
                    assert data != null;
                    uriPerfil = data.getData();
                    estado = true;
                    Glide.with(ConfiguracionesFragment.this.getContext()).load(uriPerfil).into(perfil);
                }
            break;
        }
    }


    //region  ACTUALIZAR LOS DATOS DE PERFIL
    //Metodo que acutaliza
    public  void Actualizar () {
        String tel = edtTelefono.getText().toString();
        // String fecha =  edtFecha.getText().toString();
        String nombre = edtNombre.getText().toString();
        String ocupacion = edtOcupacion.getText().toString();

        if (rM.isChecked()) genero = "Masculino";
        else if (rF.isChecked()) genero = "Femenino";

        Map<String,Object> datos = new HashMap<>();
        datos.put("nombre",nombre);
        datos.put("ocupacion",ocupacion);
        datos.put("nacimiento",fechaLong);
        datos.put("sexo",genero);
        datos.put("telefono",tel);

        //Valida que se haya seleccionado una foto
        if (estado) {
            String uid = FirebaseAuth.getInstance().getUid();
            StorageReference stg = FirebaseStorage.getInstance().getReference().child("Fotos_perfil").child(uid + ".jpg");
            stg.putFile(uriPerfil).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    EnviarDatos(datos);
                    estado = false;
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {

                }
            });

        } else {
            EnviarDatos(datos);
        }
    }

    //Funciona para enviar los datos actualizado a la base
    public void EnviarDatos(Map<String,Object> datos){
        String uid = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();
        new Realtimedb().getVoluntario(uid).updateChildren(datos).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                if (e instanceof FirebaseAuthException){
                    alerts.setType(CustomAlerts.MODALTYPE.DANGER);
                    alerts.setTitle("Error al modificar cuenta");
                    alerts.setMensage(e.getMessage());
                    alerts.show();
                }
            }
        }).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                alerts.setType(CustomAlerts.MODALTYPE.SUCCESS);
                alerts.setTitle("Datos Actualizados!");
                alerts.setMensage("Se modificaron los datos de tu usuario correctamente");
                alerts.show();
            }
        });
    }

    //endregion
    //region OBTENER LOS DATOS ACTUALIZADOS
    //Carga los datos del perfil
    public void LoadUser () {
        String uid = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();
        new Realtimedb().getVoluntarios().child(uid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (isAdded()) {
                    if (snapshot.exists()) {
                        Voluntario vol = new Voluntario(snapshot);

                        long datoF = vol.getNacimiento();
                        String date = Conversiones.milisToDateString(datoF);
                        String[] fecha = date.split(" ");
                        fechaLong = datoF;
                        edtNombre.setText(vol.getNombre());
                        edtFecha.setText(fecha[0]);
                        edtOcupacion.setText(vol.getOcupacion());

                        if (vol.getSexo().equals("Masculino"))
                            rM.setChecked(true);
                        else
                            rF.setChecked(true);

                        edtTelefono.setText(vol.getTelefono());
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) { }
        });

        StorageReference stg = FirebaseStorage.getInstance().getReference().child("Fotos_perfil").child(uid+".jpg");
        stg.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Glide.with(ConfiguracionesFragment.this.getContext()).load(uri).error(R.drawable.perfil_defecto).into(perfil);
            }
        });

    }

    // endregion

    //region OBTENER FECHA
    //Seleccionar fecha
    public void showDatePickerDialog() {
        DatePickerFragment newFragment = DatePickerFragment.newInstance((datePicker, year, month, day) -> {
            final String selectedDate = twoDigits(day) + "/" + twoDigits(month+1) + "/" + year;
            fechaLong = new GregorianCalendar(year,month,day).getTimeInMillis();
            edtFecha.setText(selectedDate);
        });
        newFragment.show(requireActivity().getSupportFragmentManager(), "datePicker");
    }

    public String twoDigits(int n) {
        return (n<=9) ? ("0"+n) : String.valueOf(n);
    }
    //endregion
}
