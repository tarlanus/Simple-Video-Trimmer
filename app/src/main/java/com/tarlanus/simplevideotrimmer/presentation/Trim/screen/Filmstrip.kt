package com.tarlanus.simplevideotrimmer.presentation.Trim.screen

import android.media.MediaMetadataRetriever
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.RangeSlider
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import com.tarlanus.simplevideotrimmer.ui.theme.LocalCustomAccentColors
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.withContext
import java.io.IOException

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun Filmstrip(path: String, duration: Long, selection: ClosedFloatingPointRange<Float>,
    onChange: (ClosedFloatingPointRange<Float>) -> Unit) {
    val frameCount = 8
    val frames by produceState<List<ImageBitmap?>>(emptyList(), path, duration) {
        value = withContext(Dispatchers.IO) {
            val retriever = MediaMetadataRetriever()
            try {
                retriever.use {
                    it.setDataSource(path)
                    List(frameCount) { index ->
                        currentCoroutineContext().ensureActive()
                        val timeUs = duration * 1000 * (2 * index + 1) / (2 * frameCount)
                        it.getScaledFrameAtTime(timeUs, MediaMetadataRetriever.OPTION_CLOSEST_SYNC, 144, 80)?.asImageBitmap()
                    }
                }
            } catch (cancelled: CancellationException) {
                throw cancelled
            } catch (_: RuntimeException) {
                emptyList()
            } catch (_: IOException) {
                emptyList()

            }
        }
    }
    val accent = LocalCustomAccentColors.current.accentButton
    val seconds = duration / 1000f
    // Page and panel contribute 16 dp each, the final 16 dp makes a 48 dp gesture inset.
    RangeSlider(value = selection, onValueChange = onChange, valueRange = 0f..seconds,
        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
        startThumb = { Grip("Start") }, endThumb = { Grip("End") },
        track = {
            Row(Modifier.fillMaxWidth().height(48.dp).clip(RoundedCornerShape(6.dp))
                .background(Color.DarkGray).drawWithContent {
                    drawContent()
                    val left = size.width * selection.start / seconds
                    val right = size.width * selection.endInclusive / seconds
                    val dim = Color.Black.copy(alpha = .48f)
                    drawRect(dim, size = Size(left, size.height))
                    drawRect(dim, Offset(right, 0f), Size(size.width - right, size.height))
                    val edge = 3.dp.toPx()
                    drawRect(accent, Offset(left, 0f), Size(right - left, edge))
                    drawRect(accent, Offset(left, size.height - edge), Size(right - left, edge))
                }) {
                frames.forEach { frame ->
                    val cell = Modifier.weight(1f).fillMaxHeight()
                    if (frame == null) Spacer(cell)
                    else Image(frame, null, cell, contentScale = ContentScale.Crop)
                }
            }
        })
}

@Composable
private fun Grip(label: String) {
    val colors = LocalCustomAccentColors.current
    Box(Modifier.size(16.dp, 54.dp).semantics { contentDescription = label }
        .background(colors.accentButton, RoundedCornerShape(6.dp)), contentAlignment = Alignment.Center) {
        Spacer(Modifier.size(2.dp, 16.dp).background(colors.accentText))
    }
}
