package com.tarlanus.simplevideotrimmer.presentation.Trim.viewstates

sealed class ViewStateProgress {
    data object IDLE: ViewStateProgress()
    data class PROGRESS(val progress : String) : ViewStateProgress()
    data class ERROR(val errorMSG : String) : ViewStateProgress()
    data class SUCCESS(val path : String) : ViewStateProgress()

}