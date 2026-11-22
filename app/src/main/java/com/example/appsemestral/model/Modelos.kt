package com.example.appsemestral.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

enum class EstadoCandidatura(val etiqueta: String) {
    POSTULADA("Postulada"),
    EN_EVALUACION("En evaluación"),
    EVALUADA("Evaluada"),
    SELECCIONADA("Seleccionada"),
    NO_SELECCIONADA("No seleccionada");

    val estaResuelta: Boolean get() = this == SELECCIONADA || this == NO_SELECCIONADA
}

enum class Rol(val etiqueta: String) {
    EVALUADORA("Evaluadora"),
    DIRECCION("Dirección")
}

data class Sesion(
    val nombre: String,
    val rol: Rol,
    val id: Long = 0,
    val usuario: String = ""
)

/** Copia local de un postulante del servidor. */
@Entity(tableName = "candidatas")
data class Candidata(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val nombre: String,
    val correo: String,
    val telefono: String,
    val area: String,
    val aniosExperiencia: Int,
    val resumenExperiencia: String,
    val linkedinUrl: String,
    val cvUri: String? = null,
    val cvNombre: String? = null,
    val estado: EstadoCandidatura = EstadoCandidatura.POSTULADA,
    val fechaRegistro: Long = System.currentTimeMillis(),
    val registradaPor: String
) {
    companion object {
        val AREAS = listOf(
            "Seguridad ofensiva (Red Team)",
            "Seguridad defensiva (Blue Team)",
            "Gobierno, riesgo y cumplimiento",
            "Forense y respuesta a incidentes",
            "Seguridad en la nube",
            "Inteligencia de amenazas"
        )
    }
}

@Entity(tableName = "criterios")
data class Criterio(
    @PrimaryKey val id: Long,
    val nombre: String,
    /** Peso porcentual; todos los criterios suman 100. */
    val peso: Int
)

@Entity(
    tableName = "evaluaciones",
    foreignKeys = [ForeignKey(
        entity = Candidata::class,
        parentColumns = ["id"],
        childColumns = ["candidataId"],
        onDelete = ForeignKey.CASCADE
    )],
    indices = [Index("candidataId"), Index(value = ["candidataId", "criterioId"], unique = true)]
)
data class Evaluacion(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val candidataId: Long,
    val criterioId: Long,
    /** Puntaje de 1 a 5. */
    val puntaje: Int,
    val evaluadora: String,
    val fecha: Long = System.currentTimeMillis()
)

@Entity(tableName = "historial")
data class RegistroHistorial(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val candidataId: Long?,
    val accion: String,
    val detalle: String,
    val usuario: String,
    val fecha: Long = System.currentTimeMillis()
)

object Acciones {
    const val REGISTRO = "Registro de candidatura"
    const val EDICION = "Edición de candidatura"
    const val ELIMINACION = "Eliminación de candidatura"
    const val EVALUACION = "Evaluación"
    const val RESOLUCION = "Resolución"
    const val INICIO_SESION = "Inicio de sesión"
    const val CIERRE_SESION = "Cierre de sesión"
}

object CalculadoraPuntaje {
    /** Puntaje ponderado de 0 a 100 a partir de puntajes de 1 a 5. */
    fun total(criterios: List<Criterio>, evaluaciones: List<Evaluacion>): Double {
        val porCriterio = evaluaciones.associate { it.criterioId to it.puntaje }
        val pesoTotal = criterios.sumOf { it.peso }
        if (pesoTotal == 0) return 0.0
        val suma = criterios.sumOf { (porCriterio[it.id] ?: 0) * it.peso }
        return suma * 100.0 / (5 * pesoTotal)
    }

    fun estaCompleta(criterios: List<Criterio>, evaluaciones: List<Evaluacion>): Boolean =
        criterios.isNotEmpty() && criterios.all { c -> evaluaciones.any { it.criterioId == c.id } }
}
