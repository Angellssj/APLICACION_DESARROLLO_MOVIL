package com.example.appsemestral.ui.viewmodel

import androidx.lifecycle.SavedStateHandle
import com.example.appsemestral.data.repository.CandidataRepository
import com.example.appsemestral.data.repository.RandomUserRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import retrofit2.HttpException
import retrofit2.Response
import java.io.IOException

@OptIn(ExperimentalCoroutinesApi::class)
class CandidataFormViewModelTest {

    private val repository = mockk<CandidataRepository>()
    private val randomUser = mockk<RandomUserRepository>()
    private lateinit var vm: CandidataFormViewModel

    @Before
    fun setUp() {
        Dispatchers.setMain(UnconfinedTestDispatcher())
        vm = CandidataFormViewModel(SavedStateHandle(), repository, randomUser)
    }

    @After
    fun tearDown() = Dispatchers.resetMain()

    private fun llenarFormulario() {
        vm.onNombre("Ana Demo Pérez")
        vm.onCorreo("ana@ejemplo.cl")
        vm.onTelefono("9 1111 2222")
        vm.onArea("Seguridad ofensiva (Red Team)")
        vm.onAnios("5")
        vm.onResumen("Resumen ficticio de al menos treinta caracteres.")
        vm.onLinkedin("https://www.linkedin.com/in/ana-demo")
    }

    @Test
    fun `validarTodo falla con el formulario vacio`() {
        assertFalse(vm.validarTodo())
        assertEquals("Selecciona un área", vm.uiState.value.areaError)
    }

    @Test
    fun `409 marca el error en el correo`() {
        llenarFormulario()
        coEvery { repository.registrar(any()) } throws HttpException(Response.error<Any>(409, "".toResponseBody()))

        vm.guardar("Camila Demo")

        assertEquals("Ya existe una candidatura con este correo", vm.uiState.value.correo.error)
        assertFalse(vm.uiState.value.guardando)
    }

    @Test
    fun `guardar correcto registra y marca guardado`() {
        llenarFormulario()
        coEvery { repository.registrar(any()) } returns 7L

        vm.guardar("Camila Demo")

        assertTrue(vm.uiState.value.guardado)
        coVerify { repository.registrar(match { it.telefono == "+56 9 1111 2222" && it.registradaPor == "Camila Demo" }) }
    }

    @Test
    fun `generarNombreFicticio completa el nombre con la API externa`() {
        coEvery { randomUser.nombreFicticio() } returns "Lucía Gómez"

        vm.generarNombreFicticio()

        assertEquals("Lucía Gómez", vm.uiState.value.nombre.valor)
    }

    @Test
    fun `si la API externa falla muestra un mensaje y no rompe el formulario`() {
        coEvery { randomUser.nombreFicticio() } throws IOException("sin internet")

        vm.generarNombreFicticio()

        assertTrue(vm.uiState.value.mensaje!!.startsWith("API externa no disponible"))
    }
}
