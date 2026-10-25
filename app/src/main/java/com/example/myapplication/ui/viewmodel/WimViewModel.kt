package com.example.myapplication.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.example.myapplication.data.CandidataRepository
import com.example.myapplication.model.Candidata
import com.example.myapplication.validation.CandidataValidator
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

/** Estado visible + coordinación de datos; no dibuja interfaces. */
class WimViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = CandidataRepository(application.applicationContext)
    private val _candidatas = MutableStateFlow(repository.listar())
    val candidatas: StateFlow<List<Candidata>> = _candidatas

    /** Devuelve null si se guarda correctamente; si falla, devuelve el mensaje. */
    fun registrar(
        nombre: String,
        correo: String,
        area: String,
        experiencia: String,
        cvUri: String?
    ): String? {
        val errores = CandidataValidator.validar(nombre, correo, area, experiencia)
        if (errores.isNotEmpty()) return errores.values.first()
        return try {
            if (!repository.registrar(
                nombre = nombre.trim(),
                correo = correo.trim().lowercase(),
                area = area.trim(),
                experiencia = experiencia.toInt(),
                cvUri = cvUri
            )) {
                "Ya existe una candidata con ese correo"
            } else {
                _candidatas.value = repository.listar()
                null
            }
        } catch (_: Exception) {
            "No se pudo guardar la candidata en el teléfono"
        }
    }
}
