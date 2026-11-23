package com.example.appsemestral.ui.screens

import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Badge
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Link
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.PictureAsPdf
import androidx.compose.material.icons.filled.RateReview
import androidx.compose.material.icons.filled.WorkOutline
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.appsemestral.model.CalculadoraPuntaje
import com.example.appsemestral.model.EstadoCandidatura
import com.example.appsemestral.model.Rol
import com.example.appsemestral.model.Sesion
import com.example.appsemestral.nativo.RecursosNativos
import com.example.appsemestral.ui.components.Avatar
import com.example.appsemestral.ui.components.BarraSuperior
import com.example.appsemestral.ui.components.EstadoChip
import com.example.appsemestral.ui.components.FilaDato
import com.example.appsemestral.ui.components.Seccion
import com.example.appsemestral.ui.theme.EstadoRojo
import com.example.appsemestral.ui.theme.EstadoVerde
import com.example.appsemestral.ui.viewmodel.DetalleCandidataViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

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
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            BarraSuperior(titulo = "Ficha de candidata", onVolver = onVolver) {
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
        }
    ) { padding ->
        val c = candidata
        if (c == null) {
            Column(
                Modifier.fillMaxSize().padding(padding),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                CircularProgressIndicator()
            }
        } else {
            Column(
                Modifier
                    .padding(padding)
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Cabecera
                Seccion(titulo = null) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Avatar(c.nombre, tamano = 60.dp)
                        Spacer(Modifier.width(14.dp))
                        Column(Modifier.weight(1f)) {
                            Text(c.nombre, style = MaterialTheme.typography.titleLarge)
                            Text(
                                c.area,
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Spacer(Modifier.height(8.dp))
                            EstadoChip(c.estado)
                        }
                    }
                    HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
                    Text(
                        "Registrada por ${c.registradaPor} · ${formatearFecha(c.fechaRegistro)}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                // RECURSOS NATIVOS: intents implícitos de Android
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    AccionRapida(Icons.Filled.Phone, "Llamar", Modifier.weight(1f)) {
                        RecursosNativos.llamar(context, c.telefono)?.let(mostrarMensaje)
                    }
                    AccionRapida(Icons.Filled.Email, "Correo", Modifier.weight(1f)) {
                        RecursosNativos.escribirCorreo(context, c.correo, "Proceso WIM 100")?.let(mostrarMensaje)
                    }
                    val cv = c.cvUri
                    AccionRapida(Icons.Filled.PictureAsPdf, "Ver CV", Modifier.weight(1f), habilitada = cv != null) {
                        if (cv != null) RecursosNativos.abrirPdf(context, cv)?.let(mostrarMensaje)
                    }
                }

                Seccion("Contacto") {
                    FilaDato(Icons.Filled.Email, "Correo", c.correo)
                    FilaDato(Icons.Filled.Phone, "Teléfono", c.telefono)
                    FilaDato(Icons.Filled.Link, "LinkedIn", c.linkedinUrl)
                    c.cvNombre?.let { nombre ->
                        FilaDato(
                            Icons.Filled.Badge,
                            "Currículum",
                            if (c.cvUri != null) nombre else "$nombre (adjuntado en otro teléfono)"
                        )
                    }
                }

                Seccion("Perfil profesional") {
                    FilaDato(Icons.Filled.WorkOutline, "Experiencia", "${c.aniosExperiencia} años")
                    Text(c.resumenExperiencia, style = MaterialTheme.typography.bodyMedium)
                }

                Seccion("Evaluación") {
                    if (evaluaciones.isEmpty()) {
                        Text(
                            "Todavía no tiene evaluación.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    } else {
                        val total = CalculadoraPuntaje.total(criterios, evaluaciones)
                        Row(verticalAlignment = Alignment.Bottom) {
                            Text(
                                "%.1f".format(total),
                                style = MaterialTheme.typography.displaySmall,
                                color = MaterialTheme.colorScheme.primary
                            )
                            Text(
                                " / 100",
                                style = MaterialTheme.typography.titleMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.padding(bottom = 6.dp)
                            )
                        }
                        LinearProgressIndicator(
                            progress = { (total / 100.0).toFloat().coerceIn(0f, 1f) },
                            modifier = Modifier.fillMaxWidth().height(8.dp),
                            color = MaterialTheme.colorScheme.secondary,
                            trackColor = MaterialTheme.colorScheme.surfaceVariant
                        )
                        Spacer(Modifier.height(4.dp))
                        criterios.forEach { cr ->
                            val p = evaluaciones.firstOrNull { it.criterioId == cr.id }?.puntaje
                            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                Row {
                                    Text(
                                        cr.nombre,
                                        style = MaterialTheme.typography.bodyMedium,
                                        modifier = Modifier.weight(1f),
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                    Text(
                                        "${p ?: "—"} / 5 · ${cr.peso}%",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                                LinearProgressIndicator(
                                    progress = { (p ?: 0) / 5f },
                                    modifier = Modifier.fillMaxWidth().height(6.dp),
                                    color = MaterialTheme.colorScheme.primary,
                                    trackColor = MaterialTheme.colorScheme.surfaceVariant
                                )
                            }
                        }
                    }
                }

                if (sesion.rol == Rol.EVALUADORA && !c.estado.estaResuelta) {
                    Button(
                        onClick = { onEvaluar(c.id) },
                        shape = MaterialTheme.shapes.medium,
                        modifier = Modifier.fillMaxWidth().height(52.dp)
                    ) {
                        Icon(Icons.Filled.RateReview, contentDescription = null)
                        Spacer(Modifier.width(8.dp))
                        Text(if (evaluaciones.isEmpty()) "Evaluar candidata" else "Editar evaluación")
                    }
                }
                if (sesion.rol == Rol.DIRECCION && c.estado == EstadoCandidatura.EVALUADA) {
                    Seccion("Decisión de la Dirección") {
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Button(
                                onClick = { viewModel.resolver(EstadoCandidatura.SELECCIONADA, sesion.nombre) },
                                colors = ButtonDefaults.buttonColors(containerColor = EstadoVerde),
                                shape = MaterialTheme.shapes.medium,
                                modifier = Modifier.weight(1f).height(48.dp)
                            ) {
                                Icon(Icons.Filled.CheckCircle, contentDescription = null, modifier = Modifier.size(18.dp))
                                Spacer(Modifier.width(6.dp))
                                Text("Seleccionar")
                            }
                            OutlinedButton(
                                onClick = { viewModel.resolver(EstadoCandidatura.NO_SELECCIONADA, sesion.nombre) },
                                colors = ButtonDefaults.outlinedButtonColors(contentColor = EstadoRojo),
                                shape = MaterialTheme.shapes.medium,
                                modifier = Modifier.weight(1f).height(48.dp)
                            ) {
                                Icon(Icons.Filled.Cancel, contentDescription = null, modifier = Modifier.size(18.dp))
                                Spacer(Modifier.width(6.dp))
                                Text("No seleccionar")
                            }
                        }
                    }
                }
                Spacer(Modifier.height(8.dp))
            }
        }
    }

    if (confirmarEliminar) {
        AlertDialog(
            onDismissRequest = { confirmarEliminar = false },
            icon = { Icon(Icons.Filled.Delete, contentDescription = null) },
            title = { Text("¿Eliminar candidatura?") },
            text = { Text("Se eliminará del servidor junto con sus evaluaciones locales. Esta acción no se puede deshacer.") },
            confirmButton = {
                TextButton(onClick = { confirmarEliminar = false; viewModel.eliminar(sesion.nombre) }) {
                    Text("Eliminar", color = MaterialTheme.colorScheme.error, fontWeight = FontWeight.SemiBold)
                }
            },
            dismissButton = { TextButton(onClick = { confirmarEliminar = false }) { Text("Cancelar") } }
        )
    }
}

@Composable
private fun AccionRapida(
    icono: ImageVector,
    texto: String,
    modifier: Modifier = Modifier,
    habilitada: Boolean = true,
    onClick: () -> Unit
) {
    FilledTonalButton(
        onClick = onClick,
        enabled = habilitada,
        shape = MaterialTheme.shapes.medium,
        contentPadding = PaddingValues(4.dp),
        modifier = modifier.height(56.dp)
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(icono, contentDescription = null, modifier = Modifier.size(18.dp))
            Text(texto, style = MaterialTheme.typography.labelMedium)
        }
    }
}

private fun formatearFecha(millis: Long): String =
    SimpleDateFormat("d MMM yyyy", Locale.forLanguageTag("es-CL")).format(Date(millis))
