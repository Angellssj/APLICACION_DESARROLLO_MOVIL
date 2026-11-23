package com.example.appsemestral

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import com.example.appsemestral.data.local.DatosSemilla
import com.example.appsemestral.navigation.WimNavHost
import com.example.appsemestral.ui.theme.MyApplicationTheme
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val container = (application as WimApplication).container
        // Criterios locales y primera descarga de postulantes
        kotlinx.coroutines.MainScope().launch { DatosSemilla.poblarSiVacia(container) }

        setContent {
            MyApplicationTheme {
                val snackbar = remember { SnackbarHostState() }
                val scope = rememberCoroutineScope()
                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    // Cada pantalla maneja sus márgenes de sistema (barra de estado y de navegación)
                    contentWindowInsets = WindowInsets(0, 0, 0, 0),
                    snackbarHost = { SnackbarHost(snackbar, Modifier.navigationBarsPadding()) }
                ) { innerPadding ->
                    androidx.compose.foundation.layout.Box(Modifier.padding(innerPadding)) {
                        WimNavHost(mostrarMensaje = { msg -> scope.launch { snackbar.showSnackbar(msg) } })
                    }
                }
            }
        }
    }
}
