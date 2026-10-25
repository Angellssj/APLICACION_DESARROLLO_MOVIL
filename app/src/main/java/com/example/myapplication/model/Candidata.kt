package com.example.myapplication.model

/** Datos ficticios del prototipo WIM 100. */
data class Candidata(
    val id: Long,
    val nombre: String,
    val correo: String,
    val area: String,
    val experiencia: Int,
    val cvUri: String? = null
)
