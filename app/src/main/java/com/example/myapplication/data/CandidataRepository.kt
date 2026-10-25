package com.example.myapplication.data

import android.content.Context
import com.example.myapplication.model.Candidata
import org.json.JSONArray
import org.json.JSONObject

/**
 * Persistencia local sencilla para la versión inicial.
 * En HU-09 se podrá migrar a Room sin cambiar las pantallas.
 */
class CandidataRepository(context: Context) {
    private val preferences = context.getSharedPreferences("wim100_local", Context.MODE_PRIVATE)

    fun listar(): List<Candidata> = runCatching {
        val json = JSONArray(preferences.getString("candidatas", "[]") ?: "[]")
        (0 until json.length()).map { indice ->
            val item = json.getJSONObject(indice)
            Candidata(
                id = item.getLong("id"),
                nombre = item.getString("nombre"),
                correo = item.getString("correo"),
                area = item.getString("area"),
                experiencia = item.getInt("experiencia"),
                cvUri = item.optString("cvUri").ifBlank { null }
            )
        }
    }.getOrDefault(emptyList())

    fun registrar(nombre: String, correo: String, area: String, experiencia: Int, cvUri: String?): Boolean {
        val actuales = listar()
        if (actuales.any { it.correo.equals(correo, ignoreCase = true) }) return false
        val nueva = Candidata(
            id = (actuales.maxOfOrNull { it.id } ?: 0L) + 1,
            nombre = nombre,
            correo = correo,
            area = area,
            experiencia = experiencia,
            cvUri = cvUri
        )
        val arreglo = JSONArray()
        (actuales + nueva).forEach { candidata ->
            arreglo.put(JSONObject().apply {
                put("id", candidata.id)
                put("nombre", candidata.nombre)
                put("correo", candidata.correo)
                put("area", candidata.area)
                put("experiencia", candidata.experiencia)
                put("cvUri", candidata.cvUri ?: "")
            })
        }
        // commit() comprueba si el almacenamiento efectivamente se escribió.
        check(preferences.edit().putString("candidatas", arreglo.toString()).commit()) {
            "No fue posible guardar los datos locales"
        }
        return true
    }
}
