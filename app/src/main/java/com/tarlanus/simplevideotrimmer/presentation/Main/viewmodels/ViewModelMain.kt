package com.tarlanus.simplevideotrimmer.presentation.Main.viewmodels

import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
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
class ViewModelMain @Inject constructor(
    private val useCasePath: UseCaseGetPath,
    @ApplicationContext private val context: Context,
    private val useCaseMedia: UseCaseMediaMetadataRetrieve,
    private val playerBuilderInterface: PlayerBuilderInterface
) : ViewModel() {


    private val _viewStateMain: MutableStateFlow<ViewStateMain> =
        MutableStateFlow(ViewStateMain.IDLE)
    val viewStateMain: StateFlow<ViewStateMain> get() = _viewStateMain.asStateFlow()

    private val _viewStateExo: MutableStateFlow<ViewStateExo> =
        MutableStateFlow(ViewStateExo.IDLE)
    val viewStateExo: StateFlow<ViewStateExo> get() = _viewStateExo.asStateFlow()

    private val _playable : MutableStateFlow<Boolean> = MutableStateFlow(false)
    val playable : StateFlow<Boolean> get() = _playable.asStateFlow()

    private var exoplayer: ExoPlayer? = null

    private var mediaPath: String? = null

    private var mediaJob: Job? = null


    fun retrieveMediaMetaData(videoUri: Uri) {
        clearMediaJob()
        val getRealPath = useCasePath.executeGetPath(context, videoUri)
        if (getRealPath != null) {

            mediaJob = useCaseMedia.executeRetrieveMetadata(getRealPath).onEach {

                val getData = it

                when (getData) {
                    is Resource.ERROR -> {
                        _viewStateMain.value = ViewStateMain.ERROR

                    }

                    is Resource.LOADING -> {
                        _viewStateMain.value = ViewStateMain.LOADING
                    }

                    is Resource.SUCCESS -> {
                        _playable.value = true
                        val path = getRealPath
                        val mediaData = getData.data
                        if (mediaData != null) {
                            _viewStateMain.value =
                                ViewStateMain.SUCCESS(mediaData, path)
                            mediaPath = path
                            val mediaItem = MediaItem.fromUri(path)
                            exoplayer?.setMediaItem(mediaItem)

                        }
                    }
                }
            }.launchIn(viewModelScope)
        }
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

        if (_viewStateMain.value is ViewStateMain.SUCCESS == false)  {
            _viewStateMain.value = ViewStateMain.IDLE

        }

    }
    fun stopTheVideo() {
        mediaJob?.cancel()
        _viewStateMain.value = ViewStateMain.IDLE
        exoplayer?.stop()
        exoplayer?.clearMediaItems()
        _playable.value = false
    }

    override fun onCleared() {
        super.onCleared()
        clearMediaJob()
        stopExoBuildPlayer()


    }



}