package com.da39a.voluntariossv.adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.da39a.voluntariossv.R;
import com.da39a.voluntariossv.firebase.Realtimedb;
import com.da39a.voluntariossv.modelos.Aviso;
import com.da39a.voluntariossv.modelos.Institucion;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class Rcv_Busqueda extends RecyclerView.Adapter<Rcv_Busqueda.VHolder> implements ValueEventListener {

    Context ctx;
    List<Aviso> data,temp;
    DatabaseReference ref;

    public Rcv_Busqueda(Context ctx) {
        this.ctx = ctx;
        this.data = new ArrayList<>();
        this.temp = new ArrayList<>();
        this.ref = new Realtimedb().getAvisos();
        this.ref.addListenerForSingleValueEvent(this);
    }

    @NonNull
    @Override
    public VHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(ctx).inflate(R.layout.item_aviso,parent,false);
        return new VHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull VHolder holder, int position) {
        holder.setUI(data.get(position));
    }

    @Override
    public int getItemCount() {
        return data.size();
    }



    @Override
    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

        if(dataSnapshot.getKey().equals("avisos")){
            temp.clear();
            for (DataSnapshot d: dataSnapshot.getChildren()) {
                temp.add(new Aviso(d));
            }
            new Realtimedb().getInstituciones().addListenerForSingleValueEvent(this);
        }else{
            for (DataSnapshot d: dataSnapshot.getChildren()) {
                for(Aviso av : temp){
                    if(d.getKey().equals(av.getInstitucionFK())){
                        av.setInstitucion(new Institucion(d));
                    }
                }
            }
            data.addAll(temp);
            this.notifyDataSetChanged();
        }
    }

    @Override
    public void onCancelled(@NonNull DatabaseError databaseError) {

    }


    public class VHolder extends RecyclerView.ViewHolder{
        CardView card;
        TextView tvInstitucion,tvRubro,tvFecha,tvVacantes,tvDescripcion;

        public VHolder(@NonNull View itemView) {
            super(itemView);
            card = itemView.findViewById(R.id.card);
            tvInstitucion = itemView.findViewById(R.id.institucion);
            tvRubro = itemView.findViewById(R.id.rubro);
            tvFecha = itemView.findViewById(R.id.fecha);
            tvVacantes = itemView.findViewById(R.id.vacantes);
            tvDescripcion = itemView.findViewById(R.id.descripcion);
        }

        public void setUI(Aviso avi){
            tvInstitucion.setText(avi.getInstitucion().getNombre());
            tvRubro.setText(avi.getInstitucion().getRubro());
            tvDescripcion.setText(avi.getDescripcion());
            tvVacantes.setText(avi.getVacantes() + " vacantes");
            tvFecha.setText(avi.getFecha()+"");

        }

    }



}
