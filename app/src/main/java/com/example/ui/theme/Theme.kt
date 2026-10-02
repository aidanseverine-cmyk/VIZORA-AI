package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val VizoraDarkColorScheme = darkColorScheme(
    primary = VizoraCyan,
    onPrimary = Color(0xFF001F24),
    primaryContainer = Color(0xFF00363F),
    onPrimaryContainer = VizoraCyan,
    secondary = VizoraVioletLight,
    onSecondary = Color(0xFF1E0B4B),
    secondaryContainer = Color(0xFF31196B),
    onSecondaryContainer = Color(0xFFEDE9FE),
    tertiary = VizoraMagenta,
    onTertiary = Color.White,
    tertiaryContainer = Color(0xFF501235),
    onTertiaryContainer = Color(0xFFFCE7F3),
    background = VizoraObsidian,
    onBackground = VizoraTextPrimaryDark,
    surface = VizoraSurfaceDark,
    onSurface = VizoraTextPrimaryDark,
    surfaceVariant = VizoraSurfaceVariantDark,
    onSurfaceVariant = VizoraTextSecondaryDark,
    outline = VizoraBorderDark,
    error = VizoraCrimson,
    onError = Color.White
)

private val VizoraLightColorScheme = lightColorScheme(
    primary = VizoraPrimaryLight,
    onPrimary = Color.White,
    primaryContainer = Color(0xFFE0E7FF),
    onPrimaryContainer = Color(0xFF1E1B4B),
    secondary = VizoraSecondaryLight,
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFE0F2FE),
    onSecondaryContainer = Color(0xFF0C4A6E),
    tertiary = VizoraMagenta,
    onTertiary = Color.White,
    tertiaryContainer = Color(0xFFFCE7F3),
    onTertiaryContainer = Color(0xFF831843),
    background = VizoraLightBg,
    onBackground = VizoraTextPrimaryLight,
    surface = VizoraSurfaceLight,
    onSurface = VizoraTextPrimaryLight,
    surfaceVariant = VizoraSurfaceVariantLight,
    onSurfaceVariant = VizoraTextSecondaryLight,
    outline = VizoraBorderLight,
    error = VizoraCrimson,
    onError = Color.White
)

@Composable
fun VizoraTheme(
    darkTheme: Boolean = true,
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) VizoraDarkColorScheme else VizoraLightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = true,
    content: @Composable () -> Unit
) {
    VizoraTheme(darkTheme = darkTheme, content = content)
}
