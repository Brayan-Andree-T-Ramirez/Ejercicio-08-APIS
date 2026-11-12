package com.upiiz.dm_batrb_08.models;

public class RespuestaEliminarCliente {
    private int estado;
    private String mensaje;

    public RespuestaEliminarCliente(int estado, String mensaje) {
        this.estado = estado;
        this.mensaje = mensaje;
    }

    public int getEstado() {
        return estado;
    }

    public void setEstado(int estado) {
        this.estado = estado;
    }

    public String getMensaje() {
        return mensaje;
    }

    public void setMensaje(String mensaje) {
        this.mensaje = mensaje;
    }
}
