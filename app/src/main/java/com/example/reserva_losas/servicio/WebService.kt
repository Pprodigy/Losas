package com.example.reserva_losas.servicio

import com.example.reserva_losas.model.Administrador
import com.example.reserva_losas.model.Cliente
import com.example.reserva_losas.model.Imagen
import com.example.reserva_losas.model.Losas
import com.example.reserva_losas.model.Reserva
import com.example.reserva_losas.model.ReservaRequest
import com.example.reserva_losas.model.Usuario
import com.example.reserva_losas.model.UsuarioResponse
import com.google.gson.GsonBuilder
import retrofit2.Call
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

object AppConstantes{
    const val BASE_URL = "http://192.168.1.12:3000"
}

interface WebService{
    @GET("/losas")
    suspend fun cargarLosas():Response<LosasResponse>

    @POST("/losa/agregar")
    suspend fun agregarLosa(@Body losas: Losas):Response<String>

    @PUT("/losa/actualizar/{id}")
    suspend fun actualizarLosa(@Path("id") id:Int, @Body losas: Losas):Response<String>

    @DELETE("/losa/eliminar/{id}")
    suspend fun eliminarLosa(@Path("id")id: Int):Response<String>

    @GET("imagenes")
    suspend fun obtenerImagenes(): Response<ImagenResponse>

    @POST("/imagen/agregar")
    suspend fun agregarImagen(@Body imagen: Imagen): Response<String>

    @DELETE("/imagen/eliminar/{id}")
    suspend fun eliminarImagen(@Path("id") id: Int): Response<String>

    @POST("/login")
    fun login(@Body datos: Map<String, String>): Call<UsuarioResponse>

    @POST("reserva/agregar")
    suspend fun agregarReserva(@Body reserva: ReservaRequest): Response<String>

    @GET("/cliente/{id}")
    fun obtenerCliente(@Path("id") id: Int): Call<Cliente>

    @GET("/cliente/{id}/reservas")
    fun obtenerReservas(@Path("id") id: Int): Call<List<Reserva>>

    @PUT("/reserva/cancelar/{id}")
    fun cancelarReserva(@Path("id") id: Int): Call<String>

    @POST("/reserva/verificar")
    suspend fun verificarDisponibilidad(@Body reserva: ReservaRequest): Response<Map<String, Boolean>>

    // Lista combinada
    @GET("/usuarios")
    suspend fun obtenerTodosLosUsuarios(): Response<List<Usuario>>

    // Clientes
    @GET("/admin/clientes")
    suspend fun obtenerClientes(): Response<List<Cliente>>

    @POST("/admin/cliente/agregar")
    suspend fun agregarCliente(@Body cliente: Cliente): Response<String>

    @PUT("/admin/cliente/editar/{id}")
    suspend fun editarCliente(@Path("id") id: Int, @Body cliente: Cliente): Response<String>

    @DELETE("/admin/cliente/eliminar/{id}")
    suspend fun eliminarCliente(@Path("id") id: Int): Response<String>

    // Administradores
    @GET("/admin/administradores")
    suspend fun obtenerAdministradores(): Response<List<Administrador>>

    @PUT("/admin/admin/editar/{id}")
    suspend fun editarAdministrador(@Path("id") id: Int, @Body administrador: Administrador): Response<String>

    @DELETE("/admin/admin/eliminar/{id}")
    suspend fun eliminarAdministrador(@Path("id") id: Int): Response<String>

    @GET("/usuarios")
    suspend fun obtenerUsuarios(): Response<List<Usuario>>

    @DELETE("/admin/admin/eliminar/{id}")
    suspend fun eliminarAdmin(@Path("id") id: Int): Response<String>

    @POST("/admin/admin/agregar")
    suspend fun agregarAdmin(@Body datos: Map<String, String>): Response<String>

    @POST("/admin/cliente/agregar")
    suspend fun agregarCliente(@Body datos: Map<String, String>): Response<String>

    @PUT("/admin/admin/editar/{id}")
    suspend fun editarAdmin(@Path("id") id: Int, @Body datos: Map<String, String>): Response<String>

    @PUT("/admin/cliente/editar/{id}")
    suspend fun editarCliente(@Path("id") id: Int, @Body datos: Map<String, String>): Response<String>


}

object  RetrofitClient{
    val webService:WebService by lazy {
        Retrofit.Builder()
            .baseUrl(AppConstantes.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create(GsonBuilder().create()))
            .build()
            .create(WebService::class.java)
    }
}