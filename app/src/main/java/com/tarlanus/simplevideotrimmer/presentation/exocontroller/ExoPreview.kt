package com.tarlanus.simplevideotrimmer.presentation.exocontroller
import androidx.compose.foundation.border
import androidx.compose.foundation.shape.CircleShape
import androidx.annotation.OptIn
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.draw.clip
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
    val videoSize = presentationState.videoSizeDp
    val aspect = if (videoSize != null && videoSize.width > 0f && videoSize.height > 0f) {
        videoSize.width / videoSize.height
    } else {
        16f / 9f
    }
    Box(modifier = Modifier.aspectRatio(aspect).clip(RoundedCornerShape(8.dp)).background(Color.Black), contentAlignment = Alignment.Center) {

        PlayerSurface(
            player = player,
            surfaceType = SURFACE_TYPE_TEXTURE_VIEW,
            modifier = Modifier.fillMaxSize(),
        )

        if (presentationState.coverSurface) {
            Box(Modifier.fillMaxSize().background(Color.Black))
        }
        PlayPauseButton(player, modifier = Modifier.size(52.dp).background(Color.Black.copy(alpha = .60f), CircleShape).border(1.dp, Color.White, CircleShape))
    }

}