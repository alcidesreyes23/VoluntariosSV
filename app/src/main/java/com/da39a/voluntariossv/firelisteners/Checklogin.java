package com.da39a.voluntariossv.firelisteners;

import android.content.Context;
import android.content.Intent;

import androidx.annotation.NonNull;

import com.da39a.voluntariossv.Home;
import com.da39a.voluntariossv.modelos.*;
import com.da39a.voluntariossv.utils.Localbase;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

public class Checklogin implements ValueEventListener {

    Context ctx;

    public Checklogin(Context ctx) {
        this.ctx = ctx;
    }

    @Override
    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
        Usuario usuario = new Usuario(dataSnapshot);
        new Localbase(ctx).setUsuario(usuario);
        ctx.startActivity(new Intent(ctx, Home.class));
    }

    @Override
    public void onCancelled(@NonNull DatabaseError databaseError) {

    }
}
