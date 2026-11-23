package com.example.appsemestral.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Leaderboard
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.appsemestral.model.FilaRanking
import com.example.appsemestral.ui.components.Avatar
import com.example.appsemestral.ui.components.BarraSuperior
import com.example.appsemestral.ui.components.EstadoChip
import com.example.appsemestral.ui.theme.Bronce
import com.example.appsemestral.ui.theme.EstadoAmbar
import com.example.appsemestral.ui.theme.Oro
import com.example.appsemestral.ui.theme.Plata
import com.example.appsemestral.ui.viewmodel.RankingViewModel

@Composable
fun RankingScreen(
    viewModel: RankingViewModel,
    onVolver: () -> Unit,
    onDetalle: (Long) -> Unit
) {
    val filas by viewModel.filas.collectAsStateWithLifecycle()

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            BarraSuperior(
                titulo = "Ranking",
                subtitulo = "Puntaje ponderado de 0 a 100",
                onVolver = onVolver
            )
        }
    ) { padding ->
        if (filas.isEmpty()) {
            Column(
                Modifier.fillMaxSize().padding(padding).padding(32.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    Modifier
                        .size(64.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primaryContainer),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Filled.Leaderboard, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                }
                Spacer(Modifier.height(12.dp))
                Text("Sin candidatas evaluadas", style = MaterialTheme.typography.titleMedium)
                Text(
                    "El ranking aparece cuando se registra la primera evaluación.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize().padding(padding),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                itemsIndexed(filas, key = { _, f -> f.candidata.id }) { indice, fila ->
                    FilaPosicion(indice + 1, fila) { onDetalle(fila.candidata.id) }
                }
                item {
                    Text(
                        "En empate gana la evaluación completa y luego la candidata registrada antes.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(top = 8.dp)
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun FilaPosicion(posicion: Int, fila: FilaRanking, onClick: () -> Unit) {
    val medalla: Color? = when (posicion) {
        1 -> Oro
        2 -> Plata
        3 -> Bronce
        else -> null
    }
    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.large,
        colors = CardDefaults.cardColors(
            containerColor = if (posicion == 1) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surface
        ),
        border = BorderStroke(1.dp, medalla ?: MaterialTheme.colorScheme.outlineVariant),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Row(Modifier.padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(
                Modifier
                    .size(32.dp)
                    .clip(CircleShape)
                    .background(medalla ?: MaterialTheme.colorScheme.surfaceVariant),
                contentAlignment = Alignment.Center
            ) {
                if (posicion == 1) {
                    Icon(Icons.Filled.EmojiEvents, contentDescription = "Primer lugar", tint = Color.White, modifier = Modifier.size(18.dp))
                } else {
                    Text(
                        "$posicion",
                        style = MaterialTheme.typography.labelLarge,
                        color = if (medalla != null) Color.White else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            Spacer(Modifier.width(10.dp))
            Avatar(fila.candidata.nombre, tamano = 40.dp)
            Spacer(Modifier.width(12.dp))
            Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(
                    fila.candidata.nombre,
                    style = MaterialTheme.typography.titleMedium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                if (fila.completa) {
                    EstadoChip(fila.candidata.estado)
                } else {
                    Text("Evaluación incompleta", style = MaterialTheme.typography.labelMedium, color = EstadoAmbar)
                }
                LinearProgressIndicator(
                    progress = { (fila.puntaje / 100.0).toFloat().coerceIn(0f, 1f) },
                    modifier = Modifier.fillMaxWidth().height(4.dp),
                    color = MaterialTheme.colorScheme.secondary,
                    trackColor = MaterialTheme.colorScheme.surfaceVariant
                )
            }
            Spacer(Modifier.width(12.dp))
            Text(
                "%.1f".format(fila.puntaje),
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}
