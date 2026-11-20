package com.tarlanus.simplevideotrimmer.presentation.Trim.screen

import androidx.activity.compose.LocalActivity
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ContentCut
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.LifecycleStartEffect
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.tarlanus.simplevideotrimmer.domain.MediaDetails
import com.tarlanus.simplevideotrimmer.presentation.Trim.viewmodels.ViewModelTrim
import com.tarlanus.simplevideotrimmer.presentation.Trim.viewstates.*
import com.tarlanus.simplevideotrimmer.presentation.components.*
import com.tarlanus.simplevideotrimmer.presentation.exocontroller.ViewStateExo
import kotlinx.coroutines.delay
import java.util.Locale
import kotlin.math.roundToLong

@Composable
fun TrimScreen(onClick: (String) -> Unit, inputPath: String, videoDetails: MediaDetails, onBack: () -> Unit,
    viewModelTrim: ViewModelTrim = hiltViewModel<ViewModelTrim, ViewModelTrim.Factory>(
        creationCallback = { it.create(inputPath) }
    )
) {
    val duration = videoDetails.duration.substringAfter(": ").toLongOrNull()?.coerceAtLeast(0L) ?: 0L
    val limit = minOf(duration / 1000f, 59f)
    var start by rememberSaveable(inputPath) { mutableFloatStateOf(0f) }
    var end by rememberSaveable(inputPath) { mutableFloatStateOf(limit) }
    val startMs = (start * 1000).roundToLong()
    val endMs = (end * 1000).roundToLong()
    val ui by viewModelTrim.viewStateTrimUI.collectAsStateWithLifecycle()
    val exo by viewModelTrim.viewStateExo.collectAsStateWithLifecycle()
    val progress by viewModelTrim.viewStateProgress.collectAsStateWithLifecycle()
    LifecycleStartEffect(viewModelTrim) {
        viewModelTrim.setStartBuildExolayer()
        onStopOrDispose { viewModelTrim.stopExoBuildPlayer() }
    }
    LaunchedEffect(startMs, endMs) {
        if (endMs > startMs) viewModelTrim.setSeek(startMs, endMs)
    }
    VideoPage("Trim video", onBack = onBack, preview = {
        val status = when (ui) {
            ViewStateTrim.ERROR -> "Unable to read this video"
            ViewStateTrim.LOADING -> "Reading video…"
            ViewStateTrim.IDLE -> null
        }
        VideoFrame((exo as? ViewStateExo.EXO)?.exoPlayer, status)
    }) {
        GlassPanel {
            Text("Trim range", fontSize = 21.sp, fontWeight = FontWeight.Bold)
            if (duration > 0L) {
                Filmstrip(inputPath, duration, start..end) { range ->
                    if (range.endInclusive - range.start <= limit) {
                        start = range.start
                        end = range.endInclusive
                    } else if (range.start != start) {
                        start = range.start
                        end = start + limit
                    } else {
                        end = range.endInclusive
                        start = end - limit
                    }
                }
            }
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                listOf("Start" to startMs, "End" to endMs).forEach { (label, time) ->
                    Column(Modifier.weight(1f)) {
                        Text(label)
                        Text(viewModelTrim.getTimeFormat(time).removePrefix("00:"), fontWeight = FontWeight.Bold, fontSize = 20.sp)
                    }
                }
            }
            Text(String.format(Locale.US, "%.3f s", (endMs - startMs) / 1000.0),
                Modifier.fillMaxWidth().padding(vertical = 4.dp), textAlign = TextAlign.Center,
                fontSize = 36.sp, lineHeight = 42.sp, fontWeight = FontWeight.Bold)
            VideoAction("Trim", Icons.Default.ContentCut, enabled = endMs > startMs && ui == ViewStateTrim.IDLE && progress == ViewStateProgress.IDLE) {
                viewModelTrim.startTrimVideo(videoDetails)
            }
        }
    }
    when (val result = progress) {
        is ViewStateProgress.SUCCESS -> LaunchedEffect(result.path) { onClick(result.path) }
        is ViewStateProgress.PROGRESS -> DialogScreen(result.progress)
        is ViewStateProgress.ERROR -> {
            val activity = LocalActivity.current
            DialogScreen("Error: " + result.errorMSG)
            LaunchedEffect(result) { delay(3000); activity?.finishAffinity() }
        }
        ViewStateProgress.IDLE -> Unit
    }
}
