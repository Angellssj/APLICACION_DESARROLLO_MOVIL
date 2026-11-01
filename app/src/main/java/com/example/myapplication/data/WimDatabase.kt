package com.example.myapplication.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.myapplication.model.Candidata
import com.example.myapplication.model.Evaluacion

@Database(entities = [Candidata::class, Evaluacion::class], version = 1, exportSchema = false)
abstract class WimDatabase : RoomDatabase() {
    abstract fun dao(): WimDao

    companion object {
        @Volatile private var instancia: WimDatabase? = null

        fun obtener(context: Context): WimDatabase = instancia ?: synchronized(this) {
            instancia ?: Room.databaseBuilder(
                context.applicationContext,
                WimDatabase::class.java,
                "wim100.db"
            ).build().also { instancia = it }
        }
    }
}
