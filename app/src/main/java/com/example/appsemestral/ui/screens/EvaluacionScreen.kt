package com.example.appsemestral.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.appsemestral.model.Sesion
import com.example.appsemestral.ui.viewmodel.EvaluacionViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EvaluacionScreen(
    viewModel: EvaluacionViewModel,
    sesion: Sesion,
    onVolver: () -> Unit,
    mostrarMensaje: (String) -> Unit
) {
    val candidata by viewModel.candidata.collectAsStateWithLifecycle()
    val criterios by viewModel.criterios.collectAsStateWithLifecycle()
    val puntajes by viewModel.puntajes.collectAsStateWithLifecycle()
    val guardado by viewModel.guardado.collectAsStateWithLifecycle()
    val error by viewModel.error.collectAsStateWithLifecycle()

    LaunchedEffect(guardado) { if (guardado) { mostrarMensaje("Evaluación guardada"); onVolver() } }
    LaunchedEffect(error) { error?.let { mostrarMensaje(it); viewModel.errorLeido() } }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Evaluar a ${candidata?.nombre.orEmpty()}") },
                navigationIcon = {
                    IconButton(onClick = onVolver) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            Modifier.padding(padding).padding(16.dp).verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            criterios.forEach { cr ->
                Text("${cr.nombre} (${cr.peso}%)", style = MaterialTheme.typography.titleMedium)
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    (1..5).forEach { n ->
                        FilterChip(
                            selected = puntajes[cr.id] == n,
                            onClick = { viewModel.puntuar(cr.id, n) },
                            label = { Text(n.toString()) }
                        )
                    }
                }
            }
            Button(onClick = { viewModel.guardar(sesion.nombre) }, modifier = Modifier.fillMaxWidth()) {
                Text("Guardar evaluación")
            }
        }
    }
}
