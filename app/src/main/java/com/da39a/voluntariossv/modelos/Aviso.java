package com.da39a.voluntariossv.modelos;

import androidx.annotation.NonNull;

import com.da39a.voluntariossv.firebase.Realtimedb;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

public class Aviso {

    private String id;
    private String titulo;
    private String descripcion;
    private long fecha;
    private int vacantes;
    private String institucionFK;
    private Institucion institucion;
    private List<Voluntario> aplicantes;
    private String extra;

    public Aviso(){

    }

    public Aviso(DataSnapshot d){
        this.setId(d.getKey());
        this.setTitulo(d.child("titulo").getValue().toString());
        this.setDescripcion(d.child("descripcion").getValue().toString());
        this.setFecha(d.child("fecha").getValue(Long.class));
        this.setVacantes(d.child("vacantes").getValue(Integer.class));
        this.setInstitucionFK(d.child("institucion").getValue().toString());
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

    public List<Voluntario> getAplicantes() {
        return aplicantes;
    }

    public void setAplicantes(List<Voluntario> aplicantes) {
        this.aplicantes = aplicantes;
    }

    public String getInstitucionFK() {
        return institucionFK;
    }

    public void setInstitucionFK(String institucionFK) {
        this.institucionFK = institucionFK;
    }

    public String getExtra() {
        return extra;
    }

    public void setExtra(String extra) {
        this.extra = extra;
    }
}
