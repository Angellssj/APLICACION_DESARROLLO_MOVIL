package com.example.appsemestral.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.appsemestral.model.Candidata
import com.example.appsemestral.model.Criterio
import com.example.appsemestral.model.Evaluacion
import com.example.appsemestral.model.RegistroHistorial

@Database(
    entities = [Candidata::class, Criterio::class, Evaluacion::class, RegistroHistorial::class],
    version = 1,
    exportSchema = false
)
abstract class WimDatabase : RoomDatabase() {
    abstract fun candidataDao(): CandidataDao
    abstract fun criterioDao(): CriterioDao
    abstract fun evaluacionDao(): EvaluacionDao
    abstract fun historialDao(): HistorialDao

    companion object {
        @Volatile
        private var instancia: WimDatabase? = null

        fun obtener(context: Context): WimDatabase = instancia ?: synchronized(this) {
            instancia ?: Room.databaseBuilder(context.applicationContext, WimDatabase::class.java, "wim100.db")
                .fallbackToDestructiveMigration(true)
                .build()
                .also { instancia = it }
        }
    }
}
