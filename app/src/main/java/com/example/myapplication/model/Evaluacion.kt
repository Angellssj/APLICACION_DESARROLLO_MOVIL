package com.example.myapplication.model

data class Evaluacion(
    val experiencia: Int,
    val conocimientos: Int,
    val entrevista: Int
) {
    init {
        require(
            experiencia in 1..5 &&
                    conocimientos in 1..5 &&
                    entrevista in 1..5
        ) {
            "Los puntajes deben estar entre 1 y 5"
        }
    }

    fun promedio(): Double {
        return (experiencia + conocimientos + entrevista) / 3.0
    }
}