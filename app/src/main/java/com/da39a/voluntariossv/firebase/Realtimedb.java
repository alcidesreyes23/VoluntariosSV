package com.da39a.voluntariossv.firebase;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class Realtimedb {

    private DatabaseReference ref;

    public Realtimedb(){
        this.ref = FirebaseDatabase.getInstance().getReference();
    }

    public DatabaseReference getVoluntarios(){
        return this.ref.child("voluntarios");
    }

    public DatabaseReference getVoluntario(String id){
        return getVoluntarios().child(id);
    }

    public DatabaseReference getInstituciones(){
        return this.ref.child("instituciones");
    }

    public DatabaseReference getInstitucion(String id){
        return getInstituciones().child(id);
    }

    public DatabaseReference getUsuarios(){
        return this.ref.child("usuarios");
    }

    public DatabaseReference getUsuario(String id){
        return getUsuarios().child(id);
    }

    public DatabaseReference getAvisos(){
        return this.ref.child("avisos");
    }

    public DatabaseReference getAviso(String id){
        return getAvisos().child(id);
    }


}
