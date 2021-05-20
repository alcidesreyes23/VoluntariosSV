package com.da39a.voluntariossv.ui.configuraciones;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.da39a.voluntariossv.Login;
import com.da39a.voluntariossv.R;
import com.da39a.voluntariossv.utils.Calculos;
import com.da39a.voluntariossv.utils.CustomAlerts;
import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.auth.FirebaseAuth;

public class ConfiguracionesFragment extends Fragment {

    Context ctx;
    Button btn_signout;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_configuraciones, container, false);
        ctx = this.getContext();

        btn_signout = root.findViewById(R.id.btn_signout);
        btn_signout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*FirebaseAuth.getInstance().signOut();
                ConfiguracionesFragment.this.startActivity(new Intent(ctx, Login.class));*/

                //13.671402, -89.292708
                LatLng puntoA = new LatLng(13.670081,-89.293372);
                LatLng puntoB = new LatLng(13.671402,-89.292708);

                Log.e("DISTANCIA","MTS: " + Calculos.getDistancia(puntoA,puntoB));


            }
        });

        return root;
    }


}
