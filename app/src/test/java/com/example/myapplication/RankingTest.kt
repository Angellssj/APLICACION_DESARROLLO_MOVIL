package com.example.myapplication

import com.example.myapplication.model.Candidata
import com.example.myapplication.model.Evaluacion
import com.example.myapplication.model.Ranking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class RankingTest {
    private fun candidata(id: Long, nombre: String) =
        Candidata(id = id, nombre = nombre, correo = "$id@ejemplo.cl", area = "Datos", experiencia = 1)

    private fun evaluacion(candidataId: Long, puntaje: Int) = Evaluacion(
        candidataId = candidataId, evaluadora = "Ángel González",
        tecnica = puntaje, comunicacion = puntaje, trabajoEquipo = puntaje, resolucion = puntaje,
        fecha = 0L
    )

    @Test fun promedioEsLaMediaDeLosCriterios() {
        val evaluacion = Evaluacion(
            candidataId = 1, evaluadora = "Ana", tecnica = 10, comunicacion = 8,
            trabajoEquipo = 6, resolucion = 4, fecha = 0L
        )
        assertEquals(7.0, evaluacion.promedio, 0.001)
    }

    @Test fun ordenaPorPromedioDescendente() {
        val candidatas = listOf(candidata(1, "Ana López"), candidata(2, "Beatriz Soto"))
        val resultado = Ranking.construir(candidatas, listOf(evaluacion(1, 6), evaluacion(2, 9)))
        assertEquals(listOf(2L, 1L), resultado.map { it.candidata.id })
    }

    @Test fun promediaVariasEvaluacionesDeLaMismaCandidata() {
        val resultado = Ranking.construir(
            listOf(candidata(1, "Ana López")),
            listOf(evaluacion(1, 4), evaluacion(1, 8))
        )
        assertEquals(6.0, resultado.single().promedio!!, 0.001)
        assertEquals(2, resultado.single().cantidadEvaluaciones)
    }

    @Test fun sinEvaluarVanAlFinalPorNombre() {
        val candidatas = listOf(
            candidata(1, "Zoe Pérez"), candidata(2, "Ana López"), candidata(3, "Carla Rojas")
        )
        val resultado = Ranking.construir(candidatas, listOf(evaluacion(3, 5)))
        assertEquals(listOf(3L, 2L, 1L), resultado.map { it.candidata.id })
        assertNull(resultado.last().promedio)
    }
}
