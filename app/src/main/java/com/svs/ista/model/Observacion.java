package com.svs.ista.model;

public class Observacion {
    private Integer id_observacion;
    private String observacion;
    private Usuarios usuario;
    private Actividad actividad;

    public Observacion(String observacion) {
        this.observacion = observacion;
    }

    public Observacion(Integer id_observacion, String observacion, Usuarios usuario, Actividad actividad) {
        this.id_observacion = id_observacion;
        this.observacion = observacion;
        this.usuario = usuario;
        this.actividad = actividad;
    }

    public Observacion(String observacion, Usuarios usuario, Actividad actividad) {
        this.observacion = observacion;
        this.usuario = usuario;
        this.actividad = actividad;
    }

    public Integer getId_observacion() {
        return id_observacion;
    }

    public void setId_observacion(Integer id_observacion) {
        this.id_observacion = id_observacion;
    }

    public String getObservacion() {
        return observacion;
    }

    public void setObservacion(String observacion) {
        this.observacion = observacion;
    }

    public Usuarios getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuarios usuario) {
        this.usuario = usuario;
    }

    public Actividad getActividad() {
        return actividad;
    }

    public void setActividad(Actividad actividad) {
        this.actividad = actividad;
    }
}
