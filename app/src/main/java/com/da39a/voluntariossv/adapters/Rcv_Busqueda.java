package com.da39a.voluntariossv.adapters;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
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
import com.da39a.voluntariossv.utils.Conversiones;
import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class Rcv_Busqueda extends RecyclerView.Adapter<Rcv_Busqueda.VHolder>{

    Context ctx;
    List<Aviso> data;

    public Rcv_Busqueda(Context ctx, List<Aviso> avisos) {
        this.ctx = ctx;
        this.data = avisos;
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
            tvDescripcion.setText(avi.getTitulo());

            if(avi.getExtra() == null || avi.getExtra().isEmpty()){
                tvVacantes.setText(avi.getVacantes() + " vacantes");
            }else{
                tvVacantes.setText(avi.getExtra());
            }


            tvFecha.setText(Conversiones.milisToDateString(avi.getFecha()));
            card.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Bundle bundle = new Bundle();
                    bundle.putString("avisoId",avi.getId());
                    bundle.putString("institucionId",avi.getInstitucionFK());
                    Intent intent = new Intent(ctx, com.da39a.voluntariossv.Aviso.class);
                    intent.putExtra("parametros",bundle);
                    ctx.startActivity(intent);
                }
            });
        }

    }



}
