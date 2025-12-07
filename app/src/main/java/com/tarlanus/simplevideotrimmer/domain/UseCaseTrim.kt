package com.tarlanus.simplevideotrimmer.domain

import android.content.Context
import android.util.Log
import com.tarlanus.simplevideotrimmer.domain.listener.TrimListener
import com.tarlanus.simplevideotrimmer.utils.Resource
import com.tarlanus.simplevideotrimmer.utils.VideoTrimmer


import dagger.hilt.android.qualifiers.ApplicationContext

import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.catch

import javax.inject.Inject


class UseCaseTrim @Inject constructor(
    @ApplicationContext private val theContext: Context,
    private val getPath: UseCaseGetPath
) {


    fun executeTrimVideo(start: Long, end: Long, videoPath: String, videoDetails: MediaDetails): Flow<Resource<String>> =
        callbackFlow {

            trySend(Resource.LOADING())


            val trimmer = VideoTrimmer()



            delay(2000)
            trimmer.startTrimVideo(
                theContext = theContext,
                start,
                end,
                videoPath,
                getPath,
                videoDetails,
                object : TrimListener {
                    override fun onProgress(percentage: String) {

                        trySend(Resource.SUCCESS(data = percentage))

                    }

                    override fun onerror(error: String) {

                        trySend(Resource.ERROR(msg = error))
                        close()


                    }

                    override fun onCompleted(outputPath: String) {
                        val setOutput = "OutPut"
                        trySend(Resource.SUCCESS(data = setOutput + outputPath))
                        close()


                    }

                })


            awaitClose {
                trimmer.stop()
            }


        }.catch {

            emit(Resource.ERROR(it.message.toString()))
        }
}