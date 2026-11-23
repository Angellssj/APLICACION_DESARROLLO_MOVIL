package com.example.appsemestral.data.remote

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object ApiConfig {
    /**
     * Emulador: 10.0.2.2 es el "localhost" del computador.
     * Dispositivo físico: usa la IP del computador en la red Wi-Fi, ej. http://192.168.1.50:8080/api/
     * La URL SIEMPRE termina en "/".
     */
    const val BASE_URL = "http://10.0.2.2:8080/api/"
    const val RANDOM_USER_URL = "https://randomuser.me/"
}

object RetrofitClient {

    // Muestra en Logcat cada petición y respuesta
    private val logging = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    private val okHttp = OkHttpClient.Builder()
        .addInterceptor(logging)
        .connectTimeout(10, TimeUnit.SECONDS)
        .readTimeout(10, TimeUnit.SECONDS)
        .build()

    private fun retrofit(baseUrl: String): Retrofit = Retrofit.Builder()
        .baseUrl(baseUrl)
        .client(okHttp)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    val wimApi: WimApi by lazy { retrofit(ApiConfig.BASE_URL).create(WimApi::class.java) }

    val randomUserApi: RandomUserApi by lazy {
        retrofit(ApiConfig.RANDOM_USER_URL).create(RandomUserApi::class.java)
    }
}
