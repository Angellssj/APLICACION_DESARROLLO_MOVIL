package com.example.appsemestral.data.repository

import com.example.appsemestral.data.local.CandidataDao
import com.example.appsemestral.data.remote.EstadoRequest
import com.example.appsemestral.data.remote.WimApi
import com.example.appsemestral.data.remote.aDto
import com.example.appsemestral.data.remote.aEntidad
import com.example.appsemestral.model.Acciones
import com.example.appsemestral.model.Candidata
import com.example.appsemestral.model.EstadoCandidatura
import kotlinx.coroutines.flow.Flow
import retrofit2.HttpException

/**
 * Postulantes: el microservicio es la fuente oficial; Room guarda una copia local
 * que observan las pantallas y que necesitan las evaluaciones (llave foránea).
 */
class CandidataRepository(
    private val dao: CandidataDao,
    private val api: WimApi,
    private val historial: HistorialRepository
) {
    fun observarTodas(): Flow<List<Candidata>> = dao.observarTodas()

    fun observarPorId(id: Long): Flow<Candidata?> = dao.observarPorId(id)

    suspend fun obtener(id: Long): Candidata? = dao.obtenerPorId(id)

    suspend fun contar(): Int = dao.contar()

    /** READ: trae la lista del servidor y actualiza la copia local. */
    suspend fun refrescar() {
        val remotas = api.listar()
        val cvLocales = dao.obtenerTodas().associate { it.id to it.cvUri }
        dao.eliminarNoIncluidas(remotas.mapNotNull { it.id })
        dao.upsertTodas(remotas.map { it.aEntidad(cvLocales[it.id]) })
    }

    /** CREATE: el servidor asigna el id; luego se guarda la copia local. */
    suspend fun registrar(candidata: Candidata): Long {
        val creada = api.crear(candidata.aDto())
        val id = checkNotNull(creada.id) { "El servidor no devolvió el id" }
        dao.upsert(creada.aEntidad(candidata.cvUri))
        historial.registrar(id, Acciones.REGISTRO, "Se registró a ${creada.nombre} · ${creada.area}", candidata.registradaPor)
        return id
    }

    /** UPDATE */
    suspend fun actualizar(candidata: Candidata, usuario: String) {
        val actualizada = api.actualizar(candidata.id, candidata.aDto())
        dao.upsert(actualizada.aEntidad(candidata.cvUri))
        historial.registrar(candidata.id, Acciones.EDICION, "Se editaron los datos de ${actualizada.nombre}", usuario)
    }

    /** DELETE: si el servidor dice 404 ya no existe allá, igual se borra la copia local. */
    suspend fun eliminar(id: Long, usuario: String) {
        val nombre = dao.obtenerPorId(id)?.nombre ?: "#$id"
        val respuesta = api.eliminar(id)
        if (!respuesta.isSuccessful && respuesta.code() != 404) throw HttpException(respuesta)
        dao.eliminar(id)
        historial.registrar(null, Acciones.ELIMINACION, "Se eliminó a $nombre", usuario)
    }

    /** Envía al servidor el estado que calculó la app después de evaluar. */
    suspend fun sincronizarEstado(id: Long) {
        val local = dao.obtenerPorId(id) ?: return
        api.cambiarEstado(id, EstadoRequest(local.estado.name))
    }

    /** Dirección cierra la candidatura como seleccionada o no seleccionada. */
    suspend fun resolver(id: Long, estado: EstadoCandidatura, usuario: String) {
        require(estado.estaResuelta) { "Solo se puede resolver como Seleccionada o No seleccionada" }
        val actual = dao.obtenerPorId(id) ?: return
        api.cambiarEstado(id, EstadoRequest(estado.name))
        dao.actualizarEstado(id, estado)
        historial.registrar(id, Acciones.RESOLUCION, "${actual.nombre}: ${actual.estado.etiqueta} → ${estado.etiqueta}", usuario)
    }
}
