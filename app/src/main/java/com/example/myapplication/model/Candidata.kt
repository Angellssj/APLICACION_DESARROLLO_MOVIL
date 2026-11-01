package com.example.myapplication.model

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

/** Datos ficticios del prototipo WIM 100. El correo es único (se guarda en minúsculas). */
@Entity(tableName = "candidatas", indices = [Index(value = ["correo"], unique = true)])
data class Candidata(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val nombre: String,
    val correo: String,
    val area: String,
    val experiencia: Int,
    val telefono: String? = null,
    val cvUri: String? = null
)
