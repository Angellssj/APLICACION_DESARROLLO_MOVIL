package com.example.appsemestral.model

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class RankingTest {

    private val criterios = listOf(Criterio(1, "A", 60), Criterio(2, "B", 40))

    private fun candidata(id: Long, fecha: Long = id) = Candidata(
        id = id, nombre = "Candidata $id", correo = "c$id@demo.cl", telefono = "+56911111111",
        area = Candidata.AREAS.first(), aniosExperiencia = 3, resumenExperiencia = "Resumen",
        linkedinUrl = "https://www.linkedin.com/in/c$id", fechaRegistro = fecha, registradaPor = "Demo"
    )

    private fun ev(candidataId: Long, criterioId: Long, puntaje: Int) =
        Evaluacion(candidataId = candidataId, criterioId = criterioId, puntaje = puntaje, evaluadora = "Demo")

    @Test
    fun `ordena de mayor a menor puntaje`() {
        val filas = Ranking.calcular(
            candidatas = listOf(candidata(1), candidata(2)),
            criterios = criterios,
            evaluaciones = listOf(ev(1, 1, 2), ev(1, 2, 2), ev(2, 1, 5), ev(2, 2, 5))
        )
        assertEquals(listOf(2L, 1L), filas.map { it.candidata.id })
        assertEquals(100.0, filas.first().puntaje, 0.001)
    }

    @Test
    fun `excluye a las candidatas sin evaluaciones`() {
        val filas = Ranking.calcular(
            candidatas = listOf(candidata(1), candidata(2)),
            criterios = criterios,
            evaluaciones = listOf(ev(1, 1, 3))
        )
        assertEquals(listOf(1L), filas.map { it.candidata.id })
    }

    @Test
    fun `marca la evaluacion incompleta`() {
        val filas = Ranking.calcular(listOf(candidata(1)), criterios, listOf(ev(1, 1, 5)))
        assertFalse(filas.single().completa)
        assertEquals(60.0, filas.single().puntaje, 0.001)
    }

    @Test
    fun `en empate gana la evaluacion completa y luego la registrada antes`() {
        // 1 y 2: completas con 60 puntos; 3: incompleta con 60 puntos
        val filas = Ranking.calcular(
            candidatas = listOf(candidata(1, fecha = 200), candidata(2, fecha = 100), candidata(3, fecha = 50)),
            criterios = criterios,
            evaluaciones = listOf(
                ev(1, 1, 3), ev(1, 2, 3),
                ev(2, 1, 3), ev(2, 2, 3),
                ev(3, 1, 5)
            )
        )
        assertEquals(listOf(2L, 1L, 3L), filas.map { it.candidata.id })
        assertTrue(filas[0].completa && filas[1].completa)
    }
}
