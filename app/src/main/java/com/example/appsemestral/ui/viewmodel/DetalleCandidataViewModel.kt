package com.example.appsemestral.ui.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.appsemestral.data.remote.mensajeAmigable
import com.example.appsemestral.data.repository.CandidataRepository
import com.example.appsemestral.data.repository.EvaluacionRepository
import com.example.appsemestral.data.repository.HistorialRepository
import com.example.appsemestral.model.Candidata
import com.example.appsemestral.model.Criterio
import com.example.appsemestral.model.EstadoCandidatura
import com.example.appsemestral.model.Evaluacion
import com.example.appsemestral.navigation.Rutas
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class DetalleCandidataViewModel(
    savedStateHandle: SavedStateHandle,
    private val candidataRepository: CandidataRepository,
    evaluacionRepository: EvaluacionRepository,
    @Suppress("unused") private val historialRepository: HistorialRepository
) : ViewModel() {

    private val candidataId: Long = checkNotNull(savedStateHandle.get<Long>(Rutas.ARG_ID))

    val candidata: StateFlow<Candidata?> = candidataRepository.observarPorId(candidataId)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), null)

    val criterios: StateFlow<List<Criterio>> = evaluacionRepository.observarCriterios()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    val evaluaciones: StateFlow<List<Evaluacion>> = evaluacionRepository.observarEvaluaciones(candidataId)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    private val _eliminada = MutableStateFlow(false)
    val eliminada: StateFlow<Boolean> = _eliminada.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    fun eliminar(usuario: String) {
        viewModelScope.launch {
            try {
                candidataRepository.eliminar(candidataId, usuario)
                _eliminada.value = true
            } catch (e: Exception) {
                _error.value = e.mensajeAmigable()
            }
        }
    }

    fun resolver(estado: EstadoCandidatura, usuario: String) {
        viewModelScope.launch {
            try {
                candidataRepository.resolver(candidataId, estado, usuario)
            } catch (e: Exception) {
                _error.value = e.mensajeAmigable()
            }
        }
    }

    fun errorLeido() { _error.value = null }
}
