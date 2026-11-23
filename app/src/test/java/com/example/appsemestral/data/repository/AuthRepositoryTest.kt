package com.example.appsemestral.data.repository

import com.example.appsemestral.data.remote.LoginRequest
import com.example.appsemestral.data.remote.LoginResponse
import com.example.appsemestral.data.remote.WimApi
import com.example.appsemestral.model.Rol
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test

class AuthRepositoryTest {

    private val api = mockk<WimApi>()
    private val repo = AuthRepository(api)

    @Test
    fun `login convierte la respuesta en Sesion con el rol del servidor`() = runTest {
        coEvery { api.login(LoginRequest("direccion", "wim2026")) } returns
            LoginResponse(3, "direccion", "Dirección WIM Demo", "DIRECCION")

        val sesion = repo.login("  Direccion ", "wim2026")

        assertEquals(Rol.DIRECCION, sesion.rol)
        assertEquals("Dirección WIM Demo", sesion.nombre)
        assertEquals(3L, sesion.id)
    }
}
