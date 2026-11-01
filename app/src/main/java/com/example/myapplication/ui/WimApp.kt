package com.example.myapplication.ui

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.OpenableColumns
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.myapplication.model.Candidata
import com.example.myapplication.model.Evaluacion
import com.example.myapplication.model.Rol
import com.example.myapplication.ui.viewmodel.WimViewModel
import com.example.myapplication.validation.CandidataValidator
import kotlinx.coroutines.launch

private enum class Pantalla { LOGIN, LISTADO, FORMULARIO, DETALLE, EVALUACION, RANKING }

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WimApp(viewModel: WimViewModel) {
    var pantalla by rememberSaveable { mutableStateOf(Pantalla.LOGIN) }
    var nombreSesion by rememberSaveable { mutableStateOf("") }
    var rolSesion by rememberSaveable { mutableStateOf(Rol.EVALUADORA) }
    var candidataId by rememberSaveable { mutableStateOf(-1L) }
    val candidatas by viewModel.candidatas.collectAsState()
    val evaluaciones by viewModel.evaluaciones.collectAsState()
    val ranking by viewModel.ranking.collectAsState()
    val aviso = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    fun cerrarSesion() {
        nombreSesion = ""
        rolSesion = Rol.EVALUADORA
        candidataId = -1L
        pantalla = Pantalla.LOGIN
    }

    fun regresar() {
        when (pantalla) {
            Pantalla.FORMULARIO, Pantalla.DETALLE, Pantalla.RANKING -> pantalla = Pantalla.LISTADO
            Pantalla.EVALUACION -> pantalla = Pantalla.DETALLE
            Pantalla.LISTADO -> cerrarSesion()
            Pantalla.LOGIN -> Unit
        }
    }
    BackHandler(enabled = pantalla != Pantalla.LOGIN) { regresar() }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("WIM 100", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    if (pantalla != Pantalla.LOGIN) {
                        TextButton(onClick = { regresar() }) { Text("Atrás") }
                    }
                },
                actions = {
                    if (pantalla == Pantalla.LISTADO) {
                        TextButton(onClick = { cerrarSesion() }) { Text("Salir") }
                    }
                }
            )
        },
        snackbarHost = { SnackbarHost(aviso) }
    ) { relleno ->
        // Animación simple: cambio suave de una pantalla a otra.
        Crossfade(targetState = pantalla, label = "Navegacion WIM") { actual ->
            Column(Modifier.fillMaxSize().padding(relleno).padding(16.dp)) {
                when (actual) {
                    Pantalla.LOGIN -> LoginScreen { nombre, rol ->
                        nombreSesion = nombre
                        rolSesion = rol
                        pantalla = Pantalla.LISTADO
                    }
                    Pantalla.LISTADO -> ListadoScreen(
                        candidatas = candidatas,
                        usuario = nombreSesion,
                        rol = rolSesion,
                        onNueva = { pantalla = Pantalla.FORMULARIO },
                        onRanking = { pantalla = Pantalla.RANKING },
                        onDetalle = { id ->
                            candidataId = id
                            pantalla = Pantalla.DETALLE
                        }
                    )
                    Pantalla.FORMULARIO -> FormularioScreen(
                        onGuardar = { nombre, correo, area, experiencia, telefono, cv ->
                            scope.launch {
                                val error = viewModel.registrar(nombre, correo, area, experiencia, telefono, cv)
                                if (error == null) {
                                    pantalla = Pantalla.LISTADO
                                    aviso.showSnackbar("Candidata registrada")
                                } else {
                                    aviso.showSnackbar(error)
                                }
                            }
                        }
                    )
                    Pantalla.DETALLE -> DetalleScreen(
                        candidata = candidatas.firstOrNull { it.id == candidataId },
                        evaluaciones = evaluaciones.filter { it.candidataId == candidataId },
                        rol = rolSesion,
                        onEvaluar = { pantalla = Pantalla.EVALUACION },
                        onEliminar = {
                            scope.launch {
                                val error = viewModel.eliminar(candidataId)
                                if (error == null) {
                                    pantalla = Pantalla.LISTADO
                                    aviso.showSnackbar("Candidata eliminada")
                                } else {
                                    aviso.showSnackbar(error)
                                }
                            }
                        },
                        onError = { scope.launch { aviso.showSnackbar(it) } }
                    )
                    Pantalla.EVALUACION -> EvaluacionScreen(
                        candidata = candidatas.firstOrNull { it.id == candidataId },
                        onGuardar = { puntajes, comentario ->
                            scope.launch {
                                val error = viewModel.evaluar(candidataId, nombreSesion, puntajes, comentario)
                                if (error == null) {
                                    pantalla = Pantalla.DETALLE
                                    aviso.showSnackbar("Evaluación guardada")
                                } else {
                                    aviso.showSnackbar(error)
                                }
                            }
                        }
                    )
                    Pantalla.RANKING -> RankingScreen(
                        ranking = ranking,
                        onDetalle = { id ->
                            candidataId = id
                            pantalla = Pantalla.DETALLE
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun LoginScreen(onEntrar: (String, Rol) -> Unit) {
    var nombre by rememberSaveable { mutableStateOf("") }
    var rol by rememberSaveable { mutableStateOf(Rol.EVALUADORA) }
    var error by remember { mutableStateOf(false) }
    Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
        Text("Sistema académico de candidaturas", style = MaterialTheme.typography.headlineSmall)
        Text("Acceso de demostración: no usa contraseñas reales.")
        OutlinedTextField(
            value = nombre,
            onValueChange = { nombre = it; error = false },
            label = { Text("Nombre y apellido") },
            isError = error,
            supportingText = { if (error) Text("Ingresa tu nombre") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Rol.entries.forEach { opcion ->
                if (rol == opcion) Button(onClick = { rol = opcion }) { Text(opcion.etiqueta) }
                else OutlinedButton(onClick = { rol = opcion }) { Text(opcion.etiqueta) }
            }
        }
        Text(
            if (rol.puedeEvaluar) "Evaluadora: registra y evalúa candidatas."
            else "Dirección: consulta el ranking y puede eliminar registros.",
            style = MaterialTheme.typography.bodySmall
        )
        Button(onClick = {
            if (nombre.trim().isNotBlank()) onEntrar(nombre.trim(), rol)
            else error = true
        }, modifier = Modifier.fillMaxWidth()) { Text("Ingresar") }
    }
}

@Composable
private fun ListadoScreen(
    candidatas: List<Candidata>, usuario: String, rol: Rol,
    onNueva: () -> Unit, onRanking: () -> Unit, onDetalle: (Long) -> Unit
) {
    Column(Modifier.fillMaxSize(), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Text("Bienvenido/a, $usuario", style = MaterialTheme.typography.titleLarge)
        Text("Perfil: ${rol.etiqueta} | Registros: ${candidatas.size}")
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Button(onClick = onNueva, modifier = Modifier.weight(1f)) { Text("+ Registrar") }
            OutlinedButton(onClick = onRanking, modifier = Modifier.weight(1f)) { Text("Ranking") }
        }
        HorizontalDivider()
        if (candidatas.isEmpty()) {
            Text("No hay candidatas. Crea la primera con el botón superior.")
        }
        LazyColumn(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            items(candidatas, key = { it.id }) { candidata ->
                Card(onClick = { onDetalle(candidata.id) }, modifier = Modifier.fillMaxWidth()) {
                    Column(Modifier.padding(16.dp)) {
                        Text(candidata.nombre, fontWeight = FontWeight.Bold)
                        Text(candidata.area)
                        Text(candidata.correo, style = MaterialTheme.typography.bodySmall)
                    }
                }
            }
        }
    }
}

@Composable
private fun FormularioScreen(onGuardar: (String, String, String, String, String, String?) -> Unit) {
    val context = LocalContext.current
    var nombre by rememberSaveable { mutableStateOf("") }
    var correo by rememberSaveable { mutableStateOf("") }
    var area by rememberSaveable { mutableStateOf("") }
    var experiencia by rememberSaveable { mutableStateOf("") }
    var telefono by rememberSaveable { mutableStateOf("") }
    var cvUri by rememberSaveable { mutableStateOf<String?>(null) }
    val errores = remember { mutableStateMapOf<String, String>() }
    val selectorPdf = rememberLauncherForActivityResult(ActivityResultContracts.OpenDocument()) { uri ->
        if (uri != null) {
            try {
                context.contentResolver.takePersistableUriPermission(uri, Intent.FLAG_GRANT_READ_URI_PERMISSION)
            } catch (_: SecurityException) { /* Algunos proveedores no ofrecen permiso persistente. */ }
            cvUri = uri.toString()
        }
    }

    Column(Modifier.verticalScroll(rememberScrollState()), verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Text("Nueva candidata", style = MaterialTheme.typography.headlineSmall)
        Text("Completa los campos obligatorios.")
        OutlinedTextField(
            value = nombre, onValueChange = { nombre = it; errores.remove("nombre") },
            label = { Text("Nombre y apellido") }, modifier = Modifier.fillMaxWidth(),
            isError = errores.containsKey("nombre"), singleLine = true,
            supportingText = { errores["nombre"]?.let { Text("⚠ $it") } }
        )
        OutlinedTextField(
            value = correo, onValueChange = { correo = it; errores.remove("correo") },
            label = { Text("Correo") }, modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
            isError = errores.containsKey("correo"), singleLine = true,
            supportingText = { errores["correo"]?.let { Text("⚠ $it") } }
        )
        OutlinedTextField(
            value = area, onValueChange = { area = it; errores.remove("area") },
            label = { Text("Área de postulación") }, modifier = Modifier.fillMaxWidth(),
            isError = errores.containsKey("area"), singleLine = true,
            supportingText = { errores["area"]?.let { Text("⚠ $it") } }
        )
        OutlinedTextField(
            value = experiencia, onValueChange = { experiencia = it; errores.remove("experiencia") },
            label = { Text("Años de experiencia") }, modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            isError = errores.containsKey("experiencia"), singleLine = true,
            supportingText = { errores["experiencia"]?.let { Text("⚠ $it") } }
        )
        OutlinedTextField(
            value = telefono, onValueChange = { telefono = it; errores.remove("telefono") },
            label = { Text("Teléfono (opcional)") }, modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
            isError = errores.containsKey("telefono"), singleLine = true,
            supportingText = { errores["telefono"]?.let { Text("⚠ $it") } }
        )
        OutlinedButton(onClick = { selectorPdf.launch(arrayOf("application/pdf")) }, modifier = Modifier.fillMaxWidth()) {
            Text(
                cvUri?.let { "CV: ${nombreArchivo(context, it) ?: "PDF seleccionado"} ✓" }
                    ?: "Adjuntar CV PDF (opcional)"
            )
        }
        Button(onClick = {
            errores.clear()
            errores.putAll(CandidataValidator.validar(nombre, correo, area, experiencia, telefono))
            if (errores.isEmpty()) onGuardar(nombre, correo, area, experiencia, telefono, cvUri)
        }, modifier = Modifier.fillMaxWidth()) { Text("Guardar candidata") }
        Spacer(Modifier.height(10.dp))
    }
}

@Composable
private fun DetalleScreen(
    candidata: Candidata?,
    evaluaciones: List<Evaluacion>,
    rol: Rol,
    onEvaluar: () -> Unit,
    onEliminar: () -> Unit,
    onError: (String) -> Unit
) {
    val context = LocalContext.current
    var confirmarEliminar by rememberSaveable { mutableStateOf(false) }
    if (candidata == null) {
        Text("No se encontró esta candidata.")
        return
    }
    Column(Modifier.verticalScroll(rememberScrollState()), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Text("Detalle de candidata", style = MaterialTheme.typography.headlineSmall)
        Text("Nombre: ${candidata.nombre}")
        Text("Correo: ${candidata.correo}")
        Text("Teléfono: ${candidata.telefono ?: "No indicado"}")
        Text("Área: ${candidata.area}")
        Text("Experiencia: ${candidata.experiencia} años")
        if (candidata.cvUri != null) {
            Button(onClick = {
                try {
                    val intent = Intent(Intent.ACTION_VIEW).apply {
                        setDataAndType(Uri.parse(candidata.cvUri), "application/pdf")
                        addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                    }
                    context.startActivity(intent)
                } catch (_: ActivityNotFoundException) {
                    onError("Instala una aplicación para abrir PDF")
                } catch (_: SecurityException) {
                    onError("No hay permiso de lectura para este documento")
                }
            }) { Text("Abrir CV con otra app") }
        } else Text("No se adjuntó CV")

        HorizontalDivider()
        Text("Evaluaciones (${evaluaciones.size})", style = MaterialTheme.typography.titleMedium)
        if (evaluaciones.isEmpty()) Text("Aún no tiene evaluaciones.")
        evaluaciones.forEach { EvaluacionCard(it) }

        if (rol.puedeEvaluar) {
            Button(onClick = onEvaluar, modifier = Modifier.fillMaxWidth()) { Text("Evaluar candidata") }
        }
        if (rol.puedeEliminar) {
            OutlinedButton(onClick = { confirmarEliminar = true }, modifier = Modifier.fillMaxWidth()) {
                Text("Eliminar candidata")
            }
        }
        Spacer(Modifier.height(10.dp))
    }
    if (confirmarEliminar) {
        AlertDialog(
            onDismissRequest = { confirmarEliminar = false },
            title = { Text("Eliminar candidata") },
            text = { Text("Se borrarán también sus evaluaciones. Esta acción no se puede deshacer.") },
            confirmButton = {
                TextButton(onClick = {
                    confirmarEliminar = false
                    onEliminar()
                }) { Text("Eliminar") }
            },
            dismissButton = { TextButton(onClick = { confirmarEliminar = false }) { Text("Cancelar") } }
        )
    }
}

/** Nombre visible del archivo elegido, o null si el proveedor no lo informa. */
private fun nombreArchivo(context: Context, uri: String): String? = try {
    context.contentResolver.query(Uri.parse(uri), arrayOf(OpenableColumns.DISPLAY_NAME), null, null, null)
        ?.use { cursor -> if (cursor.moveToFirst()) cursor.getString(0) else null }
} catch (_: Exception) {
    null
}
