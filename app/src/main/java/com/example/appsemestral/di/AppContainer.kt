package com.example.appsemestral.di

import android.content.Context
import com.example.appsemestral.data.local.WimDatabase
import com.example.appsemestral.data.remote.RetrofitClient
import com.example.appsemestral.data.repository.AuthRepository
import com.example.appsemestral.data.repository.CandidataRepository
import com.example.appsemestral.data.repository.EvaluacionRepository
import com.example.appsemestral.data.repository.HistorialRepository
import com.example.appsemestral.data.repository.RandomUserRepository

/** Inyección de dependencias manual: crea una sola instancia de cada repositorio. */
class AppContainer(context: Context) {
    private val db = WimDatabase.obtener(context)

    val historialRepository = HistorialRepository(db.historialDao())
    val candidataRepository = CandidataRepository(db.candidataDao(), RetrofitClient.wimApi, historialRepository)
    val evaluacionRepository = EvaluacionRepository(db, historialRepository)
    val authRepository = AuthRepository(RetrofitClient.wimApi)
    val randomUserRepository = RandomUserRepository(RetrofitClient.randomUserApi)
}
