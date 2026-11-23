package com.example.appsemestral.ui.viewmodel

import com.example.appsemestral.data.repository.AuthRepository
import com.example.appsemestral.data.repository.HistorialRepository
import com.example.appsemestral.model.Rol
import com.example.appsemestral.model.Sesion
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Assert.fail
import org.junit.Before
import org.junit.Test
import retrofit2.HttpException
import retrofit2.Response

@OptIn(ExperimentalCoroutinesApi::class)
class SesionViewModelTest {

    private val auth = mockk<AuthRepository>()
    private val historial = mockk<HistorialRepository>(relaxed = true)
    private lateinit var vm: SesionViewModel

    @Before
    fun setUp() {
        Dispatchers.setMain(UnconfinedTestDispatcher())
        vm = SesionViewModel(auth, historial)
    }

    @After
    fun tearDown() = Dispatchers.resetMain()

    @Test
    fun `validar falla con campos vacios`() {
        assertFalse(vm.validar())
        assertEquals("Ingresa tu usuario", vm.loginState.value.usuario.error)
    }

    @Test
    fun `login correcto crea la sesion con el rol del servidor`() = runTest {
        coEvery { auth.login("evaluadora1", "wim2026") } returns
            Sesion(nombre = "Camila Rojas Demo", rol = Rol.EVALUADORA, id = 1, usuario = "evaluadora1")
        vm.onUsuario("evaluadora1"); vm.onPassword("wim2026")
        var navego = false

        vm.iniciarSesion("test") { navego = true }

        assertTrue(navego)
        assertEquals(Rol.EVALUADORA, vm.sesion.value?.rol)
    }

    @Test
    fun `login con 401 muestra error y no crea sesion`() = runTest {
        coEvery { auth.login(any(), any()) } throws HttpException(Response.error<Any>(401, "".toResponseBody()))
        vm.onUsuario("evaluadora1"); vm.onPassword("malapass")

        vm.iniciarSesion("test") { fail("No debería navegar") }

        assertNull(vm.sesion.value)
        assertEquals("Usuario o contraseña incorrectos", vm.loginState.value.error)
    }

    @Test
    fun `cerrar sesion limpia la sesion`() = runTest {
        coEvery { auth.login(any(), any()) } returns Sesion("Camila", Rol.EVALUADORA)
        vm.onUsuario("evaluadora1"); vm.onPassword("wim2026")
        vm.iniciarSesion("test") {}

        vm.cerrarSesion()

        assertNull(vm.sesion.value)
    }
}
