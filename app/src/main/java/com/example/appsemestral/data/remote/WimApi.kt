package com.example.appsemestral.data.remote

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

/** Microservicio propio wim100-backend. */
interface WimApi {

    @GET("Candidatas")
    suspend fun listar(): List<PostulanteDto>

    @GET("Candidatas/{id}")
    suspend fun obtener(@Path("id") id: Long): PostulanteDto

    @POST("Candidatas")
    suspend fun crear(@Body body: PostulanteDto): PostulanteDto

    @PUT("Candidatas/{id}")
    suspend fun actualizar(@Path("id") id: Long, @Body body: PostulanteDto): PostulanteDto

    @PATCH("Candidatas/{id}/estado")
    suspend fun cambiarEstado(@Path("id") id: Long, @Body body: EstadoRequest): PostulanteDto

    @DELETE("Candidatas/{id}")
    suspend fun eliminar(@Path("id") id: Long): Response<Unit>

    @POST("auth/login")
    suspend fun login(@Body body: LoginRequest): LoginResponse
}
