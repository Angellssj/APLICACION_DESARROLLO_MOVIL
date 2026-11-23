package com.example.appsemestral.ui.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.appsemestral.data.repository.CandidataRepository
import com.example.appsemestral.data.repository.EvaluacionRepository
import com.example.appsemestral.model.Candidata
import com.example.appsemestral.model.Criterio
import com.example.appsemestral.navigation.Rutas
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class EvaluacionViewModel(
    savedStateHandle: SavedStateHandle,
    private val candidataRepository: CandidataRepository,
    private val evaluacionRepository: EvaluacionRepository
) : ViewModel() {

    private val candidataId: Long = checkNotNull(savedStateHandle.get<Long>(Rutas.ARG_ID))

    val candidata: StateFlow<Candidata?> = candidataRepository.observarPorId(candidataId)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), null)

    val criterios: StateFlow<List<Criterio>> = evaluacionRepository.observarCriterios()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    /** criterioId → puntaje 1..5 elegido en pantalla. */
    private val _puntajes = MutableStateFlow<Map<Long, Int>>(emptyMap())
    val puntajes: StateFlow<Map<Long, Int>> = _puntajes.asStateFlow()

    private val _guardado = MutableStateFlow(false)
    val guardado: StateFlow<Boolean> = _guardado.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    fun puntuar(criterioId: Long, puntaje: Int) = _puntajes.update { it + (criterioId to puntaje) }

    fun guardar(evaluadora: String) {
        val elegidos = _puntajes.value
        if (elegidos.isEmpty()) {
            _error.value = "Asigna al menos un puntaje"
            return
        }
        viewModelScope.launch {
            try {
                evaluacionRepository.registrar(candidataId, elegidos, evaluadora)
                // El estado se calculó en Room; se envía al microservicio (PATCH /estado).
                // Si el servidor no responde, la evaluación igual queda guardada.
                runCatching { candidataRepository.sincronizarEstado(candidataId) }
                _guardado.value = true
            } catch (e: Exception) {
                _error.value = e.message ?: "No se pudo guardar la evaluación"
            }
        }
    }

    fun errorLeido() { _error.value = null }
}
