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
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.PictureAsPdf
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.appsemestral.model.CalculadoraPuntaje
import com.example.appsemestral.model.EstadoCandidatura
import com.example.appsemestral.model.Rol
import com.example.appsemestral.model.Sesion
import com.example.appsemestral.nativo.RecursosNativos
import com.example.appsemestral.ui.viewmodel.DetalleCandidataViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetalleCandidataScreen(
    viewModel: DetalleCandidataViewModel,
    sesion: Sesion,
    onVolver: () -> Unit,
    onEditar: (Long) -> Unit,
    onEvaluar: (Long) -> Unit,
    mostrarMensaje: (String) -> Unit
) {
    val candidata by viewModel.candidata.collectAsStateWithLifecycle()
    val criterios by viewModel.criterios.collectAsStateWithLifecycle()
    val evaluaciones by viewModel.evaluaciones.collectAsStateWithLifecycle()
    val eliminada by viewModel.eliminada.collectAsStateWithLifecycle()
    val error by viewModel.error.collectAsStateWithLifecycle()
    var confirmarEliminar by remember { mutableStateOf(false) }
    val context = LocalContext.current

    LaunchedEffect(eliminada) { if (eliminada) { mostrarMensaje("Candidatura eliminada"); onVolver() } }
    LaunchedEffect(error) { error?.let { mostrarMensaje(it); viewModel.errorLeido() } }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Detalle") },
                navigationIcon = {
                    IconButton(onClick = onVolver) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
                    }
                },
                actions = {
                    val c = candidata
                    if (c != null && sesion.rol == Rol.EVALUADORA) {
                        IconButton(onClick = { onEditar(c.id) }) {
                            Icon(Icons.Filled.Edit, contentDescription = "Editar")
                        }
                        IconButton(onClick = { confirmarEliminar = true }) {
                            Icon(Icons.Filled.Delete, contentDescription = "Eliminar")
                        }
                    }
                }
            )
        }
    ) { padding ->
        val c = candidata
        if (c == null) {
            Text("Cargando…", modifier = Modifier.padding(padding).padding(16.dp))
        } else {
            Column(
                Modifier.padding(padding).padding(16.dp).verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Text(c.nombre, style = MaterialTheme.typography.headlineSmall)
                Text("Estado: ${c.estado.etiqueta}", style = MaterialTheme.typography.titleMedium)
                Text("Área: ${c.area}")
                Text("Experiencia: ${c.aniosExperiencia} años")
                Text("Correo: ${c.correo}")
                Text("Teléfono: ${c.telefono}")
                Text("LinkedIn: ${c.linkedinUrl}")
                c.cvNombre?.let { Text("CV: $it") }

                // RECURSOS NATIVOS: intents implícitos de Android
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedButton(onClick = {
                        RecursosNativos.llamar(context, c.telefono)?.let(mostrarMensaje)
                    }) {
                        Icon(Icons.Filled.Phone, contentDescription = null)
                        Text(" Llamar")
                    }
                    OutlinedButton(onClick = {
                        RecursosNativos.escribirCorreo(context, c.correo, "Proceso WIM 100")?.let(mostrarMensaje)
                    }) {
                        Icon(Icons.Filled.Email, contentDescription = null)
                        Text(" Correo")
                    }
                }
                val cv = c.cvUri
                if (cv != null) {
                    Button(
                        onClick = { RecursosNativos.abrirPdf(context, cv)?.let(mostrarMensaje) },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(Icons.Filled.PictureAsPdf, contentDescription = null)
                        Text(" Abrir CV")
                    }
                } else if (c.cvNombre != null) {
                    Text(
                        "El PDF se adjuntó en otro teléfono; aquí solo se conoce su nombre.",
                        style = MaterialTheme.typography.bodySmall
                    )
                }
                Text("Registrada por: ${c.registradaPor}")
                Text(c.resumenExperiencia, modifier = Modifier.padding(vertical = 8.dp))

                if (evaluaciones.isNotEmpty()) {
                    val total = CalculadoraPuntaje.total(criterios, evaluaciones)
                    Text("Puntaje: ${"%.1f".format(total)} / 100", style = MaterialTheme.typography.titleMedium)
                    criterios.forEach { cr ->
                        val p = evaluaciones.firstOrNull { it.criterioId == cr.id }?.puntaje
                        Text("• ${cr.nombre} (${cr.peso}%): ${p ?: "—"} / 5")
                    }
                }

                if (sesion.rol == Rol.EVALUADORA && !c.estado.estaResuelta) {
                    Button(onClick = { onEvaluar(c.id) }, modifier = Modifier.fillMaxWidth()) {
                        Text(if (evaluaciones.isEmpty()) "Evaluar" else "Editar evaluación")
                    }
                }
                if (sesion.rol == Rol.DIRECCION && c.estado == EstadoCandidatura.EVALUADA) {
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Button(onClick = { viewModel.resolver(EstadoCandidatura.SELECCIONADA, sesion.nombre) }) {
                            Text("Seleccionar")
                        }
                        OutlinedButton(onClick = { viewModel.resolver(EstadoCandidatura.NO_SELECCIONADA, sesion.nombre) }) {
                            Text("No seleccionar")
                        }
                    }
                }
            }
        }
    }

    if (confirmarEliminar) {
        AlertDialog(
            onDismissRequest = { confirmarEliminar = false },
            title = { Text("¿Eliminar candidatura?") },
            text = { Text("Se eliminará del servidor junto con sus evaluaciones locales.") },
            confirmButton = {
                TextButton(onClick = { confirmarEliminar = false; viewModel.eliminar(sesion.nombre) }) { Text("Eliminar") }
            },
            dismissButton = { TextButton(onClick = { confirmarEliminar = false }) { Text("Cancelar") } }
        )
    }
}
