package com.example.appsemestral.validation

sealed class ValidationResult {
    object Valido : ValidationResult()
    data class Invalido(val mensaje: String) : ValidationResult()

    val esValido: Boolean get() = this is Valido

    companion object {
        fun error(mensaje: String): ValidationResult = Invalido(mensaje)
    }
}

object CandidataValidator {
    private val correoRegex = Regex("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")

    fun nombre(v: String): ValidationResult {
        val t = v.trim()
        return when {
            t.isEmpty() -> ValidationResult.error("El nombre es obligatorio")
            t.length < 3 || t.length > 60 -> ValidationResult.error("Entre 3 y 60 caracteres")
            else -> ValidationResult.Valido
        }
    }

    fun correo(v: String): ValidationResult = when {
        v.isBlank() -> ValidationResult.error("El correo es obligatorio")
        !correoRegex.matches(v.trim()) -> ValidationResult.error("Formato de correo inválido")
        else -> ValidationResult.Valido
    }

    fun telefono(v: String): ValidationResult {
        val digitos = v.filter { it.isDigit() }
        return when {
            v.isBlank() -> ValidationResult.error("El teléfono es obligatorio")
            digitos.length != 9 && !(digitos.length == 11 && digitos.startsWith("56")) ->
                ValidationResult.error("Teléfono chileno: 9 dígitos, ej. 9 1111 2222")
            else -> ValidationResult.Valido
        }
    }

    /** Devuelve el formato +56 9 XXXX XXXX. */
    fun formatearTelefono(v: String): String {
        var d = v.filter { it.isDigit() }
        if (d.length == 11 && d.startsWith("56")) d = d.substring(2)
        if (d.length != 9) return v.trim()
        return "+56 ${d[0]} ${d.substring(1, 5)} ${d.substring(5)}"
    }

    fun anios(v: String): ValidationResult {
        val n = v.trim().toIntOrNull()
        return when {
            v.isBlank() -> ValidationResult.error("Indica los años de experiencia")
            n == null -> ValidationResult.error("Debe ser un número")
            n < 0 -> ValidationResult.error("No puede ser negativo")
            n > 50 -> ValidationResult.error("Máximo 50 años")
            else -> ValidationResult.Valido
        }
    }

    fun resumen(v: String): ValidationResult {
        val t = v.trim()
        return when {
            t.isEmpty() -> ValidationResult.error("El resumen es obligatorio")
            t.length < 30 || t.length > 500 -> ValidationResult.error("Entre 30 y 500 caracteres")
            else -> ValidationResult.Valido
        }
    }

    fun linkedin(v: String): ValidationResult = when {
        v.isBlank() -> ValidationResult.error("El enlace al perfil es obligatorio")
        !v.trim().startsWith("https://") -> ValidationResult.error("Debe comenzar con https://")
        // La columna del backend admite 200; los enlaces compartidos traen "?utm_..." y se pasan.
        v.trim().length > 200 -> ValidationResult.error("Máximo 200 caracteres (borra lo que va después de \"?\")")
        else -> ValidationResult.Valido
    }
}
