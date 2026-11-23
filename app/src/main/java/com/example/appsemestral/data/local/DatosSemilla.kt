package com.example.appsemestral.data.local

import com.example.appsemestral.di.AppContainer
import com.example.appsemestral.model.Criterio

object DatosSemilla {
    val criterios = listOf(
        Criterio(1, "Experiencia técnica", 30),
        Criterio(2, "Liderazgo", 25),
        Criterio(3, "Comunicación", 20),
        Criterio(4, "Certificaciones", 15),
        Criterio(5, "Ajuste cultural", 10)
    )

    suspend fun poblarSiVacia(container: AppContainer) {
        // La matriz de evaluación sigue siendo local
        container.evaluacionRepository.asegurarCriterios(criterios)
        // Los postulantes vienen del microservicio
        runCatching { container.candidataRepository.refrescar() }
    }
}
