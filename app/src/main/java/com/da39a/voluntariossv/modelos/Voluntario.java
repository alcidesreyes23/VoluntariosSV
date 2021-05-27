package com.da39a.voluntariossv.modelos;

import com.google.firebase.database.DataSnapshot;

public class Voluntario {

    private String id;
    private String nombre;
    private long nacimiento;
    private String ocupacion;
    private String fotoPerfil;

    public Voluntario() {
    }

    public Voluntario(DataSnapshot ds){
        this.setId(ds.getKey());
        this.setNombre(ds.child("nombre").getValue().toString());
        this.setNacimiento(ds.child("nacimiento").getValue(Long.class));
        this.setOcupacion(ds.child("ocupacion").getValue().toString());
        this.setFotoPerfil(ds.child("fotoPerfil").getValue().toString());
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public long getNacimiento() {
        return nacimiento;
    }

    public void setNacimiento(long nacimiento) {
        this.nacimiento = nacimiento;
    }

    public String getOcupacion() {
        return ocupacion;
    }

    public void setOcupacion(String ocupacion) {
        this.ocupacion = ocupacion;
    }

    public String getFotoPerfil() {return fotoPerfil; }

    public void setFotoPerfil(String fotoPerfil) { this.fotoPerfil = fotoPerfil;}
}
