package com.tarlanus.simplevideotrimmer.presentation.Main.viewstates

import com.tarlanus.simplevideotrimmer.domain.MediaDetails

sealed class ViewStateMain {
    data object LOADING : ViewStateMain()
    data object IDLE : ViewStateMain()
    data object ERROR : ViewStateMain()
    data class SUCCESS(
        val mediaDetails: MediaDetails,
        val mediaPath: String,
    ) : ViewStateMain()

}