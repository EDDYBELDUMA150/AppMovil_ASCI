package com.svs.ista.service;

import com.svs.ista.model.Actividad;
import com.svs.ista.model.Actividades;
import com.svs.ista.model.LoginUser;
import com.svs.ista.model.Notificacion;
import com.svs.ista.model.Observacion;
import com.svs.ista.model.Usuario;

import com.svs.ista.model.Criterios;
import com.svs.ista.model.Indicadores;
import com.svs.ista.model.Usuarios;

import java.util.List;

import kotlin.Unit;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Query;
import retrofit2.http.Path;

public interface ApiClient {
    @POST("generate-token")
    Call<Usuario> login(@Body LoginUser login);
    @GET("usuarios/buscaruser/{username}")
    Call<Usuarios>getusuarios(@Header("Authorization")String authToken, @Path("username") String username);
    @GET("api/actividad/listar")
    Call<List<Actividades>> listactividad(@Header("Authorization") String authToken);
    @GET("api/actividad/buscar/{id}")
    Call<Actividades> getactividad(@Header("Authorization") String authToken,@Path("id") Integer id);
    @GET("api/actividad/listar")
    Call<List<Actividad>> getactividad(@Header("Authorization") String authToken, @Query("fecha_inicio") String fechaInicio, @Query("fecha_fin") String fechaFin);
    @GET("api/persona/buscarpersona/{username}")
    Call<ResponseBody> obtenerPersona(@Path("username") String username, @Header("Authorization") String authToken);
    @GET("api/criterio/listarcriterios")
    Call<List<Criterios>> obtenerCriterios(@Header("Authorization") String authToken);
    @GET("api/indicadores/buscarindicador/{id}")
    Call<List<Indicadores>> obtenerIndicadores(@Path("id") Integer id, @Header("Authorization") String authToken);
    @POST("api/observacion/crear")
    Call<Observacion> crearobservacion(@Header("Authorization") String authToken, @Body Observacion obs);
    @POST("api/notificacion/crear")
    Call<Notificacion> crearnoti(@Header("Authorization") String authToken, @Body Notificacion not);
    @GET("api/notificacion/notificacionsinleer/{id}")
    Call<List<Notificacion>> listarnoti(@Header("Authorization") String authToken, @Path("id") Integer id);
    @PUT("api/observacion/actualizar/{id}")
    Call<Observacion> actualizarobservacion(@Header("Authorization") String authToken,@Path("id") Integer id, @Body Observacion obs);
    @DELETE("api/observacion/eliminar/{id}")
    Call<Void> eliminarObservacion(@Header("Authorization") String authToken, @Path("id") Integer id);

    @GET("api/observacion/buscarobs/{username}/{id}")
    Call<List<Observacion>> obtenerObs(@Header("Authorization") String authToken, @Path("username") String username, @Path("id") Integer id);
}
