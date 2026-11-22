package com.example.appsemestral.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.appsemestral.data.remote.mensajeAmigable
import com.example.appsemestral.data.repository.AuthRepository
import com.example.appsemestral.data.repository.HistorialRepository
import com.example.appsemestral.model.Acciones
import com.example.appsemestral.model.Sesion
import com.example.appsemestral.validation.ValidationResult
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/** Valor de un campo de formulario y su error de validación. */
data class CampoState(val valor: String = "", val error: String? = null) {
    fun con(nuevo: String, resultado: ValidationResult) =
        CampoState(nuevo, (resultado as? ValidationResult.Invalido)?.mensaje)
}

data class LoginUiState(
    val usuario: CampoState = CampoState(),
    val password: CampoState = CampoState(),
    val cargando: Boolean = false,
    val error: String? = null,
    val intentosFallidos: Int = 0
)

class SesionViewModel(
    private val auth: AuthRepository,
    private val historial: HistorialRepository
) : ViewModel() {

    private val _login = MutableStateFlow(LoginUiState())
    val loginState: StateFlow<LoginUiState> = _login.asStateFlow()

    private val _sesion = MutableStateFlow<Sesion?>(null)
    val sesion: StateFlow<Sesion?> = _sesion.asStateFlow()

    fun onUsuario(v: String) = _login.update {
        it.copy(usuario = it.usuario.con(v, validarUsuario(v)), error = null)
    }

    fun onPassword(v: String) = _login.update {
        it.copy(password = it.password.con(v, validarPassword(v)), error = null)
    }

    fun validar(): Boolean {
        val s = _login.value
        val u = validarUsuario(s.usuario.valor)
        val p = validarPassword(s.password.valor)
        val ok = u.esValido && p.esValido
        _login.update {
            it.copy(
                usuario = it.usuario.con(s.usuario.valor, u),
                password = it.password.con(s.password.valor, p),
                intentosFallidos = if (ok) it.intentosFallidos else it.intentosFallidos + 1
            )
        }
        return ok
    }

    /** Llama al backend. Si responde 200 crea la sesión y avisa a la pantalla. */
    fun iniciarSesion(metodo: String, onExito: () -> Unit) {
        val s = _login.value
        if (s.cargando) return
        _login.update { it.copy(cargando = true, error = null) }
        viewModelScope.launch {
            try {
                val nueva = auth.login(s.usuario.valor, s.password.valor)
                _sesion.value = nueva
                _login.value = LoginUiState()
                historial.registrar(null, Acciones.INICIO_SESION, "Ingreso como ${nueva.rol.etiqueta} ($metodo)", nueva.nombre)
                onExito()
            } catch (e: Exception) {
                _login.update {
                    it.copy(cargando = false, error = e.mensajeAmigable(), intentosFallidos = it.intentosFallidos + 1)
                }
            }
        }
    }

    fun cerrarSesion() {
        val actual = _sesion.value ?: return
        _sesion.value = null
        _login.value = LoginUiState()
        viewModelScope.launch {
            historial.registrar(null, Acciones.CIERRE_SESION, "Salida del perfil ${actual.rol.etiqueta}", actual.nombre)
        }
    }

    companion object {
        fun validarUsuario(v: String): ValidationResult =
            if (v.isBlank()) ValidationResult.error("Ingresa tu usuario") else ValidationResult.Valido

        fun validarPassword(v: String): ValidationResult = when {
            v.isBlank() -> ValidationResult.error("Ingresa tu contraseña")
            v.length < 6 -> ValidationResult.error("Mínimo 6 caracteres")
            else -> ValidationResult.Valido
        }
    }
}
