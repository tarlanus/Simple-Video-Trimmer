package com.tarlanus.simplevideotrimmer.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.media3.exoplayer.ExoPlayer
import com.tarlanus.simplevideotrimmer.domain.MediaDetails
import com.tarlanus.simplevideotrimmer.domain.UseCaseTimeFormatter
import com.tarlanus.simplevideotrimmer.presentation.Main.viewstates.ViewStateMain
import com.tarlanus.simplevideotrimmer.presentation.exocontroller.ExoPreview
import com.tarlanus.simplevideotrimmer.presentation.exocontroller.ViewStateExo
import com.tarlanus.simplevideotrimmer.ui.theme.LocalCustomAccentColors
import com.tarlanus.simplevideotrimmer.ui.theme.GlassColors
import com.tarlanus.simplevideotrimmer.ui.theme.darkGlass
import com.tarlanus.simplevideotrimmer.ui.theme.lightGlass
import java.util.Locale

@Composable
internal fun VideoPage(
    title: String,
    preview: @Composable () -> Unit,
    onBack: (() -> Unit)? = null,
    topActions: (@Composable () -> Unit)? = null,
    content: @Composable ColumnScope.() -> Unit
) {
    val colors = LocalCustomAccentColors.current
    val glass = if (colors.accentText == Color.White) darkGlass else lightGlass
    Box(Modifier.fillMaxSize().background(colors.accentBackground)) {
        Spacer(Modifier.matchParentSize().atmosphere(glass))
        CompositionLocalProvider(LocalContentColor provides colors.accentText) {
            BoxWithConstraints(
                Modifier.fillMaxSize().safeDrawingPadding()
                    .padding(start = 16.dp, top = 4.dp, end = 16.dp, bottom = 28.dp)
            ) {
                val wide = maxWidth >= 540.dp && maxWidth > maxHeight
                val gap = Arrangement.spacedBy(6.dp)
                Column(Modifier.fillMaxSize(), verticalArrangement = gap) {
                    topActions?.invoke()
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        if (onBack != null) {
                            IconButton(onClick = onBack) {
                                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                            }
                        }
                        Text(title, Modifier.weight(1f), fontFamily = FontFamily.Serif,
                            fontWeight = FontWeight.Bold, fontSize = if (topActions != null) 24.sp else 28.sp,
                            lineHeight = 30.sp, textAlign = if (topActions != null) TextAlign.Center else TextAlign.Start)
                    }
                    if (wide) {
                        Row(Modifier.fillMaxSize(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                            Box(Modifier.weight(1f), contentAlignment = Alignment.TopCenter) { preview() }
                            Column(Modifier.weight(1f).verticalScroll(rememberScrollState()),
                                verticalArrangement = gap, content = content)
                        }
                    } else {
                        BoxWithConstraints(Modifier.fillMaxSize()) {
                            // Measure the controls first. Give all remaining height to the video,
                            // retaining a visible preview when large text makes the details scroll.
                            val minPreview = minOf(120.dp, maxHeight * .28f)
                            val detailsLimit = (maxHeight - minPreview - 6.dp).coerceAtLeast(0.dp)
                            Column(Modifier.fillMaxSize(), verticalArrangement = gap) {
                                Box(Modifier.fillMaxWidth().weight(1f), contentAlignment = Alignment.Center) {
                                    preview()
                                }
                                Column(Modifier.fillMaxWidth().heightIn(max = detailsLimit)
                                    .verticalScroll(rememberScrollState()), verticalArrangement = gap, content = content)
                            }
                        }
                    }
                }
            }
        }
    }
}

// Soft diagonal bands remain visible even on Android versions without RenderEffect.
private fun Modifier.atmosphere(colors: GlassColors) = drawWithCache {
    val base = Brush.linearGradient(listOf(colors.shade, colors.base, colors.shade))
    val bands = Brush.linearGradient(
        0f to Color.Transparent,
        .12f to colors.light.copy(alpha = .12f),
        .20f to colors.light.copy(alpha = .88f),
        .26f to colors.light.copy(alpha = .24f),
        .36f to Color.Transparent,
        .55f to colors.warmLight.copy(alpha = .65f),
        .67f to Color.Transparent,
        .80f to colors.light.copy(alpha = .72f),
        .93f to colors.warmLight.copy(alpha = .42f),
        1f to Color.Transparent,
        start = Offset.Zero, end = Offset(size.width * .78f, size.height * .90f)
    )
    onDrawBehind {
        drawRect(base)
        drawRect(bands)
    }
}

