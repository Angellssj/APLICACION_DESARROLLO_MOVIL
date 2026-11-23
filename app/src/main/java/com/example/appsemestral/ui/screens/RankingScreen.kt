package com.example.appsemestral.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.appsemestral.ui.viewmodel.RankingViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RankingScreen(
    viewModel: RankingViewModel,
    onVolver: () -> Unit,
    onDetalle: (Long) -> Unit
) {
    val filas by viewModel.filas.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Ranking") },
                navigationIcon = {
                    IconButton(onClick = onVolver) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
                    }
                }
            )
        }
    ) { padding ->
        Column(Modifier.fillMaxSize().padding(padding).padding(horizontal = 16.dp)) {
            if (filas.isEmpty()) {
                Text(
                    "Todavía no hay candidatas evaluadas.",
                    modifier = Modifier.padding(vertical = 16.dp)
                )
            }
            LazyColumn {
                itemsIndexed(filas, key = { _, f -> f.candidata.id }) { indice, fila ->
                    val primera = indice == 0
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 6.dp)
                            .clickable { onDetalle(fila.candidata.id) },
                        colors = if (primera) {
                            CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
                        } else {
                            CardDefaults.cardColors()
                        }
                    ) {
                        Row(
                            Modifier.padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            if (primera) {
                                Icon(Icons.Filled.EmojiEvents, contentDescription = "Primer lugar")
                            } else {
                                Text(
                                    "${indice + 1}°",
                                    style = MaterialTheme.typography.titleMedium,
                                    modifier = Modifier.width(24.dp)
                                )
                            }
                            Column(Modifier.weight(1f)) {
                                Text(fila.candidata.nombre, style = MaterialTheme.typography.titleMedium)
                                Text(fila.candidata.area, style = MaterialTheme.typography.bodySmall)
                                Text(
                                    if (fila.completa) fila.candidata.estado.etiqueta else "Evaluación incompleta",
                                    style = MaterialTheme.typography.bodySmall
                                )
                            }
                            Text(
                                "%.1f".format(fila.puntaje),
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }
    }
}
