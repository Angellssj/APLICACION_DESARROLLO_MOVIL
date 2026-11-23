package com.example.appsemestral.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.appsemestral.data.remote.mensajeAmigable
import com.example.appsemestral.data.repository.CandidataRepository
import com.example.appsemestral.model.Candidata
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class CandidatasViewModel(private val repository: CandidataRepository) : ViewModel() {

    val candidatas: StateFlow<List<Candidata>> = repository.observarTodas()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    init { refrescar() }

    /** READ remoto: se llama al abrir la pantalla y con el botón "Actualizar". */
    fun refrescar() {
        viewModelScope.launch {
            try {
                repository.refrescar()
                _error.value = null
            } catch (e: Exception) {
                _error.value = e.mensajeAmigable()
            }
        }
    }
}
