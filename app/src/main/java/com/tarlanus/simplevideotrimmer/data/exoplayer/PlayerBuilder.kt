package com.tarlanus.simplevideotrimmer.data.exoplayer

import android.content.Context
import androidx.annotation.OptIn
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.DefaultRenderersFactory
import androidx.media3.exoplayer.ExoPlayer

class PlayerBuilder {

    @OptIn(UnstableApi::class)
    fun provideExoplayer(context: Context): ExoPlayer {
        val renderersFactory = DefaultRenderersFactory(context).setEnableDecoderFallback(true)
            .experimentalSetMediaCodecAsyncCryptoFlagEnabled(true)
            .forceEnableMediaCodecAsynchronousQueueing()
        val player = ExoPlayer.Builder(context)
            .setRenderersFactory(renderersFactory)
            .build()
        return player

    }

}