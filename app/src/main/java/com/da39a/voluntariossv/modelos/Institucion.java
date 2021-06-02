package com.da39a.voluntariossv.modelos;

import com.google.firebase.database.DataSnapshot;

import java.util.HashMap;
import java.util.Map;

public class Institucion {

    private String id;
    private String nombre;
    private String descripcion;
    private String rubro;
    private String telefono;
    private double latitud;
    private double longitud;

    public Institucion(){

    }

    public Institucion(DataSnapshot d){
        this.setId(d.getKey());
        this.setNombre(d.child("nombre").getValue().toString());
        this.setDescripcion(d.child("descripcion").getValue().toString());
        this.setRubro(d.child("rubro").getValue().toString());
        this.setTelefono(d.child("telefono").getValue().toString());
        this.setLatitud(d.child("localizacion").child("latitud").getValue(Double.class));
        this.setLongitud(d.child("localizacion").child("longitud").getValue(Double.class));
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

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public double getLatitud() {
        return latitud;
    }

    public void setLatitud(double latitud) {
        this.latitud = latitud;
    }

    public double getLongitud() {
        return longitud;
    }

    public void setLongitud(double longitud) {
        this.longitud = longitud;
    }


    public Map<String,Object> toMap(){
        Map<String,Object> data = new HashMap<>();
        data.put("nombre",this.getNombre());
        data.put("descripcion",this.getDescripcion());
        data.put("rubro",this.getRubro());
        data.put("telefono",this.getTelefono());

        Map<String,Object> localizacion = new HashMap<>();
        localizacion.put("latitud",this.getLatitud());
        localizacion.put("longitud",this.getLongitud());
        data.put("localizacion",localizacion);

        return data;
    }

}
