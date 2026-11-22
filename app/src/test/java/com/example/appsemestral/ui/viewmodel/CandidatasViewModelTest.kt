package com.example.appsemestral.ui.viewmodel

import com.example.appsemestral.data.repository.CandidataRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test
import java.io.IOException

@OptIn(ExperimentalCoroutinesApi::class)
class CandidatasViewModelTest {

    private val repository = mockk<CandidataRepository>()

    @Before
    fun setUp() {
        Dispatchers.setMain(UnconfinedTestDispatcher())
        every { repository.observarTodas() } returns flowOf(emptyList())
    }

    @After
    fun tearDown() = Dispatchers.resetMain()

    @Test
    fun `refrescar con error de red muestra el mensaje`() {
        coEvery { repository.refrescar() } throws IOException("sin red")

        val vm = CandidatasViewModel(repository)

        assertEquals("No se pudo conectar con el servidor. ¿Está corriendo el backend?", vm.error.value)
    }

    @Test
    fun `refrescar correcto no deja error`() {
        coEvery { repository.refrescar() } returns Unit

        val vm = CandidatasViewModel(repository)

        assertNull(vm.error.value)
        coVerify { repository.refrescar() }
    }
}
