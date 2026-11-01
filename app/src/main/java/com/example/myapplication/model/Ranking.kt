package com.example.myapplication.model

/** Candidata con el consolidado de sus evaluaciones; `promedio` es null si aún no tiene ninguna. */
data class ResumenCandidata(
    val candidata: Candidata,
    val cantidadEvaluaciones: Int,
    val promedio: Double?
)

/** Regla pura del ranking: mejor promedio primero; sin evaluar al final, por nombre. */
object Ranking {
    fun construir(candidatas: List<Candidata>, evaluaciones: List<Evaluacion>): List<ResumenCandidata> {
        val porCandidata = evaluaciones.groupBy { it.candidataId }
        return candidatas
            .map { candidata ->
                val propias = porCandidata[candidata.id].orEmpty()
                ResumenCandidata(
                    candidata = candidata,
                    cantidadEvaluaciones = propias.size,
                    promedio = propias.takeIf { it.isNotEmpty() }?.map { it.promedio }?.average()
                )
            }
            .sortedWith(
                compareByDescending<ResumenCandidata> { it.promedio != null }
                    .thenByDescending { it.promedio ?: 0.0 }
                    .thenBy { it.candidata.nombre.lowercase() }
            )
    }
}
