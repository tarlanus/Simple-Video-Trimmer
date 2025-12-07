package com.tarlanus.simplevideotrimmer.presentation.Trim.viewmodels

import android.net.Uri
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.ExoPlayer
import com.tarlanus.simplevideotrimmer.domain.MediaDetails
import com.tarlanus.simplevideotrimmer.domain.UseCaseMediaMetadataRetrieve
import com.tarlanus.simplevideotrimmer.domain.UseCaseTimeFormatter
import com.tarlanus.simplevideotrimmer.domain.UseCaseTrim
import com.tarlanus.simplevideotrimmer.domain.exoplayer.PlayerBuilderInterface
import com.tarlanus.simplevideotrimmer.presentation.Trim.viewstates.ViewStateProgress
import com.tarlanus.simplevideotrimmer.presentation.Trim.viewstates.ViewStateTrim
import com.tarlanus.simplevideotrimmer.presentation.exocontroller.ViewStateExo
import com.tarlanus.simplevideotrimmer.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import java.io.File
import javax.inject.Inject

@HiltViewModel
class ViewModelTrim @Inject constructor(
    private val useCaseMedia: UseCaseMediaMetadataRetrieve,
    private val playerBuilderInterface: PlayerBuilderInterface,
    private val usegetTimeFormatting: UseCaseTimeFormatter,
    private val useCaseTrim: UseCaseTrim
) : ViewModel() {

    private val _viewStateTrimUI: MutableStateFlow<ViewStateTrim> =
        MutableStateFlow(ViewStateTrim.IDLE)
    val viewStateTrimUI: StateFlow<ViewStateTrim> get() = _viewStateTrimUI.asStateFlow()

    private val _viewStateProgress: MutableStateFlow<ViewStateProgress> =
        MutableStateFlow(ViewStateProgress.IDLE)
    val viewStateProgress: StateFlow<ViewStateProgress> get() = _viewStateProgress.asStateFlow()
    private val _viewStateExo: MutableStateFlow<ViewStateExo> =
        MutableStateFlow(ViewStateExo.IDLE)
    val viewStateExo: StateFlow<ViewStateExo> get() = _viewStateExo.asStateFlow()
    private var exoplayer: ExoPlayer? = null

    private var mediaJob: Job? = null
    private var mediaPath: String? = null
    private var lastStartPosition: Long? = null
    private var lastEndposition: Long? = null




    fun setSeek(start: Long, end: Long, inputPath: String) {
        if (_viewStateProgress.value is ViewStateProgress.PROGRESS) {
            return
        }
        setStartAndEndPosition(start, end)

        viewModelScope.launch {
            exoplayer?.playWhenReady = false
            exoplayer?.stop()
            val urip = Uri.fromFile(File(inputPath))
            delay(75)
            val mediaItemSp = MediaItem.Builder()
                .setUri(urip)

                .setClippingConfiguration(
                    MediaItem.ClippingConfiguration.Builder()
                        .setStartPositionMs(start.toLong())
                        .setEndPositionMs(end.toLong()).build()
                )
                .build()
            exoplayer?.setMediaItem(mediaItemSp)
            delay(75)
            exoplayer?.prepare()
            exoplayer?.playWhenReady = true
            exoplayer?.seekTo(0L)
        }


    }

    fun setStartAndEndPosition(start: Long, end: Long) {
        this@ViewModelTrim.lastStartPosition = start
        this@ViewModelTrim.lastEndposition = end

    }


    fun setVideoInput(getRealPath: String) {
        clearMediaJob()
        mediaJob = useCaseMedia.executeRetrieveMetadata(getRealPath).onEach {

            val getData = it

            when (getData) {
                is Resource.ERROR -> {
                    _viewStateTrimUI.value = ViewStateTrim.ERROR

                }

                is Resource.LOADING -> {
                    _viewStateTrimUI.value = ViewStateTrim.LOADING
                }

                is Resource.SUCCESS -> {
                    val path = getRealPath
                    val mediaData = getData.data
                    if (mediaData != null) {
                        _viewStateTrimUI.value = ViewStateTrim.IDLE

                        mediaPath = path
                        val mediaItem = MediaItem.fromUri(path)
                        exoplayer?.setMediaItem(mediaItem)
                    }

                }
            }


        }.launchIn(viewModelScope)


    }

    fun getTimeFormat(time: Long): String {
        return usegetTimeFormatting.formattime(time)
    }


    fun setStartBuildExolayer() {

        if (_viewStateProgress.value is ViewStateProgress.PROGRESS) {
            return
        }
        val getExoplayer = playerBuilderInterface.builderExoPlayer()
        exoplayer = getExoplayer
        _viewStateExo.value = ViewStateExo.EXO(exoplayer)

        val getPath = mediaPath


        if (exoplayer?.mediaItemCount == 0 && getPath != null) {


            if (exoplayer?.playWhenReady == false) {


                val getLastStart = lastStartPosition
                val getLastEnd = lastEndposition

                if (getLastStart != null && getLastEnd != null) {
                    setSeek(getLastStart, getLastEnd, getPath)
                }
            }
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
        _viewStateTrimUI.value = ViewStateTrim.IDLE
        if (_viewStateProgress.value is ViewStateProgress.PROGRESS) {
            return
        }
        _viewStateProgress.value = ViewStateProgress.IDLE

    }

    override fun onCleared() {
        super.onCleared()
        clearMediaJob()
        stopExoBuildPlayer()


    }

    fun startTrimVideo(videoDetails: MediaDetails) {
        exoplayer?.playWhenReady = false
        exoplayer?.stop()

        val getLastStart = lastStartPosition
        val getLastEnd = lastEndposition
        val getMediaPath = mediaPath

        if (getLastStart != null && getLastEnd != null && getMediaPath != null) {

            useCaseTrim.executeTrimVideo(getLastStart, getLastEnd, getMediaPath, videoDetails).onEach {

                when (it) {
                    is Resource.ERROR -> {
                        _viewStateProgress.value = ViewStateProgress.ERROR(it.error.toString())



                    }

                    is Resource.LOADING -> {
                        _viewStateProgress.value = ViewStateProgress.PROGRESS("Starting ...")

                    }

                    is Resource.SUCCESS -> {
                        val getData = it.data
                        if (getData != null) {
                            if (getData.contains("OutPut")) {
                                val output = getData.removePrefix("OutPut")
                                _viewStateProgress.value = ViewStateProgress.SUCCESS(output)

                            } else {
                                _viewStateProgress.value = ViewStateProgress.PROGRESS(getData)

                            }
                        }




                    }

                }

            }.launchIn(viewModelScope)

        }
    }
}