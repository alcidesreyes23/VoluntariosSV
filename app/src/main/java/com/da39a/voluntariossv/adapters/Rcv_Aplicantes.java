package com.da39a.voluntariossv.adapters;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.da39a.voluntariossv.Aviso;
import com.da39a.voluntariossv.Citar;
import com.da39a.voluntariossv.R;
import com.da39a.voluntariossv.firebase.Realtimedb;
import com.da39a.voluntariossv.modelos.Voluntario;
import com.da39a.voluntariossv.ui.configuraciones.ConfiguracionesFragment;
import com.da39a.voluntariossv.utils.Calculos;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.List;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

public class Rcv_Aplicantes extends RecyclerView.Adapter<Rcv_Aplicantes.VHolder>{

    Context ctx;
    String avisoId;
    List<Voluntario> aplicantes;

    public Rcv_Aplicantes(Context ctx, List<Voluntario> aplicantes, String avid) {
        this.ctx = ctx;
        this.avisoId = avid;
        this.aplicantes = aplicantes;
    }

    @NonNull
    @Override
    public VHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(ctx).inflate(R.layout.item_aplicante,parent,false);
        return new VHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull VHolder holder, int position) {
        holder.setUI(aplicantes.get(position));
    }

    @Override
    public int getItemCount() {
        return aplicantes.size();
    }

    public class VHolder extends RecyclerView.ViewHolder{
        Button btnLlamar,btnAceptar;
        CircleImageView civ;
        StorageReference stg;
        TextView tvNombre,tvOcupacion,tvEdadSexo;

        public VHolder(@NonNull View itemView) {
            super(itemView);
            civ = itemView.findViewById(R.id.civ_voluntario);
            tvNombre = itemView.findViewById(R.id.vol_nombre);
            btnLlamar = itemView.findViewById(R.id.btn_llamar);
            btnAceptar = itemView.findViewById(R.id.btn_aceptar);
            tvEdadSexo = itemView.findViewById(R.id.vol_edadsexo);
            tvOcupacion = itemView.findViewById(R.id.vol_ocupacion);

            stg = FirebaseStorage.getInstance().getReference().child("Fotos_perfil");
        }

        public void setUI(Voluntario v){
            tvNombre.setText(v.getNombre());
            tvOcupacion.setText(v.getOcupacion());
            tvEdadSexo.setText(v.getSexo() + " - " + Calculos.getEdadByMilisdate(v.getNacimiento()) + " años");
            btnLlamar.setOnClickListener(new PhoneIntent(v.getTelefono()));
            btnAceptar.setOnClickListener(new Notificar(v.getId()));

            stg = stg.child(v.getFotoPerfil());
            stg.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                @Override
                public void onSuccess(Uri uri) {
                    Glide.with(ctx).load(uri).error(R.drawable.perfil_defecto).into(civ);
                }
            });

        }

        private class PhoneIntent implements View.OnClickListener{
            String phone;
            public PhoneIntent(String phone) {
                this.phone = phone;
            }
            @Override
            public void onClick(View v) {
                ctx.startActivity(new Intent(Intent.ACTION_DIAL, Uri.fromParts("tel",phone,null)));
            }
        }

        private class Notificar implements View.OnClickListener{
            String uid;

            public Notificar(String uid) {
                this.uid = uid;
            }

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ctx, Citar.class);
                intent.putExtra("uid",uid);
                intent.putExtra("avisoId",avisoId);
                ctx.startActivity(intent);
            }
        }

    }

}
