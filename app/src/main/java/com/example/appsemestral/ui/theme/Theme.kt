package com.example.appsemestral.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

private val EsquemaClaro = lightColorScheme(
    primary = Marino,
    onPrimary = Color.White,
    primaryContainer = Color(0xFFDCE6F7),
    onPrimaryContainer = MarinoProfundo,
    secondary = Turquesa,
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFD3F4F1),
    onSecondaryContainer = Color(0xFF00413D),
    tertiary = Violeta,
    onTertiary = Color.White,
    tertiaryContainer = Color(0xFFEAE3FF),
    onTertiaryContainer = Color(0xFF2A1468),
    background = Fondo,
    onBackground = TextoPrincipal,
    surface = Superficie,
    onSurface = TextoPrincipal,
    surfaceVariant = SuperficieVariante,
    onSurfaceVariant = TextoSecundario,
    surfaceContainerLowest = Color.White,
    surfaceContainerLow = Color(0xFFF9FAFC),
    surfaceContainer = Color(0xFFF1F4F8),
    surfaceContainerHigh = Color(0xFFEBEFF5),
    surfaceContainerHighest = SuperficieVariante,
    outline = Borde,
    outlineVariant = Color(0xFFE3E8EF),
    error = ErrorMarca,
    onError = Color.White,
    errorContainer = EstadoRojoFondo,
    onErrorContainer = EstadoRojo
)

private val EsquemaOscuro = darkColorScheme(
    primary = Color(0xFFA9C4F0),
    onPrimary = MarinoProfundo,
    primaryContainer = MarinoClaro,
    onPrimaryContainer = Color(0xFFDCE6F7),
    secondary = TurquesaClaro,
    onSecondary = Color(0xFF00332F),
    secondaryContainer = Color(0xFF005D57),
    onSecondaryContainer = Color(0xFFD3F4F1),
    tertiary = VioletaClaro,
    onTertiary = Color(0xFF2A1468),
    tertiaryContainer = Color(0xFF4A2FB0),
    onTertiaryContainer = Color(0xFFEAE3FF),
    background = FondoOscuro,
    onBackground = Color(0xFFE4E9F1),
    surface = SuperficieOscura,
    onSurface = Color(0xFFE4E9F1),
    surfaceVariant = SuperficieVarianteOscura,
    onSurfaceVariant = Color(0xFFA9B4C4),
    surfaceContainerLowest = FondoOscuro,
    surfaceContainerLow = Color(0xFF0F1A2A),
    surfaceContainer = SuperficieOscura,
    surfaceContainerHigh = Color(0xFF172438),
    surfaceContainerHighest = SuperficieVarianteOscura,
    outline = Color(0xFF3A4A62),
    outlineVariant = Color(0xFF26344A),
    error = Color(0xFFFFB3B5)
)

val FormasMarca = Shapes(
    extraSmall = RoundedCornerShape(6.dp),
    small = RoundedCornerShape(10.dp),
    medium = RoundedCornerShape(14.dp),
    large = RoundedCornerShape(20.dp),
    extraLarge = RoundedCornerShape(28.dp)
)

/**
 * Tema de marca WIM 100. No usa colores dinámicos (Material You) para que la app
 * se vea igual en todos los teléfonos, con los colores corporativos.
 */
@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = if (darkTheme) EsquemaOscuro else EsquemaClaro,
        typography = Typography,
        shapes = FormasMarca,
        content = content
    )
}
