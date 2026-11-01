package com.example.myapplication.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.myapplication.model.Candidata
import com.example.myapplication.model.Evaluacion
import kotlinx.coroutines.flow.Flow

@Dao
interface WimDao {
    @Query("SELECT * FROM candidatas ORDER BY nombre COLLATE NOCASE")
    fun observarCandidatas(): Flow<List<Candidata>>

    @Query("SELECT * FROM evaluaciones ORDER BY fecha DESC")
    fun observarEvaluaciones(): Flow<List<Evaluacion>>

    /** Devuelve -1 si el correo ya existe (índice único). */
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertarCandidata(candidata: Candidata): Long

    @Insert
    suspend fun insertarEvaluacion(evaluacion: Evaluacion): Long

    @Query("DELETE FROM candidatas WHERE id = :id")
    suspend fun eliminarCandidata(id: Long)
}
