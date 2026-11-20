package com.tarlanus.simplevideotrimmer.presentation.Trim.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.tarlanus.simplevideotrimmer.presentation.components.GlassPanel
import com.tarlanus.simplevideotrimmer.ui.theme.LocalCustomAccentColors

@Composable
fun DialogScreen(message: String) {
    val colors = LocalCustomAccentColors.current
    Dialog(onDismissRequest = {}) {
        CompositionLocalProvider(LocalContentColor provides colors.accentText) {
            Box(Modifier.widthIn(max = 360.dp).background(colors.accentBackground, RoundedCornerShape(18.dp))) {
                GlassPanel {
                    if (message.startsWith("Error:")) {
                        Text(message)
                    } else {
                        val percent = message.toFloatOrNull()?.coerceIn(0f, 100f)
                        Text("Trimming video", fontWeight = FontWeight.Bold)
                        if (percent == null) {
                            Text(message)
                            LinearProgressIndicator(Modifier.fillMaxWidth(), color = colors.accentButton)
                        } else {
                            Text("${percent.toInt()}%")
                            LinearProgressIndicator(progress = { percent / 100f }, modifier = Modifier.fillMaxWidth(), color = colors.accentButton)
                        }
                    }
                }
            }
        }
    }
}
