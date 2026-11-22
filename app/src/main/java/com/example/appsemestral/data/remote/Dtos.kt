package com.example.appsemestral.data.remote

/** JSON de un postulante tal como lo envía y recibe el backend. */
data class PostulanteDto(
    val id: Long? = null,
    val nombre: String,
    val correo: String,
    val telefono: String,
    val area: String,
    val aniosExperiencia: Int,
    val resumenExperiencia: String,
    val linkedinUrl: String,
    val cvNombre: String? = null,
    val estado: String? = null,
    val fechaRegistro: Long? = null,
    val registradaPor: String
)

data class EstadoRequest(val estado: String)

data class LoginRequest(val usuario: String, val password: String)

data class LoginResponse(val id: Long, val usuario: String, val nombre: String, val rol: String)

// ---- API externa: randomuser.me ----
data class RandomUserResponse(val results: List<RandomUser>)
data class RandomUser(val name: RandomName)
data class RandomName(val first: String, val last: String)
