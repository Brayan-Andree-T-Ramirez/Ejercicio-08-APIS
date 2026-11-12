package com.upiiz.dm_batrb_08.models;

public class PeticionCliente {
    private Long id;
    private String nombre;
    private String correo;
    private String telefono;
    private String direccion;
    private Boolean registrado;


    public PeticionCliente(Long id, String nombre, String correo, String telefono, String direccion, Boolean registrado) {
        this.id = id;
        this.nombre = nombre;
        this.correo = correo;
        this.telefono = telefono;
        this.direccion = direccion;
        this.registrado = registrado;
    }

    public PeticionCliente(String nombre, String correo, String telefono, String direccion, Boolean registrado) {
        this.nombre = nombre;
        this.correo = correo;
        this.telefono = telefono;
        this.direccion = direccion;
        this.registrado = registrado;
    }
}
