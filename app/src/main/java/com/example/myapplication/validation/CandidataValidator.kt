package com.example.myapplication.validation

/** Reglas puras, sin dependencias de Android: fáciles de probar y modificar. */
object CandidataValidator {
    fun validar(nombre: String, correo: String, area: String, experiencia: String): Map<String, String> {
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
        return errores
    }
}
