package com.tarlanus.simplevideotrimmer.domain

import java.util.Locale
import javax.inject.Inject

class UseCaseTimeFormatter @Inject constructor() {

    fun formattime(milliseconds: Long): String {
        val totalSeconds = milliseconds / 1000
        val hours = totalSeconds / 3600
        val minutes = (totalSeconds % 3600) / 60
        val seconds = totalSeconds % 60
        val millisecondsFormatted = String.format(Locale.US,"%03d", milliseconds % 1000)

        return String.format(Locale.US,"%02d:%02d:%02d.%s", hours, minutes, seconds, millisecondsFormatted)

    }
}