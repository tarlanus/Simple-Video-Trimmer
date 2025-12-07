package com.tarlanus.simplevideotrimmer.presentation.OutPut.viewmodels

import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.ExoPlayer
import com.tarlanus.simplevideotrimmer.domain.UseCaseGetPath
import com.tarlanus.simplevideotrimmer.domain.UseCaseMediaMetadataRetrieve
import com.tarlanus.simplevideotrimmer.domain.exoplayer.PlayerBuilderInterface
import com.tarlanus.simplevideotrimmer.presentation.Main.viewstates.ViewStateMain
import com.tarlanus.simplevideotrimmer.presentation.exocontroller.ViewStateExo
import com.tarlanus.simplevideotrimmer.utils.Resource

import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

@HiltViewModel
class ViewModelOutput @Inject constructor(
    private val useCasePath: UseCaseGetPath,
    @ApplicationContext private val context: Context,
    private val useCaseMedia: UseCaseMediaMetadataRetrieve,
    private val playerBuilderInterface: PlayerBuilderInterface
) : ViewModel() {


    private val _viewStateUI: MutableStateFlow<ViewStateMain> = MutableStateFlow(ViewStateMain.IDLE)
    val viewStateUI: StateFlow<ViewStateMain> get() = _viewStateUI.asStateFlow()
    private val _viewStateExo: MutableStateFlow<ViewStateExo> =
        MutableStateFlow(ViewStateExo.IDLE)
    val viewStateExo: StateFlow<ViewStateExo> get() = _viewStateExo.asStateFlow()
    private var exoplayer : ExoPlayer? = null

    private var mediaJob: Job? = null
    private var mediaPath: String? = null


    fun retrieveMediaMetaData(getRealPath: String) {
        clearMediaJob()
        mediaJob = useCaseMedia.executeRetrieveMetadata(getRealPath).onEach {

            val getData = it

            when (getData) {
                is Resource.ERROR -> {
                    _viewStateUI.value = ViewStateMain.ERROR

                }

                is Resource.LOADING -> {
                    _viewStateUI.value = ViewStateMain.LOADING
                }

                is Resource.SUCCESS -> {
                    val path = getRealPath
                    val mediaData = getData.data
                    if (mediaData != null) {

                        _viewStateUI.value = ViewStateMain.SUCCESS(mediaData, path)
                        mediaPath = path
                        val mediaItem = MediaItem.fromUri(path)
                        exoplayer?.setMediaItem(mediaItem)
                    }

                }
            }


        }.launchIn(viewModelScope)


    }




    fun setStartBuildExolayer() {
        val getExoplayer = playerBuilderInterface.builderExoPlayer()
        exoplayer = getExoplayer
        _viewStateExo.value = ViewStateExo.EXO(exoplayer)

        val getPath = mediaPath
        if (exoplayer?.mediaItemCount == 0 && getPath != null) {
            val mediaItem = MediaItem.fromUri(getPath)

            exoplayer?.setMediaItem(mediaItem)
        }
    }

    fun stopExoBuildPlayer() {
        exoplayer?.stop()
        exoplayer?.clearMediaItems()
        exoplayer?.release()
        exoplayer = null
        _viewStateExo.value = ViewStateExo.IDLE
    }


    fun clearMediaJob() {
        mediaJob?.cancel()
        _viewStateUI.value = ViewStateMain.IDLE

    }

    override fun onCleared() {
        super.onCleared()
        clearMediaJob()
        stopExoBuildPlayer()


    }
}