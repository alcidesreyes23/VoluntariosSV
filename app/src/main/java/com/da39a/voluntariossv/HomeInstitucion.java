package com.da39a.voluntariossv;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.Menu;
import android.widget.TextView;

import com.da39a.voluntariossv.modelos.Institucion;
import com.da39a.voluntariossv.utils.CustomAlerts;
import com.da39a.voluntariossv.utils.Localbase;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;

import androidx.annotation.NonNull;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

public class HomeInstitucion extends AppCompatActivity {

    private AppBarConfiguration mAppBarConfiguration;
    Institucion miInstitucion;
    NavigationView navigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_institucion);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);

        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_mis_avisos, R.id.nav_publicar_aviso, R.id.nav_mi_institucion)
                .setDrawerLayout(drawer)
                .build();

        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);

        initHeader();
    }

    public void initHeader(){
        miInstitucion = new Localbase(this).getInstitucion();
        View header = navigationView.getHeaderView(0);
        TextView tvInsNombre = header.findViewById(R.id.institucion_nombre);
        TextView tvInsRubro = header.findViewById(R.id.institucion_rubro);
        tvInsNombre.setText(miInstitucion.getNombre());
        tvInsRubro.setText(miInstitucion.getRubro());
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.home_institucion, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId() == R.id.action_settings){
            CustomAlerts ca = new CustomAlerts(this);
            ca.setType(CustomAlerts.MODALTYPE.QUESTION);
            ca.setTitle("Desea cerrar la sesión?");
            ca.setMensage("Se cerrara la sesion de usuario");
            ca.setNegative("Cancelar",null);
            ca.setPositive(new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    FirebaseAuth.getInstance().signOut();
                    startActivity(new Intent(HomeInstitucion.this,Login.class));
                }
            });
            ca.show();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }
}