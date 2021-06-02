package com.da39a.voluntariossv.adapters;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.da39a.voluntariossv.Aviso;
import com.da39a.voluntariossv.R;
import com.da39a.voluntariossv.modelos.Cita;
import com.google.android.gms.maps.model.LatLng;

import java.util.List;
import java.util.Locale;

public class Rcv_Citas extends RecyclerView.Adapter<Rcv_Citas.VHolder>{

    List<Cita> citas;
    Context ctx;

    public Rcv_Citas(List<Cita> citas, Context ctx) {
        this.citas = citas;
        this.ctx = ctx;
    }

    @NonNull
    @Override
    public VHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(ctx).inflate(R.layout.item_notificacion_cita,parent,false);
        return new VHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull VHolder holder, int position) {
        holder.setUI(citas.get(position));
    }

    @Override
    public int getItemCount() {
        return citas.size();
    }

    public class VHolder extends RecyclerView.ViewHolder{
        TextView tvInstitucion,tvFechahora,tvOficina,tvHora,tvMsg;
        TextView btnAviso,btnUbicacion;

        public VHolder(@NonNull View itemView) {
            super(itemView);
            tvInstitucion = itemView.findViewById(R.id.noty_institucion);
            tvFechahora = itemView.findViewById(R.id.noty_fecha);
            tvOficina = itemView.findViewById(R.id.noty_oficina);
            tvHora = itemView.findViewById(R.id.noty_hora);
            tvMsg = itemView.findViewById(R.id.noty_msg);
            btnUbicacion = itemView.findViewById(R.id.noty_ubicacion);
            btnAviso = itemView.findViewById(R.id.noty_aviso);
        }

        public void setUI(Cita cita){
            tvInstitucion.setText(cita.getInstitucion().getNombre());
            tvFechahora.setText(cita.getFecha());
            tvHora.setText(cita.getHora());
            tvMsg.setText(cita.getMensaje());
            tvOficina.setText("Salón u Oficina: " + cita.getOficina());
            btnUbicacion.setOnClickListener(new GoogleMapIntent(new LatLng(cita.getInstitucion().getLatitud(),cita.getInstitucion().getLongitud())));
            btnAviso.setOnClickListener(new AvisoActivity(cita.getAvisoId()));
        }

        private class GoogleMapIntent implements View.OnClickListener{
            LatLng location;

            public GoogleMapIntent(LatLng location) {
                this.location = location;
            }

            @Override
            public void onClick(View v) {
                String uri = String.format(Locale.ENGLISH, "https://www.google.com/maps/search/?api=1&query=%f,%f",location.latitude, location.longitude);
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
                intent.setPackage("com.google.android.apps.maps");
                ctx.startActivity(intent);
            }
        }

        private class AvisoActivity implements View.OnClickListener{
            String id;

            public AvisoActivity(String id) {
                this.id = id;
            }

            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                bundle.putString("avisoId",id);
                Intent intent = new Intent(ctx, com.da39a.voluntariossv.Aviso.class);
                intent.putExtra("parametros",bundle);
                ctx.startActivity(intent);
            }
        }

    }
}
