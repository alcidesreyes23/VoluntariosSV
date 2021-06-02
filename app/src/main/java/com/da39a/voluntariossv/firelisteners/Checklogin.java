package com.da39a.voluntariossv.firelisteners;

import android.content.Context;
import android.content.Intent;

import androidx.annotation.NonNull;

import com.da39a.voluntariossv.Home;
import com.da39a.voluntariossv.HomeInstitucion;
import com.da39a.voluntariossv.firebase.Realtimedb;
import com.da39a.voluntariossv.modelos.*;
import com.da39a.voluntariossv.utils.Localbase;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

public class Checklogin implements ValueEventListener {

    Context ctx;
    DatabaseReference ref;

    public Checklogin(Context ctx) {
        this.ctx = ctx;
    }

    @Override
    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
        Usuario usuario = new Usuario(dataSnapshot);
        new Localbase(ctx).setUsuario(usuario);

        if(usuario.getTipo().equals("institucion")){
            ref = new Realtimedb().getInstitucion(usuario.getId());
            ref.addListenerForSingleValueEvent(new GetInstitucion());
        }else{
            ref = new Realtimedb().getVoluntario(usuario.getId());
            ref.addListenerForSingleValueEvent(new GetVoluntario());
        }
    }

    @Override
    public void onCancelled(@NonNull DatabaseError databaseError) {

    }

    public class GetInstitucion implements ValueEventListener{
        @Override
        public void onDataChange(@NonNull DataSnapshot snapshot) {
            Institucion institucion = new Institucion(snapshot);
            new Localbase(ctx).setInstitucion(institucion);
            ctx.startActivity(new Intent(ctx, HomeInstitucion.class));
        }

        @Override
        public void onCancelled(@NonNull DatabaseError error) {

        }
    }

    public class GetVoluntario implements ValueEventListener{
        @Override
        public void onDataChange(@NonNull DataSnapshot snapshot) {
            ctx.startActivity(new Intent(ctx, Home.class));
        }

        @Override
        public void onCancelled(@NonNull DatabaseError error) {

        }
    }
}
