package com.example.appsemestral.data.repository

import com.example.appsemestral.data.local.HistorialDao
import com.example.appsemestral.model.RegistroHistorial
import kotlinx.coroutines.flow.Flow

open class HistorialRepository(private val dao: HistorialDao) {

    fun observarTodos(): Flow<List<RegistroHistorial>> = dao.observarTodos()

    open suspend fun registrar(candidataId: Long?, accion: String, detalle: String, usuario: String) {
        dao.insertar(RegistroHistorial(candidataId = candidataId, accion = accion, detalle = detalle, usuario = usuario))
    }
}
