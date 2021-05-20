package com.da39a.voluntariossv.ui.avisos;

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
import com.da39a.voluntariossv.adapters.Rcv_Busqueda;
import com.da39a.voluntariossv.firebase.Realtimedb;
import com.da39a.voluntariossv.modelos.Aviso;
import com.da39a.voluntariossv.modelos.Institucion;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class AvisosFragment extends Fragment {

    RecyclerView rcv;
    DatabaseReference refFav,refAvisos,refInstituciones;
    List<Aviso> temp,avisos;
    List<String> favoritos;
    Rcv_Busqueda adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_avisos, container, false);
        init(root);
        load();
        return root;
    }

    private void init(View v){
        rcv = v.findViewById(R.id.rcv);

        temp = new ArrayList<>();
        avisos = new ArrayList<>();
        favoritos = new ArrayList<>();

        refAvisos = new Realtimedb().getAvisos();
        adapter = new Rcv_Busqueda(this.getContext(),avisos);
        refInstituciones = new Realtimedb().getInstituciones();
        refFav = new Realtimedb().getFavoritosUsuario(FirebaseAuth.getInstance().getUid());
    }

    private void load(){
        rcv.setHasFixedSize(true);
        rcv.setLayoutManager(new LinearLayoutManager(this.getContext()));
        rcv.setAdapter(adapter);

        refFav.addListenerForSingleValueEvent(new LoadFavs());
    }


    private class LoadFavs implements ValueEventListener{
        @Override
        public void onDataChange(@NonNull DataSnapshot snapshot) {
            for(DataSnapshot d : snapshot.getChildren()){
                favoritos.add(d.getKey());
            }
            refAvisos.addListenerForSingleValueEvent(new LoadAvisos());
        }
        @Override
        public void onCancelled(@NonNull DatabaseError error) {

        }
    }

    private class LoadAvisos implements ValueEventListener{
        @Override
        public void onDataChange(@NonNull DataSnapshot snapshot) {
            temp.clear();
            for (DataSnapshot d : snapshot.getChildren()){
                if(favoritos.contains(d.getKey())){
                    temp.add(new Aviso(d));
                }
            }
            refInstituciones.addListenerForSingleValueEvent(new LoadInstituciones());
        }
        @Override
        public void onCancelled(@NonNull DatabaseError error) {

        }
    }

    private class LoadInstituciones implements ValueEventListener{
        @Override
        public void onDataChange(@NonNull DataSnapshot snapshot) {
            avisos.clear();
            for(DataSnapshot d : snapshot.getChildren()){
                for (Aviso av : temp){
                    if(d.getKey().equals(av.getInstitucionFK())){
                        av.setInstitucion(new Institucion(d));
                    }
                }
            }
            avisos.addAll(temp);
            adapter.notifyDataSetChanged();
        }

        @Override
        public void onCancelled(@NonNull DatabaseError error) {

        }
    }



}
