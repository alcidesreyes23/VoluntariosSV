package com.da39a.voluntariossv.modelos;

import androidx.annotation.NonNull;

import com.da39a.voluntariossv.firebase.Realtimedb;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class Aviso {

    private String id;
    private String titulo;
    private String descripcion;
    private long fecha;
    private long expiracion;
    private int vacantes;
    private int edadmax;
    private int edadmin;
    private String institucionFK;
    private Institucion institucion;
    private List<String> aplicantes;
    private String extra;

    public Aviso(){

    }

    public Aviso(DataSnapshot d){
        this.setId(d.getKey());
        this.setTitulo(d.child("titulo").getValue().toString());
        this.setDescripcion(d.child("descripcion").getValue().toString());
        this.setFecha(d.child("fecha").getValue(Long.class));
        this.setVacantes(d.child("vacantes").getValue(Integer.class));
        this.setEdadmax(d.child("edadmax").getValue(Integer.class));
        this.setEdadmin(d.child("edadmin").getValue(Integer.class));
        this.setExpiracion(d.child("expiracion").getValue(Long.class));
        this.setInstitucion(new Institucion(d.child("institucion").getChildren().iterator().next()));
        this.setAplicantes(new ArrayList<>());
        if(d.child("aplicantes").exists()){
            for(DataSnapshot da : d.child("aplicantes").getChildren()){
                this.getAplicantes().add(da.getKey());
            }
        }
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public long getFecha() {
        return fecha;
    }

    public void setFecha(long fecha) {
        this.fecha = fecha;
    }

    public int getVacantes() {
        return vacantes;
    }

    public void setVacantes(int vacantes) {
        this.vacantes = vacantes;
    }

    public Institucion getInstitucion() {
        return institucion;
    }

    public void setInstitucion(Institucion institucion) {
        this.institucion = institucion;
    }

    public void setInstitucionFK(String institucionFK) {
        this.institucionFK = institucionFK;
    }

    public List<String> getAplicantes() {
        return aplicantes;
    }

    public void setAplicantes(List<String> aplicantes) {
        this.aplicantes = aplicantes;
    }

    public String getInstitucionFK() {
        return institucionFK;
    }

    /*public void setInstitucionFK(String institucionFK) {
        this.institucionFK = institucionFK;
    }*/

    public String getExtra() {
        return extra;
    }

    public void setExtra(String extra) {
        this.extra = extra;
    }

    public long getExpiracion() {
        return expiracion;
    }

    public void setExpiracion(long expiracion) {
        this.expiracion = expiracion;
    }

    public int getEdadmax() {
        return edadmax;
    }

    public void setEdadmax(int edadmax) {
        this.edadmax = edadmax;
    }

    public int getEdadmin() {
        return edadmin;
    }

    public void setEdadmin(int edadmin) {
        this.edadmin = edadmin;
    }
}
