package com.example.appsemestral.data.repository

import com.example.appsemestral.data.remote.LoginRequest
import com.example.appsemestral.data.remote.WimApi
import com.example.appsemestral.model.Rol
import com.example.appsemestral.model.Sesion

class AuthRepository(private val api: WimApi) {

    /** Lanza HttpException(401) si las credenciales son incorrectas. */
    suspend fun login(usuario: String, password: String): Sesion {
        val r = api.login(LoginRequest(usuario.trim().lowercase(), password))
        return Sesion(nombre = r.nombre, rol = Rol.valueOf(r.rol), id = r.id, usuario = r.usuario)
    }
}
