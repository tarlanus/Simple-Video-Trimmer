package com.tarlanus.simplevideotrimmer.domain

import android.media.MediaExtractor
import android.media.MediaFormat
import android.media.MediaMetadataRetriever
import android.util.Log
import com.tarlanus.simplevideotrimmer.utils.Resource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import java.io.File
import java.io.IOException
import javax.inject.Inject

class UseCaseMediaMetadataRetrieve @Inject constructor() {

    fun executeRetrieveMetadata(videoPath: String): Flow<Resource<MediaDetails>> = flow {
        emit(Resource.LOADING())

        val mediaMetadataRetriever = MediaMetadataRetriever()

        try {
            mediaMetadataRetriever.setDataSource(videoPath)

            val getWidth =
                mediaMetadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_WIDTH)
                    .toString()
            val getHeight =
                mediaMetadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_HEIGHT)
                    .toString()
            val getBirate =
                mediaMetadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_BITRATE)
                    .toString()
            val getFrameRate =
                getFrameRate(videoPath)

            val getDuration = mediaMetadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)

            val file = File(videoPath)
            val sizeMb = String.format("%.2f MB", file.length() / (1024f * 1024f))

            val details = MediaDetails(
                name = "Name: ${file.name}",
                size = "Size: $sizeMb",
                resolution = "Resolution: ${getWidth}x$getHeight",
                path = "Path: $videoPath",
                bitrate = "Bitrate: $getBirate",
                frameRate = "Framerate: $getFrameRate",
                duration = "Duration: $getDuration"
            )
            emit(Resource.SUCCESS(details))

        } catch (it: Exception) {
            emit(Resource.ERROR(it.message.toString()))

        }




    }.catch {
        emit(Resource.ERROR(it.message.toString()))

    }.flowOn(Dispatchers.Default)

    fun getFrameRate(videoPath: String?): Int? {
        if (videoPath == null) return null

        val extractor = MediaExtractor()
        var frameRate: Int? = null
        try {
            extractor.setDataSource(videoPath)
            val numTracks = extractor.trackCount
            for (i in 0 until numTracks) {
                val format = extractor.getTrackFormat(i)
                val mime = format.getString(MediaFormat.KEY_MIME)
                if (mime!!.startsWith("video/")) {
                    if (format.containsKey(MediaFormat.KEY_FRAME_RATE)) {
                        frameRate = format.getInteger(MediaFormat.KEY_FRAME_RATE)
                    }
                }
            }
        } catch (e: IOException) {
            e.printStackTrace()
            frameRate = null
        } finally {
            extractor.release()
        }
        return frameRate
    }

}