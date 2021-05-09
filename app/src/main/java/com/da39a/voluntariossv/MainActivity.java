package com.da39a.voluntariossv;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.Handler;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

import com.da39a.voluntariossv.firebase.Realtimedb;
import com.da39a.voluntariossv.firelisteners.Checklogin;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {

    TextView txvTitulo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {


        //Configuraciones para la Activity
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Animacion de Titulo
        Animation mov = AnimationUtils.loadAnimation(this,R.anim.movimiento);

        txvTitulo =  findViewById(R.id.txvTitulo);
        txvTitulo.setAnimation(mov);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                CheckAuth();
            }
        },4000);

    }

    public void CheckAuth(){
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if(user == null){
            startActivity(new Intent(this,Login.class));
        }else{
            new Realtimedb().getUsuario(user.getUid()).addListenerForSingleValueEvent(new Checklogin(this));
        }
    }



}