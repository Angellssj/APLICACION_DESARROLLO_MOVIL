package com.example.appsemestral.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.appsemestral.model.Rol
import com.example.appsemestral.model.Sesion
import com.example.appsemestral.ui.viewmodel.CandidatasViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CandidatasScreen(
    viewModel: CandidatasViewModel,
    sesion: Sesion,
    onNueva: () -> Unit,
    onDetalle: (Long) -> Unit,
    onCerrarSesion: () -> Unit
) {
    val candidatas by viewModel.candidatas.collectAsStateWithLifecycle()
    val error by viewModel.error.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Candidatas · ${sesion.rol.etiqueta}") },
                actions = {
                    IconButton(onClick = viewModel::refrescar) {
                        Icon(Icons.Filled.Refresh, contentDescription = "Actualizar")
                    }
                    IconButton(onClick = onCerrarSesion) {
                        Icon(Icons.AutoMirrored.Filled.Logout, contentDescription = "Cerrar sesión")
                    }
                }
            )
        },
        floatingActionButton = {
            if (sesion.rol == Rol.EVALUADORA) {
                FloatingActionButton(onClick = onNueva) {
                    Icon(Icons.Filled.Add, contentDescription = "Nueva candidatura")
                }
            }
        }
    ) { padding ->
        Column(Modifier.fillMaxSize().padding(padding).padding(horizontal = 16.dp)) {
            error?.let { Text(it, color = MaterialTheme.colorScheme.error, modifier = Modifier.padding(vertical = 8.dp)) }
            if (candidatas.isEmpty()) {
                Text("No hay candidaturas para mostrar.", modifier = Modifier.padding(vertical = 16.dp))
            }
            LazyColumn {
                items(candidatas, key = { it.id }) { c ->
                    Card(Modifier.fillMaxWidth().padding(vertical = 6.dp).clickable { onDetalle(c.id) }) {
                        Column(Modifier.padding(16.dp)) {
                            Text(c.nombre, style = MaterialTheme.typography.titleMedium)
                            Text(c.area, style = MaterialTheme.typography.bodyMedium)
                            Text(
                                "${c.aniosExperiencia} años · ${c.estado.etiqueta}",
                                style = MaterialTheme.typography.bodySmall
                            )
                        }
                    }
                }
            }
        }
    }
}
