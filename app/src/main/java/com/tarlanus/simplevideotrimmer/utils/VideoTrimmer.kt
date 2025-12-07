package com.tarlanus.simplevideotrimmer.utils

import android.content.ContentValues
import android.content.Context
import android.net.Uri
import android.os.Environment
import android.os.Handler
import android.os.Looper
import android.provider.MediaStore
import android.util.Log
import androidx.annotation.OptIn
import androidx.media3.common.Effect
import androidx.media3.common.MediaItem
import androidx.media3.common.MimeTypes
import androidx.media3.common.util.UnstableApi
import androidx.media3.effect.LanczosResample
import androidx.media3.effect.Presentation
import androidx.media3.effect.Presentation.LAYOUT_SCALE_TO_FIT
import androidx.media3.effect.Presentation.LAYOUT_SCALE_TO_FIT_WITH_CROP
import androidx.media3.effect.ScaleAndRotateTransformation
import androidx.media3.transformer.Composition
import androidx.media3.transformer.DefaultEncoderFactory
import androidx.media3.transformer.EditedMediaItem
import androidx.media3.transformer.EditedMediaItemSequence
import androidx.media3.transformer.Effects
import androidx.media3.transformer.ExportException
import androidx.media3.transformer.ExportResult
import androidx.media3.transformer.ProgressHolder
import androidx.media3.transformer.Transformer
import androidx.media3.transformer.VideoEncoderSettings
import com.tarlanus.simplevideotrimmer.domain.MediaDetails
import com.tarlanus.simplevideotrimmer.domain.UseCaseGetPath
import com.tarlanus.simplevideotrimmer.domain.listener.TrimListener


@OptIn(UnstableApi::class)
class VideoTrimmer {

    private var mainHandler: Handler? = null
    private var runnable: Runnable? = null

    fun startTrimVideo(
        theContext: Context,
        start: Long,
        end: Long,
        videoPath: String,
        pathHelper: UseCaseGetPath,
        videoDetails : MediaDetails,
        listener: TrimListener
    ) {
        val getBirate = videoDetails.bitrate.substringAfter("Bitrate: ")

        val getBirateInt =getBirate.toIntOrNull()
        val progressHolder = ProgressHolder()
        mainHandler = Handler(Looper.getMainLooper())

        var getLaustUncompletedUri: Uri? = null

        var setBitrate =200000

        if (getBirateInt != null) {
            if (getBirateInt > 300000 && getBirateInt < 1500000 ) {
                setBitrate =200000
            } else if (getBirateInt < 300000){
                setBitrate =100000
            } else if (getBirateInt > 1500000) {
                setBitrate = 1000000
            } else {
                setBitrate = getBirateInt / 2
            }

        }

        val videoEncoderSettings = VideoEncoderSettings.Builder()
            .setBitrate(setBitrate)
            .build()
        val encoderFactory = DefaultEncoderFactory.Builder(theContext)
            .setRequestedVideoEncoderSettings(videoEncoderSettings)
            .setEnableFallback(true)


            .build()
        val transformerListener: Transformer.Listener =
            object : Transformer.Listener {

                override fun onCompleted(composition: Composition, result: ExportResult) {
                    stop()

                    val uri = getLaustUncompletedUri ?: return

                    val completedValues = ContentValues()
                    completedValues.put(MediaStore.Video.Media.IS_PENDING, 0)
                    theContext.contentResolver.update(uri, completedValues, null, null)

                    val path = pathHelper.executeGetPath(theContext, uri)
                    if (path != null) listener.onCompleted(path)
                }

                override fun onError(
                    composition: Composition, result: ExportResult,
                    exception: ExportException,
                ) {
                    stop()
                    listener.onerror(exception.message ?: "Unknown error")
                }
            }

        val transformer = Transformer.Builder(theContext)
            .addListener(transformerListener)
            .setEncoderFactory(encoderFactory)
            .setAudioMimeType(MimeTypes.AUDIO_AAC)
            .setVideoMimeType(MimeTypes.VIDEO_H264)
            .build()

        val outUri = getFullUri(theContext)
        if (outUri == null) {
            listener.onerror("Output URI is null")
            return
        }

        getLaustUncompletedUri = outUri
        val outPath = pathHelper.executeGetPath(theContext, outUri)
        if (outPath == null) {
            listener.onerror("Output path is null")
            return
        }




        var setHeight = 360
        if (setBitrate <= 210000) {
            setHeight = 240
        }

        if (getBirateInt != null) {
            if (getBirateInt > 8000000) {
                setHeight = 480
            }
        }
        val effects1 = Effects(listOf(), listOf(LanczosResample.scaleToFitWithFlexibleOrientation(1000,setHeight),
            Presentation.createForHeight(setHeight)))







        transformer.start(
            Composition.Builder(
                EditedMediaItemSequence.Builder()
                    .addItem(
                        EditedMediaItem.Builder(
                            MediaItem.fromUri(videoPath)
                                .buildUpon()
                                .setClippingConfiguration(
                                    MediaItem.ClippingConfiguration.Builder()
                                        .setStartPositionMs(start)
                                        .setEndPositionMs(end)
                                        .build()
                                )

                                .build()
                        )
                            .setRemoveAudio(false)
                            .setEffects(effects1)
                            .build()
                    )
                    .build()
            ).build(),
            outPath
        )

        runnable = object : Runnable {
            override fun run() {
                val state = transformer.getProgress(progressHolder)
                if (state != Transformer.PROGRESS_STATE_NOT_STARTED) {
                    listener.onProgress(progressHolder.progress.toString())
                }
                mainHandler?.postDelayed(this, 1000)
            }
        }

        mainHandler?.post(runnable!!)
    }

    fun stop() {
        runnable?.let {
            mainHandler?.removeCallbacks(it)
        }
    }

    fun getFullUri(theContext: Context): Uri? {
        val videoName = System.currentTimeMillis()
        val contentValues = ContentValues().apply {
            put(MediaStore.Video.Media.RELATIVE_PATH, Environment.DIRECTORY_MOVIES + "/Simple Video Trimmer")
            put(MediaStore.Video.Media.MIME_TYPE, MimeTypes.VIDEO_H264)
            put(MediaStore.Video.Media.IS_PENDING, 1)
            put(MediaStore.Video.Media.DISPLAY_NAME, "$videoName.mp4")
        }

        val resolver = theContext.contentResolver
        val uri = resolver.insert(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, contentValues)
        resolver.openOutputStream(uri!!)?.close()
        return uri
    }
}
