package com.upiiz.dm_batrb_08.network;

import com.upiiz.dm_batrb_08.network.api.ApiCliente;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ClienteRetrofit {
    //Se comunica con la API - Clientes
    private static final String BASE_URL="https://fw-ejercicio-08.onrender.com/";

    private static final Retrofit retrofit = new Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .build();

    public static ApiCliente obtenerApiCliente(){
        return retrofit.create(ApiCliente.class);
    }

    /*
    public static ApiProductos obtenerApiProductos(){
        return retrofit.create(ApiProductos.class);
    }
    */

}
