
package com.example.myapplication.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.keyboard.KeyboardOptions
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.platform.LocalContext
import com.example.myapplication.model.Candidata

@Composable
fun RankingScreen(candidatas: List<Candidata>) {
    val context = LocalContext.current
    val preferencias = remember(context) {
        context.getSharedPreferences(
            "wim100_ranking", 0
        )
    }

    val puntajes = remember {
        mutableStateMapOf<Long, Int>().apply {
            candidatas.forEach { candidata ->
                val clave = "puntaje_${candidata.id}"
                if (preferencias.contains(clave)) {
                    this[candidata.id] =
                        preferencias.getInt(clave, 0)
                }
            }
        }
    }

    val entradas = remember {
        mutableStateMapOf<Long, String>()
    }

    val ranking = candidatas
        .filter { puntajes.containsKey(it.id) }
        .sortedWith(
            compareByDescending<Candidata> {
                puntajes[it.id] ?: 0
            }.thenBy { it.nombre }
        )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            "Resultados y ranking",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold
        )

        Text("HU-09 | Clasificación de candidatas")
        HorizontalDivider()

        Text(
            "Registrar puntajes",
            style = MaterialTheme.typography.titleMedium
        )

        candidatas.forEach { candidata ->
            val entrada = entradas[candidata.id]
                ?: puntajes[candidata.id]?.toString()
                ?: ""

            Card(modifier = Modifier.fillMaxWidth()) {
                Column(
                    modifier = Modifier.padding(12.dp),
                    verticalArrangement =
                        Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        candidata.nombre,
                        fontWeight = FontWeight.Bold
                    )

                    OutlinedTextField(
                        value = entrada,
                        onValueChange = {
                            entradas[candidata.id] =
                                it.filter(Char::isDigit)
                                    .take(3)
                        },
                        label = { Text("Puntaje (0 a 100)") },
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Number
                        ),
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )

                    val valor = entrada.toIntOrNull()
                    Button(
                        onClick = {
                            if (valor != null) {
                                puntajes[candidata.id] = valor
                                preferencias.edit()
                                    .putInt(
                                        "puntaje_${candidata.id}",
                                        valor
                                    ).apply()
                            }
                        },
                        enabled = valor != null &&
                                valor in 0..100
                    ) {
                        Text("Guardar evaluación")
                    }
                }
            }
        }

        HorizontalDivider()

        Text(
            "Clasificación general",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold
        )

        if (ranking.isEmpty()) {
            Text(
                "Aún no existen evaluaciones registradas."
            )
        } else {
            val mejorPuntaje =
                puntajes[ranking.first().id] ?: 0

            Text(
                "Mejor puntaje: $mejorPuntaje puntos",
                fontWeight = FontWeight.Bold
            )

            ranking.forEachIndexed { indice, candidata ->
                val puntaje = puntajes[candidata.id] ?: 0

                Card(modifier = Modifier.fillMaxWidth()) {
                    Column(Modifier.padding(16.dp)) {
                        Text(
                            "${indice + 1}. ${candidata.nombre}",
                            fontWeight = FontWeight.Bold
                        )
                        Text("Puntaje: $puntaje / 100")

                        if (indice == 0) {
                            Text(
                                "🏆 Primer lugar",
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                }
            }
        }

        Spacer(Modifier.height(16.dp))
    }
}
