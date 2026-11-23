package com.example.appsemestral.ui.screens

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.AttachFile
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.appsemestral.model.Candidata
import com.example.appsemestral.model.Sesion
import com.example.appsemestral.nativo.RecursosNativos
import com.example.appsemestral.ui.components.BarraSuperior
import com.example.appsemestral.ui.components.Seccion
import com.example.appsemestral.ui.viewmodel.CampoState
import com.example.appsemestral.ui.viewmodel.CandidataFormViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CandidataFormScreen(
    viewModel: CandidataFormViewModel,
    sesion: Sesion,
    onVolver: () -> Unit,
    mostrarMensaje: (String) -> Unit
) {
    val ui by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current
    // RECURSO NATIVO: selector de documentos del sistema, filtrado a PDF
    val selectorPdf = rememberLauncherForActivityResult(ActivityResultContracts.OpenDocument()) { uri ->
        if (uri != null) {
            viewModel.onCvSeleccionado(uri.toString(), RecursosNativos.prepararCv(context, uri))
        }
    }

    LaunchedEffect(ui.guardado) {
        if (ui.guardado) {
            mostrarMensaje(if (viewModel.esEdicion) "Candidatura actualizada" else "Candidatura registrada")
            onVolver()
        }
    }
    LaunchedEffect(ui.mensaje) {
        ui.mensaje?.let { mostrarMensaje(it); viewModel.mensajeLeido() }
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            BarraSuperior(
                titulo = if (viewModel.esEdicion) "Editar candidatura" else "Nueva candidatura",
                subtitulo = "Usa solo datos ficticios",
                onVolver = onVolver
            )
        }
    ) { padding ->
        Column(
            Modifier
                .padding(padding)
                .imePadding()
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
          Seccion("Datos personales") {
            Campo("Nombre completo", ui.nombre, viewModel::onNombre)
            // API externa: randomuser.me
            FilledTonalButton(
                onClick = viewModel::generarNombreFicticio,
                shape = MaterialTheme.shapes.medium
            ) {
                Icon(Icons.Filled.AutoAwesome, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(Modifier.width(6.dp))
                Text("Generar nombre ficticio · API externa")
            }
            Campo("Correo", ui.correo, viewModel::onCorreo, KeyboardType.Email)
            Campo("Teléfono", ui.telefono, viewModel::onTelefono, KeyboardType.Phone)
          }

          Seccion("Perfil profesional") {

            var abierto by remember { mutableStateOf(false) }
            ExposedDropdownMenuBox(expanded = abierto, onExpandedChange = { abierto = it }) {
                OutlinedTextField(
                    value = ui.area.orEmpty(),
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Área") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = abierto) },
                    isError = ui.areaError != null,
                    supportingText = { ui.areaError?.let { Text(it) } },
                    shape = MaterialTheme.shapes.medium,
                    modifier = Modifier.menuAnchor(MenuAnchorType.PrimaryNotEditable).fillMaxWidth()
                )
                ExposedDropdownMenu(expanded = abierto, onDismissRequest = { abierto = false }) {
                    Candidata.AREAS.forEach { area ->
                        DropdownMenuItem(text = { Text(area) }, onClick = { viewModel.onArea(area); abierto = false })
                    }
                }
            }

            Campo("Años de experiencia", ui.anios, viewModel::onAnios, KeyboardType.Number)
            Campo("Resumen de experiencia", ui.resumen, viewModel::onResumen, lineas = 4)
            Campo("Perfil de LinkedIn (https://...)", ui.linkedin, viewModel::onLinkedin, KeyboardType.Uri)
          }

          Seccion("Documentos") {
            Row(verticalAlignment = Alignment.CenterVertically) {
                OutlinedButton(
                    onClick = { selectorPdf.launch(arrayOf(RecursosNativos.MIME_PDF)) },
                    shape = MaterialTheme.shapes.medium,
                    modifier = Modifier.weight(1f).height(52.dp)
                ) {
                    Icon(Icons.Filled.AttachFile, contentDescription = null)
                    Spacer(Modifier.width(6.dp))
                    Text(ui.cvNombre ?: "Adjuntar CV en PDF (opcional)", maxLines = 1)
                }
                if (ui.cvNombre != null) {
                    IconButton(onClick = viewModel::quitarCv) {
                        Icon(Icons.Filled.Close, contentDescription = "Quitar CV")
                    }
                }
            }
            Text(
                "El PDF se guarda en este teléfono; al servidor solo se envía su nombre.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
          }

            Button(
                onClick = { viewModel.guardar(sesion.nombre) },
                enabled = !ui.guardando,
                shape = MaterialTheme.shapes.medium,
                modifier = Modifier.fillMaxWidth().height(52.dp)
            ) {
                if (ui.guardando) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        strokeWidth = 2.dp,
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                } else {
                    Icon(Icons.Filled.Save, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(Modifier.width(8.dp))
                    Text(if (viewModel.esEdicion) "Guardar cambios" else "Registrar candidata")
                }
            }
            Spacer(Modifier.height(8.dp))
        }
    }
}

@Composable
private fun Campo(
    etiqueta: String,
    campo: CampoState,
    onCambio: (String) -> Unit,
    teclado: KeyboardType = KeyboardType.Text,
    lineas: Int = 1
) {
    OutlinedTextField(
        value = campo.valor,
        onValueChange = onCambio,
        label = { Text(etiqueta) },
        isError = campo.error != null,
        supportingText = { campo.error?.let { Text(it) } },
        keyboardOptions = KeyboardOptions(keyboardType = teclado),
        singleLine = lineas == 1,
        shape = MaterialTheme.shapes.medium,
        minLines = lineas,
        modifier = Modifier.fillMaxWidth()
    )
}
