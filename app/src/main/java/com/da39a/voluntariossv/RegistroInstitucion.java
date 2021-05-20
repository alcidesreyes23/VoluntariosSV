package com.da39a.voluntariossv;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;

import com.da39a.voluntariossv.firebase.Realtimedb;
import com.da39a.voluntariossv.firelisteners.Checklogin;
import com.da39a.voluntariossv.utils.CustomAlerts;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.database.DatabaseReference;

import java.util.HashMap;
import java.util.Map;

public class RegistroInstitucion extends AppCompatActivity implements OnFailureListener {

    Spinner iRubro;
    EditText edtCorreo, edtContra, edtConfContra, edtDireccion, edtDescripcion, edtNombre;
    Button btnRegistrar, btnCancel;
    ImageButton btnMap;
    CustomAlerts alerts;
    String latitud, longitud;
    static int PLACE_PICKER_REQUEST=1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registro_institucion);
        init();
    }

    public  void  init(){
        //EditText
        edtCorreo = findViewById(R.id.correo);
        edtContra = findViewById(R.id.contra);
        edtConfContra = findViewById(R.id.confirmarContra);
        edtNombre = findViewById(R.id.nombre);
        edtDescripcion = findViewById(R.id.descripcion);
        edtDireccion = findViewById(R.id.direccion);
        //Buttons
        btnCancel = findViewById(R.id.btnCancelar);
        btnRegistrar = findViewById(R.id.btnRegistrar);
        btnMap  = findViewById(R.id.btnMapa);
        //Spinner
        iRubro = findViewById(R.id.rubro);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,R.array.rubros, android.R.layout.simple_spinner_item);
        iRubro.setAdapter(adapter);
        iRubro.setSelection(0);
        alerts =  new CustomAlerts(this);

    }

    public void  Checklogin(View v){
        boolean estado = true;
        String correo = edtCorreo.getText().toString();
        String contra = edtContra.getText().toString();
        String cfContra = edtConfContra.getText().toString();
        String direc =  edtDireccion.getText().toString();
        String nombre = edtNombre.getText().toString();
        String rubro = iRubro.getSelectedItem().toString();
        String desc = edtDescripcion.getText().toString();

        String[] datos = {correo,contra,cfContra, direc,nombre,rubro,desc};
        for (int i = 0; i < datos.length; i++) {
            if (datos[i] == null || datos[i].isEmpty() || datos[i].equals("Seleccione el rubro")) {
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
                alerts.setMensage("Las contraseñas ingresadas no coinciden.");
                alerts.show();
            }
            else {
                Map<String,Object> dataInstituciones = new HashMap<>();
                dataInstituciones.put("localizacion",null);
                dataInstituciones.put("nombre",nombre);
                dataInstituciones.put("rubro",rubro);
                dataInstituciones.put("descripcion",desc);

                Map<String,Object> dataDireccion = new HashMap<>();
                dataDireccion.put("latitud",latitud);
                dataDireccion.put("longitud",longitud);

                FirebaseAuth.getInstance().createUserWithEmailAndPassword(correo,contra).addOnSuccessListener(authResult -> {
                    String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
                    new Realtimedb().getUsuario(uid).setValue("institucion").addOnSuccessListener(aVoid ->
                             new Realtimedb().getInstitucion(uid).setValue(dataInstituciones).addOnSuccessListener(aVoid1 ->
                                     new Realtimedb().getInstitucion(uid).child("localizacion").updateChildren(dataDireccion).addOnSuccessListener(aVoid2 -> {
                                         alerts.setType(CustomAlerts.MODALTYPE.SUCCESS);
                                         alerts.setTitle("¡Bienvenido a VoluntariosSV!");
                                         alerts.setMensage("Gracias por crear una cuenta con nosotros, esperamos su ayuda para construir un mejor El Salvador");
                                         alerts.setEstado(true);
                                         alerts.setUid(uid);
                                         alerts.show();
                                    }).addOnFailureListener(this)
                             .addOnFailureListener(this))
                    .addOnFailureListener(this));
                }).addOnFailureListener(this);
            }
        }
    }

    //Metodos para crear alertas de validaciones
    public void alert(int i){
        String campo;
        if (i == 0) campo = "Correo";
        else if (i == 1) campo = "Contraseña";
        else if (i == 2) campo = "Confirmar contraseña";
        else if (i == 3) campo = "Dirección";
        else if (i == 4) campo = "Nombre de institucion";
        else if (i == 5) campo = "Rubro";
        else campo = "Descripción";

        alerts.setType(CustomAlerts.MODALTYPE.WARNING);
        alerts.setTitle("Campo vacio");
        alerts.setMensage("El campo de " + campo +  " es requerido.");
        alerts.show();
    }

    //Metodo para limpiar el formulario
    public void limpiar(View v){
        alerts.setType(CustomAlerts.MODALTYPE.WARNING);
        alerts.setTitle("Cancelar Registro");
        alerts.setMensage("¿ Desea cancelar el registro ?");
        alerts.setPositive(new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                /*edtCorreo.setText("");
                edtContra.setText("");
                edtConfContra.setText("");
                edtDireccion.setText("");
                edtNombre.setText("");
                iRubro.setSelection(0);
                edtDescripcion.setText("");*/
                startActivity(new Intent(RegistroInstitucion.this,DecisionRegistro.class));
            }
        });
        alerts.show();
    }

    //Inicio metodos para cargar el mapa y obtener su posicion
    public  void map(View v) {
        PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();
        Intent intent;
        try {
            intent=builder.build(this);
            startActivityForResult(intent,PLACE_PICKER_REQUEST);
        } catch (GooglePlayServicesRepairableException | GooglePlayServicesNotAvailableException e) {
            e.printStackTrace();
        }
    }
    protected void onActivityResult(int requestCode, int resultCode,Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PLACE_PICKER_REQUEST) {
            if (resultCode == RESULT_OK) {
                Place place = PlacePicker.getPlace(this,data);
                String address=String.format("Direccion guardada %s",place.getAddress());
                edtDireccion.setText(address);
                latitud = String.valueOf(place.getLatLng().latitude);
                longitud = String.valueOf(place.getLatLng().longitude);
            }
        }
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