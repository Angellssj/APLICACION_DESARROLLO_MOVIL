package com.example.appsemestral.data.repository

import androidx.room.withTransaction
import com.example.appsemestral.data.local.WimDatabase
import com.example.appsemestral.model.Acciones
import com.example.appsemestral.model.CalculadoraPuntaje
import com.example.appsemestral.model.Criterio
import com.example.appsemestral.model.EstadoCandidatura
import com.example.appsemestral.model.Evaluacion
import kotlinx.coroutines.flow.Flow

/** Evaluaciones y criterios: viven solo en Room. */
class EvaluacionRepository(
    private val db: WimDatabase,
    private val historial: HistorialRepository
) {
    fun observarCriterios(): Flow<List<Criterio>> = db.criterioDao().observarTodos()

    fun observarEvaluaciones(candidataId: Long): Flow<List<Evaluacion>> =
        db.evaluacionDao().observarPorCandidata(candidataId)

    suspend fun asegurarCriterios(criterios: List<Criterio>) {
        db.criterioDao().insertarTodos(criterios)
    }

    /**
     * Guarda los puntajes (criterioId → 1..5) y calcula el nuevo estado de la candidata.
     * Devuelve el puntaje total de 0 a 100.
     */
    suspend fun registrar(candidataId: Long, puntajes: Map<Long, Int>, evaluadora: String): Double {
        require(puntajes.values.all { it in 1..5 }) { "Los puntajes van de 1 a 5" }
        var total = 0.0
        db.withTransaction {
            val criterios = db.criterioDao().obtenerTodos()
            db.evaluacionDao().guardarTodas(
                puntajes.map { (criterioId, puntaje) ->
                    Evaluacion(candidataId = candidataId, criterioId = criterioId, puntaje = puntaje, evaluadora = evaluadora)
                }
            )
            val todas = db.evaluacionDao().obtenerPorCandidata(candidataId)
            total = CalculadoraPuntaje.total(criterios, todas)
            val candidata = db.candidataDao().obtenerPorId(candidataId)
            if (candidata != null && !candidata.estado.estaResuelta) {
                val nuevo = if (CalculadoraPuntaje.estaCompleta(criterios, todas)) {
                    EstadoCandidatura.EVALUADA
                } else {
                    EstadoCandidatura.EN_EVALUACION
                }
                db.candidataDao().actualizarEstado(candidataId, nuevo)
            }
        }
        historial.registrar(candidataId, Acciones.EVALUACION, "Puntaje total ${"%.1f".format(total)} / 100", evaluadora)
        return total
    }
}
