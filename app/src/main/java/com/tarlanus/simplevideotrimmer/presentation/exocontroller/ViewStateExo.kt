package com.tarlanus.simplevideotrimmer.presentation.exocontroller

import androidx.media3.exoplayer.ExoPlayer

sealed class ViewStateExo {
    object IDLE : ViewStateExo()
    data class EXO(val exoPlayer: ExoPlayer? = null)  : ViewStateExo()
}