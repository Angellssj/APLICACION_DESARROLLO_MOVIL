package com.example.myapplication.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.myapplication.model.Candidata
import com.example.myapplication.model.Criterios
import com.example.myapplication.model.Evaluacion
import com.example.myapplication.model.ResumenCandidata
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.math.roundToInt

private val configuracionRegional = Locale.forLanguageTag("es-CL")

private fun formatoPromedio(valor: Double) = String.format(configuracionRegional, "%.1f", valor)

private fun formatoFecha(milis: Long) =
    SimpleDateFormat("dd-MM-yyyy HH:mm", configuracionRegional).format(Date(milis))

@Composable
internal fun EvaluacionScreen(candidata: Candidata?, onGuardar: (List<Int>, String) -> Unit) {
    if (candidata == null) {
        Text("No se encontró esta candidata.")
        return
    }
    val inicial = (Criterios.MINIMO + Criterios.MAXIMO) / 2
    var puntajes by rememberSaveable { mutableStateOf(List(Criterios.nombres.size) { inicial }) }
    var comentario by rememberSaveable { mutableStateOf("") }

    Column(Modifier.verticalScroll(rememberScrollState()), verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text("Evaluar a ${candidata.nombre}", style = MaterialTheme.typography.headlineSmall)
        Text("Puntaje de ${Criterios.MINIMO} a ${Criterios.MAXIMO} por criterio.")
        Criterios.nombres.forEachIndexed { indice, nombre ->
            Text("$nombre: ${puntajes[indice]}/${Criterios.MAXIMO}", fontWeight = FontWeight.Bold)
            Slider(
                value = puntajes[indice].toFloat(),
                onValueChange = { nuevo ->
                    puntajes = puntajes.toMutableList().also { it[indice] = nuevo.roundToInt() }
                },
                valueRange = Criterios.MINIMO.toFloat()..Criterios.MAXIMO.toFloat(),
                steps = Criterios.MAXIMO - Criterios.MINIMO - 1
            )
        }
        Text("Promedio: ${formatoPromedio(puntajes.average())}", style = MaterialTheme.typography.titleMedium)
        OutlinedTextField(
            value = comentario,
            onValueChange = { comentario = it },
            label = { Text("Comentario (opcional)") },
            modifier = Modifier.fillMaxWidth(),
            minLines = 2
        )
        Button(onClick = { onGuardar(puntajes, comentario) }, modifier = Modifier.fillMaxWidth()) {
            Text("Guardar evaluación")
        }
        Spacer(Modifier.height(10.dp))
    }
}

@Composable
internal fun EvaluacionCard(evaluacion: Evaluacion) {
    Card(Modifier.fillMaxWidth()) {
        Column(Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(2.dp)) {
            Text(
                "Promedio ${formatoPromedio(evaluacion.promedio)} · ${evaluacion.evaluadora}",
                fontWeight = FontWeight.Bold
            )
            Text(formatoFecha(evaluacion.fecha), style = MaterialTheme.typography.bodySmall)
            Criterios.nombres.zip(evaluacion.puntajes).forEach { (nombre, puntaje) ->
                Text("$nombre: $puntaje", style = MaterialTheme.typography.bodySmall)
            }
            if (evaluacion.comentario.isNotBlank()) Text("“${evaluacion.comentario}”")
        }
    }
}

@Composable
internal fun RankingScreen(ranking: List<ResumenCandidata>, onDetalle: (Long) -> Unit) {
    Column(Modifier.fillMaxSize(), verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Text("Ranking de candidatas", style = MaterialTheme.typography.headlineSmall)
        Text("Ordenado por el promedio de todas sus evaluaciones.")
        if (ranking.isEmpty()) Text("Aún no hay candidatas registradas.")
        LazyColumn(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            itemsIndexed(ranking, key = { _, item -> item.candidata.id }) { posicion, resumen ->
                Card(onClick = { onDetalle(resumen.candidata.id) }, modifier = Modifier.fillMaxWidth()) {
                    Column(Modifier.padding(16.dp)) {
                        Text("${posicion + 1}. ${resumen.candidata.nombre}", fontWeight = FontWeight.Bold)
                        Text(resumen.candidata.area)
                        Text(
                            resumen.promedio?.let {
                                "Promedio ${formatoPromedio(it)} (${resumen.cantidadEvaluaciones} evaluaciones)"
                            } ?: "Sin evaluaciones",
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }
            }
        }
    }
}
