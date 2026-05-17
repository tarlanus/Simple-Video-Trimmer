package com.tarlanus.simplevideotrimmer.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext


@Immutable
data class CustomAccentColors(
    val accentBackground: Color = Color.Unspecified,
    val accentButton: Color = Color.Unspecified,
    val accentText: Color = Color.Unspecified

)

private val DarkAccentColors = CustomAccentColors(
    accentBackground = accentDarkBackground,
    accentButton = accentDarkButton,
    accentText = accentDarkText
)

private val LightAccentColors =CustomAccentColors(
    accentBackground = accentLightBackground,
    accentButton = accentLightButton,
    accentText = accentLightText
)

private val DarkColorScheme = darkColorScheme(
    primary = Purple80,
    secondary = PurpleGrey80,
    tertiary = Pink80
)

private val LightColorScheme = lightColorScheme(
    primary = Purple40,
    secondary = PurpleGrey40,
    tertiary = Pink40
)
val LocalCustomAccentColors = staticCompositionLocalOf { CustomAccentColors() }
@Composable
fun SimpleVideoTrimmerTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    val customAccentColors = if (darkTheme) DarkAccentColors else LightAccentColors

    CompositionLocalProvider(LocalCustomAccentColors provides customAccentColors) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = Typography,
            content = content
        )
    }
}