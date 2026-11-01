package com.example.myapplication.validation

import com.example.myapplication.model.Criterios

/** Reglas puras, sin dependencias de Android: fáciles de probar y modificar. */
object CandidataValidator {
    fun validar(
        nombre: String,
        correo: String,
        area: String,
        experiencia: String,
        telefono: String = ""
    ): Map<String, String> {
        val errores = mutableMapOf<String, String>()
        if (nombre.trim().length < 3 || nombre.trim().split(Regex("\\s+")).size < 2) {
            errores["nombre"] = "Escribe nombre y apellido (mínimo 3 caracteres)"
        }
        if (!Regex("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$").matches(correo.trim())) {
            errores["correo"] = "Ingresa un correo válido"
        }
        if (area.trim().isEmpty()) {
            errores["area"] = "El área es obligatoria"
        }
        val anios = experiencia.toIntOrNull()
        if (anios == null || anios !in 0..50) {
            errores["experiencia"] = "Ingresa años entre 0 y 50"
        }
        if (telefono.isNotBlank() && !telefonoValido(telefono)) {
            errores["telefono"] = "Teléfono inválido (8 a 12 dígitos, puede llevar +)"
        }
        return errores
    }

    /** El teléfono es opcional; acepta espacios, guiones y paréntesis como separadores. */
    fun telefonoValido(telefono: String): Boolean =
        Regex("^\\+?[0-9]{8,12}$").matches(telefono.replace(Regex("[\\s\\-()]"), ""))

    /** Devuelve null si los puntajes son válidos; si no, el mensaje de error. */
    fun validarEvaluacion(puntajes: List<Int>): String? = when {
        puntajes.size != Criterios.nombres.size -> "Faltan criterios por evaluar"
        puntajes.any { it !in Criterios.MINIMO..Criterios.MAXIMO } ->
            "Los puntajes deben estar entre ${Criterios.MINIMO} y ${Criterios.MAXIMO}"
        else -> null
    }
}
