package com.svs.ista.model;

import com.google.gson.annotations.SerializedName;
import java.time.LocalDate;

public class Actividad {
    @SerializedName("id_actividad")
    private Integer id_actividad;
    @SerializedName("fecha_inicio")
    private String fecha_inicio;
    @SerializedName("fecha_fin")
    private String fecha_fin;
    private String nombre;
    private Usuarios usuario;

    public Actividad(String fecha_inicio, String fecha_fin, String nombre, Usuarios usuario) {
        this.fecha_inicio = fecha_inicio;
        this.fecha_fin = fecha_fin;
        this.nombre = nombre;
        this.usuario=usuario;
    }

    public Usuarios getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuarios usuario) {
        this.usuario = usuario;
    }

    public Actividad(Integer id_actividad) {
        this.id_actividad = id_actividad;
    }

    public Integer getId_actividad() {
        return id_actividad;
    }

    public void setId_actividad(Integer id_actividad) {
        this.id_actividad = id_actividad;
    }

    public String getFecha_inicio() {
        return fecha_inicio;
    }

    public void setFecha_inicio(String fecha_inicio) {
        this.fecha_inicio = fecha_inicio;
    }

    public String getFecha_fin() {
        return fecha_fin;
    }

    public void setFecha_fin(String fecha_fin) {
        this.fecha_fin = fecha_fin;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }
}