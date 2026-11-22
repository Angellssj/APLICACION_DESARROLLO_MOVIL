package com.example.appsemestral.navigation

object Rutas {
    const val ARG_ID = "id"

    const val LOGIN = "login"
    const val LISTA = "candidatas"
    const val NUEVA = "nueva-candidata"
    const val EDITAR = "editar-candidata/{$ARG_ID}"
    const val DETALLE = "candidata/{$ARG_ID}"
    const val EVALUAR = "evaluar/{$ARG_ID}"

    fun editar(id: Long) = "editar-candidata/$id"
    fun detalle(id: Long) = "candidata/$id"
    fun evaluar(id: Long) = "evaluar/$id"
}
