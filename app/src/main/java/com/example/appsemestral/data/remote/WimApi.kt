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

    @GET("postulantes")
    suspend fun listar(): List<PostulanteDto>

    @GET("postulantes/{id}")
    suspend fun obtener(@Path("id") id: Long): PostulanteDto

    @POST("postulantes")
    suspend fun crear(@Body body: PostulanteDto): PostulanteDto

    @PUT("postulantes/{id}")
    suspend fun actualizar(@Path("id") id: Long, @Body body: PostulanteDto): PostulanteDto

    @PATCH("postulantes/{id}/estado")
    suspend fun cambiarEstado(@Path("id") id: Long, @Body body: EstadoRequest): PostulanteDto

    @DELETE("postulantes/{id}")
    suspend fun eliminar(@Path("id") id: Long): Response<Unit>

    @POST("auth/login")
    suspend fun login(@Body body: LoginRequest): LoginResponse
}
