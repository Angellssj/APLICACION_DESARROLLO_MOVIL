package com.example.appsemestral.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.appsemestral.model.Sesion
import com.example.appsemestral.ui.components.BarraSuperior
import com.example.appsemestral.ui.components.Seccion
import com.example.appsemestral.ui.viewmodel.EvaluacionViewModel

private val etiquetasNivel = mapOf(
    1 to "Insuficiente",
    2 to "Básico",
    3 to "Adecuado",
    4 to "Destacado",
    5 to "Sobresaliente"
)

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

    // Puntaje en vivo con la misma fórmula de CalculadoraPuntaje (0 a 100)
    val pesoTotal = criterios.sumOf { it.peso }
    val estimado = if (pesoTotal == 0) 0.0
    else criterios.sumOf { (puntajes[it.id] ?: 0) * it.peso } * 100.0 / (5 * pesoTotal)
    val respondidos = criterios.count { puntajes[it.id] != null }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            BarraSuperior(
                titulo = "Evaluación",
                subtitulo = candidata?.nombre,
                onVolver = onVolver
            )
        }
    ) { padding ->
        Column(
            Modifier
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Seccion("Puntaje ponderado") {
                Row(verticalAlignment = Alignment.Bottom) {
                    Text(
                        "%.1f".format(estimado),
                        style = MaterialTheme.typography.displaySmall,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        " / 100",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(bottom = 6.dp)
                    )
                    Spacer(Modifier.weight(1f))
                    Text(
                        "$respondidos de ${criterios.size} criterios",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(bottom = 6.dp)
                    )
                }
                LinearProgressIndicator(
                    progress = { (estimado / 100.0).toFloat().coerceIn(0f, 1f) },
                    modifier = Modifier.fillMaxWidth().height(8.dp),
                    color = MaterialTheme.colorScheme.secondary,
                    trackColor = MaterialTheme.colorScheme.surfaceVariant
                )
                Text(
                    "Escala: 1 insuficiente · 3 adecuado · 5 sobresaliente",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            criterios.forEach { cr ->
                val elegido = puntajes[cr.id]
                Seccion(titulo = null) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(cr.nombre, style = MaterialTheme.typography.titleMedium, modifier = Modifier.weight(1f))
                        Text(
                            "Peso ${cr.peso}%",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.secondary
                        )
                    }
                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        (1..5).forEach { n ->
                            val activo = elegido == n
                            if (activo) {
                                Button(
                                    onClick = { viewModel.puntuar(cr.id, n) },
                                    shape = MaterialTheme.shapes.small,
                                    contentPadding = androidx.compose.foundation.layout.PaddingValues(0.dp),
                                    modifier = Modifier.weight(1f).height(44.dp)
                                ) { Text(n.toString(), fontWeight = FontWeight.Bold) }
                            } else {
                                OutlinedButton(
                                    onClick = { viewModel.puntuar(cr.id, n) },
                                    shape = MaterialTheme.shapes.small,
                                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
                                    contentPadding = androidx.compose.foundation.layout.PaddingValues(0.dp),
                                    modifier = Modifier.weight(1f).height(44.dp)
                                ) { Text(n.toString()) }
                            }
                        }
                    }
                    Text(
                        elegido?.let { etiquetasNivel[it] } ?: "Sin puntaje",
                        style = MaterialTheme.typography.bodySmall,
                        color = if (elegido != null) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Button(
                onClick = { viewModel.guardar(sesion.nombre) },
                shape = MaterialTheme.shapes.medium,
                modifier = Modifier.fillMaxWidth().height(52.dp)
            ) {
                Icon(Icons.Filled.Save, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(Modifier.width(8.dp))
                Text("Guardar evaluación")
            }
            Spacer(Modifier.height(8.dp))
        }
    }
}
