package com.example.appsemestral.ui.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.appsemestral.data.remote.mensajeAmigable
import com.example.appsemestral.data.repository.CandidataRepository
import com.example.appsemestral.data.repository.RandomUserRepository
import com.example.appsemestral.model.Candidata
import com.example.appsemestral.model.EstadoCandidatura
import com.example.appsemestral.navigation.Rutas
import com.example.appsemestral.validation.CandidataValidator
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import retrofit2.HttpException

data class CandidataFormUiState(
    val nombre: CampoState = CampoState(),
    val correo: CampoState = CampoState(),
    val telefono: CampoState = CampoState(),
    val anios: CampoState = CampoState(),
    val resumen: CampoState = CampoState(),
    val linkedin: CampoState = CampoState(),
    val area: String? = null,
    val areaError: String? = null,
    val cvUri: String? = null,
    val cvNombre: String? = null,
    val guardando: Boolean = false,
    val guardado: Boolean = false,
    val mensaje: String? = null,
    val intentosFallidos: Int = 0
)

class CandidataFormViewModel(
    savedStateHandle: SavedStateHandle,
    private val repository: CandidataRepository,
    private val randomUser: RandomUserRepository
) : ViewModel() {

    /** null = registrar; con valor = editar esa candidata. */
    private val idEdicion: Long? = savedStateHandle.get<Long>(Rutas.ARG_ID)
    private var original: Candidata? = null
    val esEdicion: Boolean get() = idEdicion != null

    private val _ui = MutableStateFlow(CandidataFormUiState())
    val uiState: StateFlow<CandidataFormUiState> = _ui.asStateFlow()

    init {
        idEdicion?.let { id ->
            viewModelScope.launch {
                val c = repository.obtener(id) ?: return@launch
                original = c
                onNombre(c.nombre); onCorreo(c.correo); onTelefono(c.telefono)
                onAnios(c.aniosExperiencia.toString()); onResumen(c.resumenExperiencia)
                onLinkedin(c.linkedinUrl); onArea(c.area)
                _ui.update { it.copy(cvUri = c.cvUri, cvNombre = c.cvNombre) }
            }
        }
    }

    /** API EXTERNA: completa el nombre con uno ficticio de randomuser.me. */
    fun generarNombreFicticio() {
        viewModelScope.launch {
            try {
                onNombre(randomUser.nombreFicticio())
            } catch (e: Exception) {
                _ui.update { it.copy(mensaje = "API externa no disponible: ${e.mensajeAmigable()}") }
            }
        }
    }

    fun onNombre(v: String) = _ui.update { it.copy(nombre = it.nombre.con(v, CandidataValidator.nombre(v))) }
    fun onCorreo(v: String) = _ui.update { it.copy(correo = it.correo.con(v, CandidataValidator.correo(v))) }
    fun onTelefono(v: String) = _ui.update { it.copy(telefono = it.telefono.con(v, CandidataValidator.telefono(v))) }
    fun onAnios(v: String) = _ui.update { it.copy(anios = it.anios.con(v, CandidataValidator.anios(v))) }
    fun onResumen(v: String) = _ui.update { it.copy(resumen = it.resumen.con(v, CandidataValidator.resumen(v))) }
    fun onLinkedin(v: String) = _ui.update { it.copy(linkedin = it.linkedin.con(v, CandidataValidator.linkedin(v))) }
    fun onArea(v: String) = _ui.update { it.copy(area = v, areaError = null) }
    fun onCvNombre(v: String) = _ui.update { it.copy(cvNombre = v.ifBlank { null }) }
    fun mensajeLeido() = _ui.update { it.copy(mensaje = null) }

    fun validarTodo(): Boolean {
        val s = _ui.value
        val n = CandidataValidator.nombre(s.nombre.valor)
        val c = CandidataValidator.correo(s.correo.valor)
        val t = CandidataValidator.telefono(s.telefono.valor)
        val a = CandidataValidator.anios(s.anios.valor)
        val r = CandidataValidator.resumen(s.resumen.valor)
        val l = CandidataValidator.linkedin(s.linkedin.valor)
        val areaOk = s.area != null
        val ok = n.esValido && c.esValido && t.esValido && a.esValido && r.esValido && l.esValido && areaOk
        _ui.update {
            it.copy(
                nombre = it.nombre.con(s.nombre.valor, n),
                correo = it.correo.con(s.correo.valor, c),
                telefono = it.telefono.con(s.telefono.valor, t),
                anios = it.anios.con(s.anios.valor, a),
                resumen = it.resumen.con(s.resumen.valor, r),
                linkedin = it.linkedin.con(s.linkedin.valor, l),
                areaError = if (areaOk) null else "Selecciona un área",
                intentosFallidos = if (ok) it.intentosFallidos else it.intentosFallidos + 1
            )
        }
        return ok
    }

    fun guardar(usuario: String) {
        if (_ui.value.guardando || !validarTodo()) return
        val s = _ui.value
        _ui.update { it.copy(guardando = true) }
        viewModelScope.launch {
            try {
                val datos = Candidata(
                    id = original?.id ?: 0,
                    nombre = s.nombre.valor.trim().replace(Regex("\\s+"), " "),
                    correo = s.correo.valor.trim().lowercase(),
                    telefono = CandidataValidator.formatearTelefono(s.telefono.valor),
                    area = s.area.orEmpty(),
                    aniosExperiencia = s.anios.valor.trim().toInt(),
                    resumenExperiencia = s.resumen.valor.trim(),
                    linkedinUrl = s.linkedin.valor.trim(),
                    cvUri = s.cvUri,
                    cvNombre = s.cvNombre,
                    estado = original?.estado ?: EstadoCandidatura.POSTULADA,
                    fechaRegistro = original?.fechaRegistro ?: System.currentTimeMillis(),
                    registradaPor = original?.registradaPor ?: usuario
                )
                if (original == null) repository.registrar(datos) else repository.actualizar(datos, usuario)
                _ui.update { it.copy(guardando = false, guardado = true) }
            } catch (e: HttpException) {
                if (e.code() == 409) {
                    _ui.update {
                        it.copy(
                            guardando = false,
                            correo = it.correo.copy(error = "Ya existe una candidatura con este correo"),
                            intentosFallidos = it.intentosFallidos + 1
                        )
                    }
                } else {
                    _ui.update { it.copy(guardando = false, mensaje = e.mensajeAmigable()) }
                }
            } catch (e: Exception) {
                _ui.update { it.copy(guardando = false, mensaje = "No se pudo guardar: ${e.mensajeAmigable()}") }
            }
        }
    }
}
