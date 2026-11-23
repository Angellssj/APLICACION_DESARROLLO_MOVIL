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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Leaderboard
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material.icons.filled.PersonSearch
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.WorkOutline
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.appsemestral.model.Candidata
import com.example.appsemestral.model.EstadoCandidatura
import com.example.appsemestral.model.Rol
import com.example.appsemestral.model.Sesion
import com.example.appsemestral.ui.components.Avatar
import com.example.appsemestral.ui.components.BarraSuperior
import com.example.appsemestral.ui.components.EstadoChip
import com.example.appsemestral.ui.components.Indicador
import com.example.appsemestral.ui.theme.EstadoAmbar
import com.example.appsemestral.ui.theme.EstadoAzul
import com.example.appsemestral.ui.theme.EstadoVerde
import com.example.appsemestral.ui.viewmodel.CandidatasViewModel

@Composable
fun CandidatasScreen(
    viewModel: CandidatasViewModel,
    sesion: Sesion,
    onNueva: () -> Unit,
    onDetalle: (Long) -> Unit,
    onRanking: () -> Unit,
    onCerrarSesion: () -> Unit
) {
    val candidatas by viewModel.candidatas.collectAsStateWithLifecycle()
    val error by viewModel.error.collectAsStateWithLifecycle()
    var busqueda by rememberSaveable { mutableStateOf("") }

    val filtradas = if (busqueda.isBlank()) candidatas else candidatas.filter {
        it.nombre.contains(busqueda.trim(), ignoreCase = true) || it.area.contains(busqueda.trim(), ignoreCase = true)
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            BarraSuperior(titulo = "Candidatas") {
                IconButton(onClick = onRanking) {
                    Icon(Icons.Filled.Leaderboard, contentDescription = "Ranking")
                }
                IconButton(onClick = viewModel::refrescar) {
                    Icon(Icons.Filled.Refresh, contentDescription = "Actualizar")
                }
                IconButton(onClick = onCerrarSesion) {
                    Icon(Icons.AutoMirrored.Filled.Logout, contentDescription = "Cerrar sesión")
                }
            }
        },
        floatingActionButton = {
            if (sesion.rol == Rol.EVALUADORA) {
                ExtendedFloatingActionButton(
                    onClick = onNueva,
                    icon = { Icon(Icons.Filled.PersonAdd, contentDescription = null) },
                    text = { Text("Nueva candidata") },
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary
                )
            }
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(padding),
            contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 20.dp, bottom = 96.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            item {
                Encabezado(sesion)
            }
            item {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Indicador(
                        candidatas.size.toString(), "Total",
                        MaterialTheme.colorScheme.primary, Modifier.weight(1f)
                    )
                    Indicador(
                        candidatas.count { it.estado == EstadoCandidatura.POSTULADA || it.estado == EstadoCandidatura.EN_EVALUACION }.toString(),
                        "En curso", EstadoAzul, Modifier.weight(1f)
                    )
                    Indicador(
                        candidatas.count { it.estado == EstadoCandidatura.EVALUADA }.toString(),
                        "Evaluadas", EstadoAmbar, Modifier.weight(1f)
                    )
                    Indicador(
                        candidatas.count { it.estado == EstadoCandidatura.SELECCIONADA }.toString(),
                        "Elegidas", EstadoVerde, Modifier.weight(1f)
                    )
                }
            }
            item {
                OutlinedTextField(
                    value = busqueda,
                    onValueChange = { busqueda = it },
                    placeholder = { Text("Buscar por nombre o área") },
                    leadingIcon = { Icon(Icons.Filled.Search, contentDescription = null) },
                    trailingIcon = {
                        if (busqueda.isNotEmpty()) {
                            IconButton(onClick = { busqueda = "" }) {
                                Icon(Icons.Filled.Close, contentDescription = "Limpiar búsqueda")
                            }
                        }
                    },
                    singleLine = true,
                    shape = MaterialTheme.shapes.medium,
                    colors = OutlinedTextFieldDefaults.colors(
                        unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                        focusedContainerColor = MaterialTheme.colorScheme.surface,
                        unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant
                    ),
                    modifier = Modifier.fillMaxWidth().padding(top = 4.dp)
                )
            }
            error?.let { mensaje ->
                item {
                    Text(
                        mensaje,
                        color = MaterialTheme.colorScheme.onErrorContainer,
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(MaterialTheme.shapes.small)
                            .background(MaterialTheme.colorScheme.errorContainer)
                            .padding(12.dp)
                    )
                }
            }
            item {
                Text(
                    if (busqueda.isBlank()) "CANDIDATURAS (${candidatas.size})" else "RESULTADOS (${filtradas.size})",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }
            if (filtradas.isEmpty()) {
                item { EstadoVacio(hayBusqueda = busqueda.isNotBlank()) }
            }
            items(filtradas, key = { it.id }) { c ->
                TarjetaCandidata(c) { onDetalle(c.id) }
            }
        }
    }
}

@Composable
private fun Encabezado(sesion: Sesion) {
    Column {
        Text(
            "Hola, ${sesion.nombre.substringBefore(' ')}",
            style = MaterialTheme.typography.headlineSmall
        )
        Spacer(Modifier.height(2.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                Modifier
                    .size(8.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.secondary)
            )
            Spacer(Modifier.width(6.dp))
            Text(
                "${sesion.rol.etiqueta} · Proceso de selección 2026",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TarjetaCandidata(c: Candidata, onClick: () -> Unit) {
    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.large,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Row(Modifier.padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
            Avatar(c.nombre)
            Spacer(Modifier.width(12.dp))
            Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(2.dp)) {
                Text(c.nombre, style = MaterialTheme.typography.titleMedium, maxLines = 1, overflow = TextOverflow.Ellipsis)
                Text(
                    c.area,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(top = 6.dp)) {
                    EstadoChip(c.estado)
                    Spacer(Modifier.width(10.dp))
                    Icon(
                        Icons.Filled.WorkOutline,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(Modifier.width(4.dp))
                    Text(
                        "${c.aniosExperiencia} años",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            Icon(
                Icons.AutoMirrored.Filled.KeyboardArrowRight,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.outline
            )
        }
    }
}

@Composable
private fun EstadoVacio(hayBusqueda: Boolean) {
    Column(
        Modifier.fillMaxWidth().padding(vertical = 40.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            Modifier
                .size(64.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.primaryContainer),
            contentAlignment = Alignment.Center
        ) {
            Icon(Icons.Filled.PersonSearch, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
        }
        Spacer(Modifier.height(12.dp))
        Text(
            if (hayBusqueda) "Sin coincidencias" else "Aún no hay candidaturas",
            style = MaterialTheme.typography.titleMedium
        )
        Text(
            if (hayBusqueda) "Prueba con otro nombre o área." else "Toca Actualizar para traerlas del servidor.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}
