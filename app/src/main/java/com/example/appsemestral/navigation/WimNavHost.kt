package com.example.appsemestral.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.appsemestral.ui.screens.CandidataFormScreen
import com.example.appsemestral.ui.screens.CandidatasScreen
import com.example.appsemestral.ui.screens.DetalleCandidataScreen
import com.example.appsemestral.ui.screens.EvaluacionScreen
import com.example.appsemestral.ui.screens.LoginScreen
import com.example.appsemestral.ui.screens.RankingScreen
import com.example.appsemestral.ui.viewmodel.AppViewModelProvider
import com.example.appsemestral.ui.viewmodel.SesionViewModel

@Composable
fun WimNavHost(mostrarMensaje: (String) -> Unit) {
    val navController = rememberNavController()
    val sesionVm: SesionViewModel = viewModel(factory = AppViewModelProvider.Factory)
    val sesion by sesionVm.sesion.collectAsStateWithLifecycle()
    val argumentoId = listOf(navArgument(Rutas.ARG_ID) { type = NavType.LongType })

    NavHost(navController = navController, startDestination = Rutas.LOGIN) {
        composable(Rutas.LOGIN) {
            LoginScreen(sesionVm) {
                navController.navigate(Rutas.LISTA) { popUpTo(Rutas.LOGIN) { inclusive = true } }
            }
        }
        composable(Rutas.LISTA) {
            sesion?.let { s ->
                CandidatasScreen(
                    viewModel = viewModel(factory = AppViewModelProvider.Factory),
                    sesion = s,
                    onNueva = { navController.navigate(Rutas.NUEVA) },
                    onDetalle = { id -> navController.navigate(Rutas.detalle(id)) },
                    onRanking = { navController.navigate(Rutas.RANKING) },
                    onCerrarSesion = {
                        sesionVm.cerrarSesion()
                        navController.navigate(Rutas.LOGIN) { popUpTo(0) }
                    }
                )
            }
        }
        composable(Rutas.NUEVA) {
            sesion?.let { s ->
                CandidataFormScreen(
                    viewModel = viewModel(factory = AppViewModelProvider.Factory),
                    sesion = s,
                    onVolver = { navController.popBackStack() },
                    mostrarMensaje = mostrarMensaje
                )
            }
        }
        composable(Rutas.EDITAR, arguments = argumentoId) {
            sesion?.let { s ->
                CandidataFormScreen(
                    viewModel = viewModel(factory = AppViewModelProvider.Factory),
                    sesion = s,
                    onVolver = { navController.popBackStack() },
                    mostrarMensaje = mostrarMensaje
                )
            }
        }
        composable(Rutas.DETALLE, arguments = argumentoId) {
            sesion?.let { s ->
                DetalleCandidataScreen(
                    viewModel = viewModel(factory = AppViewModelProvider.Factory),
                    sesion = s,
                    onVolver = { navController.popBackStack() },
                    onEditar = { id -> navController.navigate(Rutas.editar(id)) },
                    onEvaluar = { id -> navController.navigate(Rutas.evaluar(id)) },
                    mostrarMensaje = mostrarMensaje
                )
            }
        }
        composable(Rutas.RANKING) {
            RankingScreen(
                viewModel = viewModel(factory = AppViewModelProvider.Factory),
                onVolver = { navController.popBackStack() },
                onDetalle = { id -> navController.navigate(Rutas.detalle(id)) }
            )
        }
        composable(Rutas.EVALUAR, arguments = argumentoId) {
            sesion?.let { s ->
                EvaluacionScreen(
                    viewModel = viewModel(factory = AppViewModelProvider.Factory),
                    sesion = s,
                    onVolver = { navController.popBackStack() },
                    mostrarMensaje = mostrarMensaje
                )
            }
        }
    }
}
