package com.example.appsemestral.ui.viewmodel

import android.app.Application
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.createSavedStateHandle
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.appsemestral.WimApplication

object AppViewModelProvider {

    private fun CreationExtras.app(): WimApplication =
        this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as WimApplication

    val Factory = viewModelFactory {
        initializer {
            SesionViewModel(app().container.authRepository, app().container.historialRepository)
        }
        initializer {
            CandidatasViewModel(app().container.candidataRepository)
        }
        initializer {
            CandidataFormViewModel(
                createSavedStateHandle(),
                app().container.candidataRepository,
                app().container.randomUserRepository
            )
        }
        initializer {
            DetalleCandidataViewModel(
                createSavedStateHandle(),
                app().container.candidataRepository,
                app().container.evaluacionRepository,
                app().container.historialRepository
            )
        }
        initializer {
            EvaluacionViewModel(
                createSavedStateHandle(),
                app().container.candidataRepository,
                app().container.evaluacionRepository
            )
        }
    }
}
