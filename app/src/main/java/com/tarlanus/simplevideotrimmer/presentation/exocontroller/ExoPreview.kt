package com.tarlanus.simplevideotrimmer.presentation.exocontroller
import androidx.annotation.OptIn
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.compose.PlayerSurface
import androidx.media3.ui.compose.SURFACE_TYPE_TEXTURE_VIEW
import androidx.media3.ui.compose.modifiers.resizeWithContentScale
import androidx.media3.ui.compose.state.rememberPresentationState

@OptIn(UnstableApi::class)
@Composable
fun ExoPreview(player : ExoPlayer) {
    val presentationState = rememberPresentationState(player)
    val scaledModifier = Modifier.resizeWithContentScale(ContentScale.Fit, presentationState.videoSizeDp)
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {

        PlayerSurface(
            player = player,
            surfaceType = SURFACE_TYPE_TEXTURE_VIEW,
            modifier = scaledModifier,
        )

        if (presentationState.coverSurface) {
            Box(Modifier.background(Color.Black))
        }
        PlayPauseButton(player, modifier = Modifier.size(150.dp))
    }

}