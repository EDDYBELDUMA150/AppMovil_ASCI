package com.svs.ista.model;

public class Notificacion {
    private Integer id;
    private Integer usuario;
    private String rol;
    private String mensaje;
    //@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "America/Guayaquil")
    private String fecha;
    private boolean visto;

    public Notificacion(Integer id, Integer usuario, String rol, String mensaje) {
        this.id = id;
        this.usuario = usuario;
        this.rol = rol;
        this.mensaje = mensaje;
    }

    public Notificacion(Integer usuario, String rol, String mensaje, String fecha, boolean visto) {
        this.usuario = usuario;
        this.rol = rol;
        this.mensaje = mensaje;
        this.fecha = fecha;
        this.visto = visto;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getUsuario() {
        return usuario;
    }

    public void setUsuario(Integer usuario) {
        this.usuario = usuario;
    }

    public String getRol() {
        return rol;
    }

    public void setRol(String rol) {
        this.rol = rol;
    }

    public String getMensaje() {
        return mensaje;
    }

    public void setMensaje(String mensaje) {
        this.mensaje = mensaje;
    }

    public String getFecha() {
        return fecha;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }

    public boolean isVisto() {
        return visto;
    }

    public void setVisto(boolean visto) {
        this.visto = visto;
    }
}
