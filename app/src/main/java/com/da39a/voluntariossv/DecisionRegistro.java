package com.da39a.voluntariossv;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class DecisionRegistro extends AppCompatActivity implements View.OnClickListener{

    Button btnI, btnV;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_decision_registro);

        init();
    }

    public void  init(){
        btnI = findViewById(R.id.btnRegistrar);
        btnV = findViewById(R.id.btnVoluntario);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btnRegistrar:
                Intent ri = new Intent(DecisionRegistro.this,RegistroInstitucion.class);
                startActivity(ri);
                break;
            case R.id.btnVoluntario:
                Intent rv = new Intent(DecisionRegistro.this,RegistroVoluntario.class);
                startActivity(rv);
                break;
        }
    }
}