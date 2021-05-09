package com.da39a.voluntariossv.modelos;

import com.google.firebase.database.DataSnapshot;

public class Institucion {

    private String id;
    private String nombre;
    private String descripcion;
    private String rubro;
    private Localizacion localizacion;

    public Institucion(){

    }

    public Institucion(DataSnapshot d){
        this.setId(d.getKey());
        this.setNombre(d.child("nombre").getValue().toString());
        this.setDescripcion(d.child("descripcion").getValue().toString());
        this.setRubro(d.child("rubro").getValue().toString());
        this.setLocalizacion(new Localizacion());
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

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getRubro() {
        return rubro;
    }

    public void setRubro(String rubro) {
        this.rubro = rubro;
    }

    public Localizacion getLocalizacion() {
        return localizacion;
    }

    public void setLocalizacion(Localizacion localizacion) {
        this.localizacion = localizacion;
    }
}
