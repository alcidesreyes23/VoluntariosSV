package com.da39a.voluntariossv.ui.mis_avisos;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.da39a.voluntariossv.R;
import com.da39a.voluntariossv.adapters.Rcv_Busqueda;
import com.da39a.voluntariossv.firebase.Realtimedb;
import com.da39a.voluntariossv.modelos.Aviso;
import com.da39a.voluntariossv.modelos.Institucion;
import com.da39a.voluntariossv.utils.Localbase;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class MisAvisosFragment extends Fragment implements ValueEventListener {

    Institucion miInstitucion;
    List<Aviso> avisos,temp;
    DatabaseReference ref;
    Rcv_Busqueda adapter;
    TextView tvHeader;
    RecyclerView rcv;
    View root;


    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.fragment_favoritos, container, false);

        init(); /* AND */ load();
        return root;
    }

    public void init(){
        //VARS
        temp = new ArrayList<>();
        avisos = new ArrayList<>();
        ref = new Realtimedb().getAvisos();
        adapter = new Rcv_Busqueda(this.getContext(),avisos);
        miInstitucion = new Localbase(this.getContext()).getInstitucion();

        //UI
        rcv = root.findViewById(R.id.rcv);
        tvHeader = root.findViewById(R.id.rcvTextHeader);
    }

    public void load(){
        tvHeader.setText("Avisos Publicados");

        //RECYCLERVIEW
        rcv.setHasFixedSize(true);
        rcv.setLayoutManager(new LinearLayoutManager(this.getContext()));
        rcv.setAdapter(adapter);

        //FIREBASE
        ref.addListenerForSingleValueEvent(this);
    }

    @Override
    public void onResume() {
        super.onResume();
        ref.addListenerForSingleValueEvent(this);
    }

    @Override
    public void onDataChange(@NonNull DataSnapshot snapshot) {
        avisos.clear();
        for (DataSnapshot d : snapshot.getChildren()){
            Aviso aviso = new Aviso(d);
            if(aviso.getInstitucion().getId().equals(miInstitucion.getId())){
                aviso.setExtra(aviso.getAplicantes().size() + " voluntarios");
                avisos.add(aviso);
            }
        }
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onCancelled(@NonNull DatabaseError error) {

    }
}