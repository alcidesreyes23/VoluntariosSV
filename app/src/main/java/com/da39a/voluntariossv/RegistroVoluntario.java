package com.da39a.voluntariossv;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.da39a.voluntariossv.firebase.Realtimedb;
import com.da39a.voluntariossv.utils.Constantes;
import com.da39a.voluntariossv.utils.CustomAlerts;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;

import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Map;

public class RegistroVoluntario extends AppCompatActivity implements  OnFailureListener {

    EditText edtCorreo, edtContra, edtConfContra, edtFecha, edtNombre,edtOcupacion,edtTelefono;
    Button btnRegistrar, btnCancel;
    RadioGroup sexo;
    CustomAlerts alerts;
    long fechaLong;
    String genero;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registro_voluntario);

        init();
    }

    public  void  init(){
        //EditText
        edtCorreo = findViewById(R.id.correo);
        edtContra = findViewById(R.id.contra);
        edtConfContra = findViewById(R.id.confirmarContra);
        edtFecha = findViewById(R.id.direccion);
        edtNombre = findViewById(R.id.nombre);
        edtOcupacion = findViewById(R.id.ocupacion);
        edtTelefono = findViewById(R.id.telefono);
        //Buttons
        btnCancel = findViewById(R.id.btnCancelar);
        btnRegistrar = findViewById(R.id.btnRegistrar);
        //RadioButton
        sexo = findViewById(R.id.rdgSexo);

        alerts =  new CustomAlerts(this);
    }

    public void onRadioButtonClicked(View view) {
        boolean checked = ((RadioButton) view).isChecked();
        int id = view.getId();
        if (id == R.id.radio_m) {
            if (checked) genero = "Masculino";
        } else if (id == R.id.radio_f){
            if (checked) genero = "Femenino";
        }
    }
    public void  Checklogin(View v){
        boolean estado = true;
        String correo = edtCorreo.getText().toString();
        String contra = edtContra.getText().toString();
        String cfContra = edtConfContra.getText().toString();
        String fecha =  edtFecha.getText().toString();
        String nombre = edtNombre.getText().toString();
        String ocupacion = edtOcupacion.getText().toString();
        String telefono = edtTelefono.getText().toString();

        String[] datos = {correo,contra,cfContra, fecha,nombre,ocupacion, telefono};
        for (int i = 0; i < datos.length; i++) {
            if (datos[i] == null || datos[i].isEmpty()) {
                alert(i);
                estado = false;
                break;
            }
            estado = true;
        }

        if (estado){
            if (!contra.equals(cfContra)) {
                alerts.setType(CustomAlerts.MODALTYPE.DANGER);
                alerts.setTitle("Contraseñas distintas");
                alerts.setMensage("Las contraseñas ingresadas no coinciden");
                alerts.show();
            }
            else {
                Map<String,Object> dataVoluntarios = new HashMap<>();
                dataVoluntarios.put("nacimiento",fechaLong);
                dataVoluntarios.put("nombre",nombre);
                dataVoluntarios.put("ocupacion",ocupacion);
                dataVoluntarios.put("sexo",genero);
                dataVoluntarios.put("telefono",telefono);
                //dataVoluntarios.put("fotoPerfil",(genero.equals("Masculino") ? Constantes.FOTO_POR_DEFECTO_M : Constantes.FOTO_POR_DEFECTO_F));

                FirebaseAuth.getInstance().createUserWithEmailAndPassword(correo,contra).addOnSuccessListener(authResult -> {
                    String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
                    new Realtimedb().getUsuario(uid).setValue("voluntario").addOnSuccessListener(aVoid ->
                        new Realtimedb().getVoluntario(uid).setValue(dataVoluntarios).addOnSuccessListener(aVoid1 -> {
                            alerts.setType(CustomAlerts.MODALTYPE.SUCCESS);
                            alerts.setTitle("¡Bienvenido a VoluntariosSV!");
                            alerts.setMensage("Gracias por crear una cuenta con nosotros, esperamos su ayuda para construir un mejor El Salvador");
                            alerts.setUid(uid);
                            alerts.setEstado(true);
                            alerts.show();
                        }).addOnFailureListener(this))
                    .addOnFailureListener(this);
                }).addOnFailureListener(this);
            }
        }
    }

    public void alert(int i){
        String campo;
        if (i == 0) campo = "Correo";
        else if (i == 1) campo = "Contraseña";
        else if (i == 2) campo = "Confirmar contraseña";
        else if (i == 3) campo = "Fecha de nacimiento";
        else if (i == 4) campo = "Nombre completo";
        else campo = "Ocupación";

        alerts.setType(CustomAlerts.MODALTYPE.WARNING);
        alerts.setTitle("Campo vacio");
        alerts.setMensage("Debes llenar el campo de " + campo);
        alerts.show();
    }


    public void limpiar(View v){
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setTitle("Cancelar Registro");
        dialog.setMessage("¿ Desea cancelar el registro ?");
        dialog.setCancelable(false);
        dialog.setPositiveButton("Aceptar",  (dialogo1, id) -> {
            edtCorreo.setText("");
            edtContra.setText("");
            edtConfContra.setText("");
            edtFecha.setText("");
            edtNombre.setText("");
            edtOcupacion.setText("");
            edtTelefono.setText("");
            startActivity(new Intent(RegistroVoluntario.this,DecisionRegistro.class));
        });
        dialog.setNegativeButton("Cancelar",null);
        dialog.show();
    }

    public void showDatePickerDialog(View v) {
        DatePickerFragment newFragment = DatePickerFragment.newInstance((datePicker, year, month, day) -> {
            final String selectedDate = twoDigits(day) + "/" + twoDigits(month+1) + "/" + year;
            fechaLong = new GregorianCalendar(year,month,day).getTimeInMillis();
            edtFecha.setText(selectedDate);
        });
        newFragment.show(getSupportFragmentManager(), "datePicker");
    }

    public String twoDigits(int n) {
        return (n<=9) ? ("0"+n) : String.valueOf(n);
    }

    @Override
    public void onFailure(@NonNull Exception e) {
        if (e instanceof FirebaseAuthException){
            alerts.setType(CustomAlerts.MODALTYPE.DANGER);
            alerts.setTitle("Error al registrar cuenta");
            switch (((FirebaseAuthException) e).getErrorCode()){
                case "ERROR_EMAIL_ALREADY_IN_USE":
                    alerts.setMensage("La dirección de correo electrónico ingresada ya está siendo utilizada por otra cuenta.");
                    break;
                case "ERROR_WEAK_PASSWORD":
                    alerts.setMensage("La contraseña debe contener almenos 6 caracteres.");
                    break;
                case "ERROR_INVALID_EMAIL":
                    alerts.setMensage("La dirección de correo electrónico no es valida.");
                    break;
                default:
                    alerts.setMensage(e.getMessage());
            }
            alerts.show();
        }
    }
}