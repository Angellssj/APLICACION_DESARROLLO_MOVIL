package com.example.appsemestral.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.appsemestral.data.repository.CandidataRepository
import com.example.appsemestral.data.repository.EvaluacionRepository
import com.example.appsemestral.model.FilaRanking
import com.example.appsemestral.model.Ranking
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn

/** Ranking calculado en vivo desde Room: se actualiza solo al evaluar o resolver. */
class RankingViewModel(
    candidataRepository: CandidataRepository,
    evaluacionRepository: EvaluacionRepository
) : ViewModel() {

    val filas: StateFlow<List<FilaRanking>> = combine(
        candidataRepository.observarTodas(),
        evaluacionRepository.observarCriterios(),
        evaluacionRepository.observarTodasLasEvaluaciones()
    ) { candidatas, criterios, evaluaciones ->
        Ranking.calcular(candidatas, criterios, evaluaciones)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())
}
