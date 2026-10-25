package com.example.myapplication.ui

import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.text.KeyboardOptions
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
import com.example.myapplication.ui.viewmodel.WimViewModel
import com.example.myapplication.validation.CandidataValidator
import kotlinx.coroutines.launch

private enum class Pantalla { LOGIN, LISTADO, FORMULARIO, DETALLE }

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WimApp(viewModel: WimViewModel) {
    var pantalla by rememberSaveable { mutableStateOf(Pantalla.LOGIN) }
    var nombreSesion by rememberSaveable { mutableStateOf("") }
    var rolSesion by rememberSaveable { mutableStateOf("Evaluadora") }
    var candidataId by rememberSaveable { mutableStateOf(-1L) }
    val candidatas by viewModel.candidatas.collectAsState()
    val aviso = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    fun regresar() {
        pantalla = when (pantalla) {
            Pantalla.FORMULARIO, Pantalla.DETALLE -> Pantalla.LISTADO
            Pantalla.LISTADO -> Pantalla.LOGIN
            Pantalla.LOGIN -> Pantalla.LOGIN
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
                        TextButton(onClick = {
                            nombreSesion = ""
                            pantalla = Pantalla.LOGIN
                        }) { Text("Salir") }
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
                        onDetalle = { id ->
                            candidataId = id
                            pantalla = Pantalla.DETALLE
                        }
                    )
                    Pantalla.FORMULARIO -> FormularioScreen(
                        onGuardar = { nombre, correo, area, experiencia, cv ->
                            val error = viewModel.registrar(nombre, correo, area, experiencia, cv)
                            if (error == null) pantalla = Pantalla.LISTADO
                            else scope.launch { aviso.showSnackbar(error) }
                        }
                    )
                    Pantalla.DETALLE -> DetalleScreen(
                        candidata = candidatas.firstOrNull { it.id == candidataId },
                        onError = { scope.launch { aviso.showSnackbar(it) } }
                    )
                }
            }
        }
    }
}

@Composable
private fun LoginScreen(onEntrar: (String, String) -> Unit) {
    var nombre by rememberSaveable { mutableStateOf("") }
    var rol by rememberSaveable { mutableStateOf("Evaluadora") }
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
            if (rol == "Evaluadora") Button(onClick = { rol = "Evaluadora" }) { Text("Evaluadora") }
            else OutlinedButton(onClick = { rol = "Evaluadora" }) { Text("Evaluadora") }
            if (rol == "Dirección") Button(onClick = { rol = "Dirección" }) { Text("Dirección") }
            else OutlinedButton(onClick = { rol = "Dirección" }) { Text("Dirección") }
        }
        Button(onClick = {
            if (nombre.trim().isNotBlank()) onEntrar(nombre.trim(), rol)
            else error = true
        }, modifier = Modifier.fillMaxWidth()) { Text("Ingresar") }
    }
}

@Composable
private fun ListadoScreen(
    candidatas: List<Candidata>, usuario: String, rol: String,
    onNueva: () -> Unit, onDetalle: (Long) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Text("Bienvenido/a, $usuario", style = MaterialTheme.typography.titleLarge)
        Text("Perfil: $rol | Registros: ${candidatas.size}")
        Button(onClick = onNueva, modifier = Modifier.fillMaxWidth()) { Text("+ Registrar candidata") }
        HorizontalDivider()
        if (candidatas.isEmpty()) {
            Text("No hay candidatas. Crea la primera con el botón superior.")
        }
        Column(modifier = Modifier.verticalScroll(rememberScrollState()), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            candidatas.forEach { candidata ->
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
private fun FormularioScreen(onGuardar: (String, String, String, String, String?) -> Unit) {
    val context = LocalContext.current
    var nombre by rememberSaveable { mutableStateOf("") }
    var correo by rememberSaveable { mutableStateOf("") }
    var area by rememberSaveable { mutableStateOf("") }
    var experiencia by rememberSaveable { mutableStateOf("") }
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
        OutlinedButton(onClick = { selectorPdf.launch(arrayOf("application/pdf")) }, modifier = Modifier.fillMaxWidth()) {
            Text(if (cvUri == null) "Adjuntar CV PDF (opcional)" else "CV PDF seleccionado ✓")
        }
        Button(onClick = {
            errores.clear()
            errores.putAll(CandidataValidator.validar(nombre, correo, area, experiencia))
            if (errores.isEmpty()) onGuardar(nombre, correo, area, experiencia, cvUri)
        }, modifier = Modifier.fillMaxWidth()) { Text("Guardar candidata") }
        Spacer(Modifier.height(10.dp))
    }
}

@Composable
private fun DetalleScreen(candidata: Candidata?, onError: (String) -> Unit) {
    val context = LocalContext.current
    if (candidata == null) {
        Text("No se encontró esta candidata.")
        return
    }
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Text("Detalle de candidata", style = MaterialTheme.typography.headlineSmall)
        Text("Nombre: ${candidata.nombre}")
        Text("Correo: ${candidata.correo}")
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
    }
}
