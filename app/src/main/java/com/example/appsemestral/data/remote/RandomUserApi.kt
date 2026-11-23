package com.example.appsemestral.data.remote

import retrofit2.http.GET
import retrofit2.http.Query

/** API externa pública (sin key). Solo se usa para generar nombres ficticios. */
interface RandomUserApi {

    @GET("api/")
    suspend fun generar(
        @Query("gender") genero: String = "female",
        @Query("nat") nacionalidad: String = "es",
        @Query("inc") campos: String = "name"
    ): RandomUserResponse
}
