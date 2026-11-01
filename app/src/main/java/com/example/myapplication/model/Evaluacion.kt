package com.example.myapplication.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

/** Criterios de evaluación y escala de puntaje (1 a 10). */
object Criterios {
    val nombres = listOf(
        "Competencia técnica",
        "Comunicación",
        "Trabajo en equipo",
        "Resolución de problemas"
    )
    const val MINIMO = 1
    const val MAXIMO = 10
}

/** Evaluación de una candidata; se borra junto con ella. */
@Entity(
    tableName = "evaluaciones",
    foreignKeys = [
        ForeignKey(
            entity = Candidata::class,
            parentColumns = ["id"],
            childColumns = ["candidataId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("candidataId")]
)
data class Evaluacion(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val candidataId: Long,
    val evaluadora: String,
    val tecnica: Int,
    val comunicacion: Int,
    val trabajoEquipo: Int,
    val resolucion: Int,
    val comentario: String = "",
    val fecha: Long
) {
    val puntajes: List<Int> get() = listOf(tecnica, comunicacion, trabajoEquipo, resolucion)
    val promedio: Double get() = puntajes.average()
}
