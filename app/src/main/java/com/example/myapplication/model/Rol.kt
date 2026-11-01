package com.example.myapplication.model

/**
 * Perfiles de la sesión de demostración.
 * Evaluadora puntúa; Dirección consulta el ranking y puede eliminar registros.
 */
enum class Rol(val etiqueta: String) {
    EVALUADORA("Evaluadora"),
    DIRECCION("Dirección");

    val puedeEvaluar: Boolean get() = this == EVALUADORA
    val puedeEliminar: Boolean get() = this == DIRECCION
}
