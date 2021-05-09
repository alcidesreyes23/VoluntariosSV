package com.da39a.voluntariossv.modelos;

import android.util.Log;

import com.google.firebase.database.DataSnapshot;

public class Usuario {

    private String id;
    private String tipo;

    public Usuario(){

    }

    public Usuario(DataSnapshot ds){
        this.setId(ds.getKey());
        this.setTipo(ds.getValue().toString());
    }

    /*GETTERS AND SETTERS*/

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public boolean esVoluntario(){
        return this.tipo.equals("voluntario");
    }

    public boolean esInstitucion(){
        return this.tipo.equals("institucion");
    }

}
