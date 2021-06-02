package com.da39a.voluntariossv.modelos;

import com.google.firebase.database.DataSnapshot;

public class Cita {

    private String id;
    private String encargado;
    private String fecha;
    private String hora;
    private String mensaje;
    private String oficina;
    private Institucion institucion;
    private String avisoId;

    public Cita(DataSnapshot d) {
        this.setId(d.getKey());
        this.setAvisoId(d.child("avisoId").getValue().toString());
        this.setEncargado(d.child("encargado").getValue().toString());
        this.setFecha(d.child("fecha").getValue().toString());
        this.setHora(d.child("hora").getValue().toString());
        this.setMensaje(d.child("mensaje").getValue().toString());
        this.setOficina(d.child("oficina").getValue().toString());
        this.setInstitucion(new Institucion(d.child("institucion").getChildren().iterator().next()));
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getEncargado() {
        return encargado;
    }

    public void setEncargado(String encargado) {
        this.encargado = encargado;
    }

    public String getFecha() {
        return fecha;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }

    public String getHora() {
        return hora;
    }

    public void setHora(String hora) {
        this.hora = hora;
    }

    public String getMensaje() {
        return mensaje;
    }

    public void setMensaje(String mensaje) {
        this.mensaje = mensaje;
    }

    public String getOficina() {
        return oficina;
    }

    public void setOficina(String oficina) {
        this.oficina = oficina;
    }

    public Institucion getInstitucion() {
        return institucion;
    }

    public void setInstitucion(Institucion institucion) {
        this.institucion = institucion;
    }

    public String getAvisoId() {
        return avisoId;
    }

    public void setAvisoId(String avisoId) {
        this.avisoId = avisoId;
    }
}
