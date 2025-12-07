package com.tarlanus.simplevideotrimmer.data.exoplayer

import android.content.Context
import androidx.media3.exoplayer.ExoPlayer
import com.tarlanus.simplevideotrimmer.domain.exoplayer.PlayerBuilderInterface

class PlayerBuilderImpl(private val getPlayer: PlayerBuilder, private val  context: Context) :
    PlayerBuilderInterface {
    override fun builderExoPlayer(): ExoPlayer {
        return getPlayer.provideExoplayer(context)
    }
}