package com.example.appsemestral.model

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class CalculadoraPuntajeTest {

    private val criterios = listOf(Criterio(1, "A", 60), Criterio(2, "B", 40))

    private fun ev(criterioId: Long, puntaje: Int) =
        Evaluacion(candidataId = 1, criterioId = criterioId, puntaje = puntaje, evaluadora = "Demo")

    @Test
    fun `puntaje maximo es 100`() {
        assertEquals(100.0, CalculadoraPuntaje.total(criterios, listOf(ev(1, 5), ev(2, 5))), 0.001)
    }

    @Test
    fun `puntaje se pondera por peso`() {
        // (5*60 + 1*40) / (5*100) = 68
        assertEquals(68.0, CalculadoraPuntaje.total(criterios, listOf(ev(1, 5), ev(2, 1))), 0.001)
    }

    @Test
    fun `sin criterios el puntaje es 0`() {
        assertEquals(0.0, CalculadoraPuntaje.total(emptyList(), emptyList()), 0.001)
    }

    @Test
    fun `evaluacion completa solo si todos los criterios tienen puntaje`() {
        assertFalse(CalculadoraPuntaje.estaCompleta(criterios, listOf(ev(1, 3))))
        assertTrue(CalculadoraPuntaje.estaCompleta(criterios, listOf(ev(1, 3), ev(2, 4))))
    }

    @Test
    fun `estado resuelto solo para seleccionada y no seleccionada`() {
        assertTrue(EstadoCandidatura.SELECCIONADA.estaResuelta)
        assertTrue(EstadoCandidatura.NO_SELECCIONADA.estaResuelta)
        assertFalse(EstadoCandidatura.EVALUADA.estaResuelta)
    }
}
