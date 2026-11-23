package com.example.appsemestral.data.remote

import retrofit2.HttpException
import java.io.IOException

/** Traduce cada error técnico a un mensaje que la usuaria entienda. */
fun Throwable.mensajeAmigable(): String = when (this) {
    is HttpException -> when (code()) {
        400 -> "Datos inválidos: revisa el formulario"
        401 -> "Usuario o contraseña incorrectos"
        404 -> "El registro ya no existe en el servidor"
        409 -> "Ya existe una candidatura con este correo"
        else -> "Error del servidor (${code()})"
    }
    is IOException -> "No se pudo conectar con el servidor. ¿Está corriendo el backend?"
    else -> message ?: "Error inesperado"
}
