package com.da39a.voluntariossv;

import android.os.Bundle;

import com.da39a.voluntariossv.firebase.Realtimedb;
import com.da39a.voluntariossv.firelisteners.Checklogin;
import com.da39a.voluntariossv.modelos.Usuario;
import com.da39a.voluntariossv.utils.CustomAlerts;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class Login extends AppCompatActivity implements OnFailureListener, OnSuccessListener<AuthResult>{

    FloatingActionButton fab;
    EditText etCorreo,etContra;
    CustomAlerts alerts;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        init();
    }

    public void init(){
        fab = findViewById(R.id.fab);
        etCorreo = findViewById(R.id.correo);
        etContra = findViewById(R.id.contra);

        alerts = new CustomAlerts(this);
    }

    public void CheckLogin(View v){
        String correo = etCorreo.getText().toString();
        String contra = etContra.getText().toString();

        if(correo == null || correo.isEmpty()){
            alerts.setType(CustomAlerts.MODALTYPE.WARNING);
            alerts.setTitle("Campo vacio");
            alerts.setMensage("Debes llenar el campo de correo electronico");
            alerts.show();
            return;
        };
        if(contra == null || contra.isEmpty()) {
            alerts.setType(CustomAlerts.MODALTYPE.WARNING);
            alerts.setTitle("Campo vacio");
            alerts.setMensage("Debes llenar el campo de contraseña");
            alerts.show();
            return;
        };

        FirebaseAuth.getInstance()
                .signInWithEmailAndPassword(correo,contra)
                .addOnFailureListener(this)
                .addOnSuccessListener(this);

    }

    @Override
    public void onFailure(@NonNull Exception e) {

        if(e instanceof FirebaseAuthInvalidUserException){
            alerts.setType(CustomAlerts.MODALTYPE.WARNING);
            alerts.setTitle("Usuario desconocido");
            alerts.setMensage("No se encontro ningun usuario con este correo electronico");
            alerts.show();
        }else if(e instanceof FirebaseAuthInvalidCredentialsException){
            alerts.setType(CustomAlerts.MODALTYPE.WARNING);
            alerts.setTitle("Datos incorrectos");
            alerts.setMensage("La contraseña ingresada no coicide con el correo");
            alerts.show();
        }
    }

    @Override
    public void onSuccess(AuthResult authResult) {
        String uid = authResult.getUser().getUid();
        new Realtimedb().getUsuario(uid).addListenerForSingleValueEvent(new Checklogin(this));
    }




}