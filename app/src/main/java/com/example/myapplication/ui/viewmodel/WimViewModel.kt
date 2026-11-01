package com.example.myapplication.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.data.CandidataRepository
import com.example.myapplication.model.Candidata
import com.example.myapplication.model.Evaluacion
import com.example.myapplication.model.Ranking
import com.example.myapplication.model.ResumenCandidata
import com.example.myapplication.validation.CandidataValidator
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

/** Estado visible + coordinación de datos; no dibuja interfaces. */
class WimViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = CandidataRepository(application.applicationContext)

    val candidatas: StateFlow<List<Candidata>> = repository.candidatas.mantener(emptyList())
    val evaluaciones: StateFlow<List<Evaluacion>> = repository.evaluaciones.mantener(emptyList())
    val ranking: StateFlow<List<ResumenCandidata>> =
        combine(repository.candidatas, repository.evaluaciones, Ranking::construir).mantener(emptyList())

    init {
        viewModelScope.launch { repository.importarDatosAnteriores() }
    }

    /** Devuelve null si se guarda correctamente; si falla, devuelve el mensaje. */
    suspend fun registrar(
        nombre: String,
        correo: String,
        area: String,
        experiencia: String,
        telefono: String,
        cvUri: String?
    ): String? {
        val errores = CandidataValidator.validar(nombre, correo, area, experiencia, telefono)
        if (errores.isNotEmpty()) return errores.values.first()
        return try {
            val registrada = repository.registrar(
                Candidata(
                    nombre = nombre.trim(),
                    correo = correo.trim().lowercase(),
                    area = area.trim(),
                    experiencia = experiencia.toInt(),
                    telefono = telefono.trim().ifBlank { null },
                    cvUri = cvUri
                )
            )
            if (registrada) null else "Ya existe una candidata con ese correo"
        } catch (_: Exception) {
            "No se pudo guardar la candidata en el teléfono"
        }
    }

    /** Devuelve null si se guarda correctamente; si falla, devuelve el mensaje. */
    suspend fun evaluar(
        candidataId: Long,
        evaluadora: String,
        puntajes: List<Int>,
        comentario: String
    ): String? {
        CandidataValidator.validarEvaluacion(puntajes)?.let { return it }
        return try {
            repository.evaluar(
                Evaluacion(
                    candidataId = candidataId,
                    evaluadora = evaluadora,
                    tecnica = puntajes[0],
                    comunicacion = puntajes[1],
                    trabajoEquipo = puntajes[2],
                    resolucion = puntajes[3],
                    comentario = comentario.trim(),
                    fecha = System.currentTimeMillis()
                )
            )
            null
        } catch (_: Exception) {
            "No se pudo guardar la evaluación"
        }
    }

    /** Devuelve null si se elimina; si falla, devuelve el mensaje. */
    suspend fun eliminar(candidataId: Long): String? = try {
        repository.eliminar(candidataId)
        null
    } catch (_: Exception) {
        "No se pudo eliminar la candidata"
    }

    private fun <T> Flow<T>.mantener(inicial: T): StateFlow<T> =
        stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), inicial)
}
