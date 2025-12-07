package com.tarlanus.simplevideotrimmer.domain.listener


interface TrimListener {

    fun onProgress(percentage: String)
    fun onerror(error: String)
    fun onCompleted(outputPath: String)
}