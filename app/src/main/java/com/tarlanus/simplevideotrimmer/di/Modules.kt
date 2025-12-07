package com.tarlanus.simplevideotrimmer.di
import android.content.Context
import com.tarlanus.simplevideotrimmer.data.exoplayer.PlayerBuilder
import com.tarlanus.simplevideotrimmer.data.exoplayer.PlayerBuilderImpl
import com.tarlanus.simplevideotrimmer.domain.exoplayer.PlayerBuilderInterface
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
@Module
@InstallIn(SingletonComponent::class)
object Modules {

    @Provides
    fun provideExoplayerBuilder(@ApplicationContext context: Context): PlayerBuilderInterface {
        val getPlayer = PlayerBuilder()

        val playerBuilderImpl = PlayerBuilderImpl(getPlayer, context)

        return playerBuilderImpl


    }


}