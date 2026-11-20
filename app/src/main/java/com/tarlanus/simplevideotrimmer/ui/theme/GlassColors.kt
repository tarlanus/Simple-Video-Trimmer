package com.tarlanus.simplevideotrimmer.ui.theme

import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.Color

@Immutable
internal data class GlassColors(
    val shade: Color,
    val base: Color,
    val light: Color,
    val warmLight: Color,
    val frost: Color,
    val edge: Color,
    val buttonHighlight: Color
)

internal val darkGlass = GlassColors(
    shade = Color(0xFF201E20), base = accentDarkBackground,
    light = Color(0xFF778335), warmLight = Color(0xFF71604F),
    frost = Color(0xFFB7A999), edge = Color(0xFFE7E3CC),
    buttonHighlight = Color(0xFF627B09)
)

internal val lightGlass = GlassColors(
    shade = Color(0xFFFFD18B), base = accentLightBackground,
    light = Color(0xFFC1DF82), warmLight = Color(0xFFFFBD85),
    frost = Color(0xFFFFF9E7), edge = Color.White,
    buttonHighlight = Color(0xFFBAFF58)
)
