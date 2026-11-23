package com.example.appsemestral.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.appsemestral.R
import com.example.appsemestral.model.EstadoCandidatura
import com.example.appsemestral.ui.theme.EstadoAmbar
import com.example.appsemestral.ui.theme.EstadoAmbarFondo
import com.example.appsemestral.ui.theme.EstadoAzul
import com.example.appsemestral.ui.theme.EstadoAzulFondo
import com.example.appsemestral.ui.theme.EstadoGris
import com.example.appsemestral.ui.theme.EstadoGrisFondo
import com.example.appsemestral.ui.theme.EstadoRojo
import com.example.appsemestral.ui.theme.EstadoRojoFondo
import com.example.appsemestral.ui.theme.EstadoVerde
import com.example.appsemestral.ui.theme.EstadoVerdeFondo
import com.example.appsemestral.ui.theme.Marino
import com.example.appsemestral.ui.theme.MarinoClaro
import com.example.appsemestral.ui.theme.Turquesa
import com.example.appsemestral.ui.theme.Violeta

/** Escudo de WIM 100 (res/drawable/logo_wim100.xml). */
@Composable
fun LogoWim(modifier: Modifier = Modifier, tamano: Dp = 40.dp) {
    Image(
        painter = painterResource(R.drawable.logo_wim100),
        contentDescription = "Logo WIM 100",
        modifier = modifier.size(tamano)
    )
}

/** Logo + nombre del programa, para encabezados. */
@Composable
fun MarcaHorizontal(modifier: Modifier = Modifier, tamanoLogo: Dp = 34.dp) {
    Row(modifier, verticalAlignment = Alignment.CenterVertically) {
        LogoWim(tamano = tamanoLogo)
        Spacer(Modifier.width(10.dp))
        Column {
            Text(
                "WIM 100",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp
            )
            Text(
                "DYM CYBER",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.secondary,
                letterSpacing = 2.sp
            )
        }
    }
}

/**
 * Barra superior blanca con línea inferior. Si hay [onVolver] muestra la flecha;
 * si no, muestra la marca a la izquierda.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BarraSuperior(
    titulo: String,
    subtitulo: String? = null,
    onVolver: (() -> Unit)? = null,
    acciones: @Composable RowScope.() -> Unit = {}
) {
    Column {
        TopAppBar(
            title = {
                if (onVolver == null) {
                    MarcaHorizontal()
                } else {
                    Column {
                        Text(titulo, maxLines = 1, overflow = TextOverflow.Ellipsis, style = MaterialTheme.typography.titleLarge)
                        subtitulo?.let {
                            Text(
                                it,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }
                }
            },
            navigationIcon = {
                if (onVolver != null) {
                    IconButton(onClick = onVolver) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
                    }
                }
            },
            actions = acciones,
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = MaterialTheme.colorScheme.surface,
                titleContentColor = MaterialTheme.colorScheme.onSurface,
                actionIconContentColor = MaterialTheme.colorScheme.onSurfaceVariant,
                navigationIconContentColor = MaterialTheme.colorScheme.onSurface
            )
        )
        HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
    }
}

/** Colores (texto, fondo) de cada estado de la candidatura. */
fun coloresEstado(estado: EstadoCandidatura): Pair<Color, Color> = when (estado) {
    EstadoCandidatura.POSTULADA -> EstadoGris to EstadoGrisFondo
    EstadoCandidatura.EN_EVALUACION -> EstadoAzul to EstadoAzulFondo
    EstadoCandidatura.EVALUADA -> EstadoAmbar to EstadoAmbarFondo
    EstadoCandidatura.SELECCIONADA -> EstadoVerde to EstadoVerdeFondo
    EstadoCandidatura.NO_SELECCIONADA -> EstadoRojo to EstadoRojoFondo
}

/** Píldora de color con el estado de la candidatura. */
@Composable
fun EstadoChip(estado: EstadoCandidatura, modifier: Modifier = Modifier) {
    val (texto, fondo) = coloresEstado(estado)
    Row(
        modifier
            .clip(RoundedCornerShape(50))
            .background(fondo)
            .padding(horizontal = 10.dp, vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(Modifier.size(6.dp).clip(CircleShape).background(texto))
        Spacer(Modifier.width(6.dp))
        Text(estado.etiqueta, style = MaterialTheme.typography.labelMedium, color = texto)
    }
}

private val coloresAvatar = listOf(Marino, MarinoClaro, Turquesa, Violeta)

/** Círculo con las iniciales del nombre; el color depende del nombre para que sea estable. */
@Composable
fun Avatar(nombre: String, modifier: Modifier = Modifier, tamano: Dp = 44.dp) {
    val iniciales = nombre.trim().split(Regex("\\s+"))
        .filter { it.isNotEmpty() }
        .take(2)
        .joinToString("") { it.first().uppercase() }
        .ifEmpty { "?" }
    val color = coloresAvatar[(nombre.hashCode() and Int.MAX_VALUE) % coloresAvatar.size]
    Box(
        modifier.size(tamano).clip(CircleShape).background(color),
        contentAlignment = Alignment.Center
    ) {
        Text(
            iniciales,
            color = Color.White,
            fontWeight = FontWeight.SemiBold,
            fontSize = (tamano.value * 0.36f).sp
        )
    }
}

/** Tarjeta blanca con borde suave y título de sección. */
@Composable
fun Seccion(
    titulo: String?,
    modifier: Modifier = Modifier,
    contenido: @Composable ColumnScope.() -> Unit
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.large,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            titulo?.let {
                Text(
                    it.uppercase(),
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            contenido()
        }
    }
}

/** Fila ícono + etiqueta + valor, para fichas de datos. */
@Composable
fun FilaDato(icono: ImageVector, etiqueta: String, valor: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
            Modifier
                .size(36.dp)
                .clip(MaterialTheme.shapes.small)
                .background(MaterialTheme.colorScheme.primaryContainer),
            contentAlignment = Alignment.Center
        ) {
            Icon(icono, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(18.dp))
        }
        Spacer(Modifier.width(12.dp))
        Column(Modifier.weight(1f)) {
            Text(etiqueta, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Text(valor, style = MaterialTheme.typography.bodyMedium, maxLines = 2, overflow = TextOverflow.Ellipsis)
        }
    }
}

/** Indicador numérico pequeño del panel (por ejemplo, "12 Postuladas"). */
@Composable
fun Indicador(valor: String, etiqueta: String, color: Color, modifier: Modifier = Modifier) {
    Column(
        modifier
            .clip(MaterialTheme.shapes.medium)
            .background(MaterialTheme.colorScheme.surface)
            .border(1.dp, MaterialTheme.colorScheme.outlineVariant, MaterialTheme.shapes.medium)
            .padding(horizontal = 12.dp, vertical = 10.dp)
    ) {
        Text(valor, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold, color = color)
        Text(
            etiqueta,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}
