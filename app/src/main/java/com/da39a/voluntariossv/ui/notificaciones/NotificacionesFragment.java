package com.da39a.voluntariossv.ui.notificaciones;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.da39a.voluntariossv.R;
import com.da39a.voluntariossv.adapters.Rcv_Citas;
import com.da39a.voluntariossv.firebase.Realtimedb;
import com.da39a.voluntariossv.modelos.Cita;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class NotificacionesFragment extends Fragment implements ValueEventListener {

    View root;
    RecyclerView rcv;
    List<Cita> citas;
    Rcv_Citas adapter;
    DatabaseReference ref;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.fragment_notificationes, container, false);

        init();
        load();

        return root;
    }

    public void init(){
        rcv = root.findViewById(R.id.rcv);

        citas = new ArrayList<>();
        adapter = new Rcv_Citas(citas,this.getContext());

        rcv.setHasFixedSize(true);
        rcv.setLayoutManager(new LinearLayoutManager(this.getContext()));
        rcv.setAdapter(adapter);

        ref = new Realtimedb().getCitasUsuario(FirebaseAuth.getInstance().getUid());
    }

    public void load(){
        ref.addListenerForSingleValueEvent(this);
    }

    @Override
    public void onDataChange(@NonNull DataSnapshot snapshot) {
        citas.clear();
        for (DataSnapshot d : snapshot.getChildren()){
            Cita cita = new Cita(d);
            Log.e("CITA",cita.getInstitucion().getNombre());
            citas.add(cita);
        }
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onCancelled(@NonNull DatabaseError error) {

    }
}