@Composable
internal fun GlassPanel(content: @Composable ColumnScope.() -> Unit) {
    val dark = LocalCustomAccentColors.current.accentText == Color.White
    val glass = if (dark) darkGlass else lightGlass
    val shape = RoundedCornerShape(18.dp)
    val reflection = Brush.linearGradient(
        0f to glass.edge.copy(alpha = .68f), .32f to glass.edge.copy(alpha = .18f),
        .70f to glass.edge.copy(alpha = .25f), 1f to glass.edge.copy(alpha = .72f)
    )
    val surface = Brush.linearGradient(
        0f to glass.frost.copy(alpha = if (dark) .32f else .58f),
        .36f to glass.base.copy(alpha = .24f),
        .58f to glass.light.copy(alpha = if (dark) .30f else .40f),
        .78f to glass.frost.copy(alpha = .22f),
        1f to glass.warmLight.copy(alpha = .35f)
    )
    Column(
        modifier = Modifier.fillMaxWidth()
            .background(surface, shape)
            .border(1.dp, reflection, shape)
            .padding(horizontal = 16.dp, vertical = 10.dp),
        verticalArrangement = Arrangement.spacedBy(3.dp),
        content = content
    )
}

@Composable
internal fun VideoAction(label: String, icon: ImageVector, enabled: Boolean = true, onClick: () -> Unit) {
    val colors = LocalCustomAccentColors.current
    val shape = RoundedCornerShape(12.dp)
    val glass = if (colors.accentText == Color.White) darkGlass else lightGlass
    val finish = if (enabled) Modifier.background(
        Brush.linearGradient(listOf(glass.buttonHighlight, colors.accentButton)), shape
    ).border(1.dp, glass.edge.copy(alpha = .32f), shape) else Modifier
    Button(onClick, Modifier.fillMaxWidth().heightIn(min = 48.dp).then(finish), enabled = enabled,
        shape = shape,
        colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent, contentColor = colors.accentText)) {
        Icon(icon, null)
        Spacer(Modifier.width(8.dp))
        Text(label, fontSize = 17.sp, fontWeight = FontWeight.Bold)
    }
}

@Composable
internal fun VideoFrame(player: ExoPlayer?, status: String? = null) {
    if (player != null && status == null) {
        ExoPreview(player)
        return
    }
    Box(Modifier.aspectRatio(16f / 9f).clip(RoundedCornerShape(8.dp)).background(Color.Black),
        contentAlignment = Alignment.Center) {
        if (status != null) Text(status, color = Color.White)
    }
}

@Composable
internal fun MediaInspection(state: ViewStateMain, exo: ViewStateExo) {
    val selected = state as? ViewStateMain.SUCCESS
    val player = (exo as? ViewStateExo.EXO)?.exoPlayer
    LaunchedEffect(player, selected?.mediaPath) {
        if (selected != null && player != null) {
            player.prepare()
            player.playWhenReady = true
        }
    }
    val status = when (state) {
        ViewStateMain.LOADING -> "Reading video…"
        ViewStateMain.ERROR -> "Unable to read this video"
        ViewStateMain.IDLE -> "No video selected"
        is ViewStateMain.SUCCESS -> null
    }
    VideoFrame(player, status)
}

@Composable
internal fun MetadataPanel(details: MediaDetails) {
    val rows = remember(details) {
        fun value(field: String) = field.substringAfter(": ").takeUnless { it.isBlank() || it == "null" } ?: "—"
        val bitrate = value(details.bitrate).toLongOrNull()
        val duration = value(details.duration).toLongOrNull()
        listOf("Name" to value(details.name), "Size" to value(details.size),
            "Resolution" to value(details.resolution).replace("x", " × "),
            "Bitrate" to (bitrate?.let { String.format(Locale.US, "%.2f Mb/s", it / 1_000_000.0) } ?: "—"),
            "Frame rate" to (value(details.frameRate).toIntOrNull()?.let { "$it fps" } ?: "—"),
            "Duration" to (duration?.let { UseCaseTimeFormatter().formattime(it).removePrefix("00:") } ?: "—"),
            "Path" to value(details.path))
    }
    GlassPanel {
        Text("Media details", fontSize = 21.sp, fontWeight = FontWeight.Bold)
        rows.forEachIndexed { index, (label, value) ->
            if (index > 0) HorizontalDivider(color = LocalContentColor.current.copy(alpha = .18f))
            if (label == "Path") {
                Text(label, fontSize = 14.sp, lineHeight = 18.sp, fontWeight = FontWeight.SemiBold)
                Text(value, Modifier.fillMaxWidth(), fontSize = 14.sp, lineHeight = 18.sp)
            } else {
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(label, Modifier.weight(.36f), fontSize = 14.sp, lineHeight = 18.sp, fontWeight = FontWeight.SemiBold)
                    Text(value, Modifier.weight(.64f), fontSize = 14.sp, lineHeight = 18.sp)
                }
            }
        }
    }
}
