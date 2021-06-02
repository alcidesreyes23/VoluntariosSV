package com.da39a.voluntariossv.ui.publicar_aviso;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.da39a.voluntariossv.DatePickerFragment;
import com.da39a.voluntariossv.R;
import com.da39a.voluntariossv.firebase.Realtimedb;
import com.da39a.voluntariossv.modelos.Institucion;
import com.da39a.voluntariossv.utils.CustomAlerts;
import com.da39a.voluntariossv.utils.Localbase;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;

import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Map;

public class PublicarAvisoFragment extends Fragment implements View.OnClickListener{

    DatabaseReference ref;
    EditText etTitulo,etExpiracion,etDescripcion,etVacantes;
    TextView etSeekMin,etSeekMax;
    SeekBar seekEdadMin,seekEdadMax;
    Button btnPublicar;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_publicar_aviso, container, false);
        init(root); /* AND */ load();
        return root;
    }

    public void init(View v){
        ref = new Realtimedb().getAvisos();

        etVacantes = v.findViewById(R.id.et_vacantes);
        etTitulo = v.findViewById(R.id.et_titulo);
        etExpiracion = v.findViewById(R.id.et_fechaexp);
        etDescripcion = v.findViewById(R.id.et_descripcion);
        etSeekMin = v.findViewById(R.id.et_seek_min);
        etSeekMax = v.findViewById(R.id.et_seek_max);
        seekEdadMax = v.findViewById(R.id.seek_edadmax);
        seekEdadMin = v.findViewById(R.id.seek_edadmin);
        btnPublicar = v.findViewById(R.id.btn_publicar);
    }

    public void load(){
        btnPublicar.setOnClickListener(this);
        etExpiracion.setOnClickListener(this);

        seekEdadMin.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                etSeekMin.setText("Minima: " + seekBar.getProgress());
            }
        });

        seekEdadMax.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                etSeekMax.setText("Maxima: " + seekBar.getProgress());
            }
        });
    }

    public boolean validarInputs(){

        CustomAlerts ca = new CustomAlerts(this.getContext());
        ca.setType(CustomAlerts.MODALTYPE.DANGER);
        ca.setTitle("Datos incorrectos!");

        if(etTitulo.getText() == null || etTitulo.getText().toString().isEmpty()){
            ca.setMensage("El titulo de el aviso esta vacio");
            ca.show();
            return false;
        }

        if(etVacantes.getText() == null || etVacantes.getText().toString().isEmpty()){
            ca.setMensage("El numero de vacantes esta vacio");
            ca.show();
            return false;
        }else if(Integer.parseInt(etVacantes.getText().toString()) <= 0){
            ca.setMensage("El numero de vacantes no puedes menor o igual a 0");
            ca.show();
            return false;
        }

        if(seekEdadMax.getProgress() < seekEdadMin.getProgress()){
            ca.setMensage("La edad maxima no puede ser menor a la edad minima");
            ca.show();
            return false;
        }

        if(etExpiracion.getText() == null || etExpiracion.getText().toString().isEmpty()){
            ca.setMensage("La fecha de expiración esta vacia");
            ca.show();
            return false;
        }

        if(etDescripcion.getText() == null || etDescripcion.getText().toString().isEmpty()){
            ca.setMensage("La descripción del aviso esta vacio");
            ca.show();
            return false;
        }

        return true;
    }

    public void showAlertMsg(boolean success){
        CustomAlerts ca = new CustomAlerts(this.getContext());
        if(success){
            ca.setTitle("Publicación exitosa!");
            ca.setMensage("Tu aviso de solicitud de voluntario ya fue publicado");
            ca.setType(CustomAlerts.MODALTYPE.SUCCESS);
        }else{
            ca.setTitle("Ocurrio un error!");
            ca.setMensage("No se pudo publicar tu aviso por un error inesperado");
            ca.setType(CustomAlerts.MODALTYPE.DANGER);
        }
        ca.show();
    }

    public void showDatePickerDialog() {
        DatePickerFragment newFragment = DatePickerFragment.newInstance((datePicker, year, month, day) -> {
            final String selectedDate = day + "/" + (month+1) + "/" + year;
            etExpiracion.setTag(new GregorianCalendar(year,month,day).getTimeInMillis() + "");
            etExpiracion.setText(selectedDate);
        });
        newFragment.show(requireActivity().getSupportFragmentManager(), "datePicker");
    }

    @Override
    public void onClick(View v) {

        if(v instanceof Button){
            publicar();
        }else if (v instanceof EditText){
            showDatePickerDialog();
        }

    }

    public void publicar(){
        if(validarInputs()){
            Map<String,Object> institucion = new HashMap<>();
            Institucion miInstitucion = new Localbase(this.getContext()).getInstitucion();
            institucion.put(miInstitucion.getId(),miInstitucion.toMap());

            Map<String,Object> data = new HashMap<>();
            data.put("titulo",etTitulo.getText().toString());
            data.put("descripcion",etDescripcion.getText().toString());
            data.put("fecha", new GregorianCalendar().getTimeInMillis());
            data.put("institucion",institucion);
            data.put("vacantes",Integer.parseInt(etVacantes.getText().toString()));
            data.put("expiracion",Long.parseLong(etExpiracion.getTag().toString()));
            data.put("edadmin",seekEdadMin.getProgress());
            data.put("edadmax",seekEdadMax.getProgress());

            ref.push().setValue(data).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    showAlertMsg(true);
                    limpiar();
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    showAlertMsg(false);
                }
            });
        }
    }

    public void limpiar(){
        etExpiracion.setText("");
        etExpiracion.setTag("");
        etVacantes.setText("");
        etTitulo.setText("");
        etDescripcion.setText("");
        seekEdadMin.setProgress(15);
        seekEdadMax.setProgress(15);
        etSeekMax.setText("Maxima: ");
        etSeekMin.setText("Minima: ");
    }

}