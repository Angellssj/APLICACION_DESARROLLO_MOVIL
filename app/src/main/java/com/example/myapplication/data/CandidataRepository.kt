package com.example.myapplication.data

import android.content.Context
import com.example.myapplication.model.Candidata
import com.example.myapplication.model.Evaluacion
import kotlinx.coroutines.flow.Flow
import org.json.JSONArray

/**
 * Acceso a los datos locales (Room). Las pantallas no cambian si se modifica el almacenamiento.
 * También importa una sola vez los registros que la versión 0.1.0 guardó en SharedPreferences.
 */
class CandidataRepository(context: Context) {
    private val dao = WimDatabase.obtener(context).dao()
    private val preferences = context.getSharedPreferences(PREFERENCIAS, Context.MODE_PRIVATE)

    val candidatas: Flow<List<Candidata>> = dao.observarCandidatas()
    val evaluaciones: Flow<List<Evaluacion>> = dao.observarEvaluaciones()

    /** Devuelve false si ya existe una candidata con ese correo. */
    suspend fun registrar(candidata: Candidata): Boolean = dao.insertarCandidata(candidata) != -1L

    suspend fun eliminar(id: Long) = dao.eliminarCandidata(id)

    suspend fun evaluar(evaluacion: Evaluacion) {
        dao.insertarEvaluacion(evaluacion)
    }

    /** Pasa los datos de la versión 0.1.0 a Room y recuerda que ya lo hizo. */
    suspend fun importarDatosAnteriores() {
        if (preferences.getBoolean(IMPORTADO, false)) return
        val anteriores = runCatching {
            val json = JSONArray(preferences.getString("candidatas", "[]") ?: "[]")
            (0 until json.length()).map { indice ->
                val item = json.getJSONObject(indice)
                Candidata(
                    id = item.getLong("id"),
                    nombre = item.getString("nombre"),
                    correo = item.getString("correo").lowercase(),
                    area = item.getString("area"),
                    experiencia = item.getInt("experiencia"),
                    cvUri = item.optString("cvUri").ifBlank { null }
                )
            }
        }.getOrDefault(emptyList())
        anteriores.forEach { dao.insertarCandidata(it) }
        preferences.edit().putBoolean(IMPORTADO, true).apply()
    }

    private companion object {
        const val PREFERENCIAS = "wim100_local"
        const val IMPORTADO = "importado_a_room"
    }
}
