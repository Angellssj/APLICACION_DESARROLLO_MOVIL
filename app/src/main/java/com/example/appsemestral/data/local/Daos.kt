package com.example.appsemestral.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Upsert
import com.example.appsemestral.model.Candidata
import com.example.appsemestral.model.Criterio
import com.example.appsemestral.model.EstadoCandidatura
import com.example.appsemestral.model.Evaluacion
import com.example.appsemestral.model.RegistroHistorial
import kotlinx.coroutines.flow.Flow

@Dao
interface CandidataDao {
    @Query("SELECT * FROM candidatas ORDER BY fechaRegistro DESC")
    fun observarTodas(): Flow<List<Candidata>>

    @Query("SELECT * FROM candidatas WHERE id = :id")
    fun observarPorId(id: Long): Flow<Candidata?>

    @Query("SELECT * FROM candidatas WHERE id = :id")
    suspend fun obtenerPorId(id: Long): Candidata?

    @Query("SELECT COUNT(*) FROM candidatas")
    suspend fun contar(): Int

    // @Upsert y no REPLACE: REPLACE borraría la fila y, por el CASCADE, sus evaluaciones.
    @Upsert
    suspend fun upsert(candidata: Candidata)

    @Upsert
    suspend fun upsertTodas(candidatas: List<Candidata>)

    @Query("SELECT * FROM candidatas")
    suspend fun obtenerTodas(): List<Candidata>

    @Query("DELETE FROM candidatas WHERE id = :id")
    suspend fun eliminar(id: Long)

    /** Borra las copias locales que ya no existen en el servidor. */
    @Query("DELETE FROM candidatas WHERE id NOT IN (:ids)")
    suspend fun eliminarNoIncluidas(ids: List<Long>)

    @Query("UPDATE candidatas SET estado = :estado WHERE id = :id")
    suspend fun actualizarEstado(id: Long, estado: EstadoCandidatura)
}

@Dao
interface CriterioDao {
    @Query("SELECT * FROM criterios ORDER BY id")
    fun observarTodos(): Flow<List<Criterio>>

    @Query("SELECT * FROM criterios ORDER BY id")
    suspend fun obtenerTodos(): List<Criterio>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertarTodos(criterios: List<Criterio>)
}

@Dao
interface EvaluacionDao {
    @Query("SELECT * FROM evaluaciones WHERE candidataId = :candidataId")
    fun observarPorCandidata(candidataId: Long): Flow<List<Evaluacion>>

    @Query("SELECT * FROM evaluaciones WHERE candidataId = :candidataId")
    suspend fun obtenerPorCandidata(candidataId: Long): List<Evaluacion>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun guardarTodas(evaluaciones: List<Evaluacion>)
}

@Dao
interface HistorialDao {
    @Insert
    suspend fun insertar(registro: RegistroHistorial)

    @Query("SELECT * FROM historial ORDER BY fecha DESC")
    fun observarTodos(): Flow<List<RegistroHistorial>>
}
