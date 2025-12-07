package com.tarlanus.simplevideotrimmer.presentation.Screens
import android.os.Parcelable
import androidx.navigation3.runtime.NavKey
import com.tarlanus.simplevideotrimmer.domain.MediaDetails
import kotlinx.serialization.Serializable


@Serializable
data object ScreenMain : NavKey
@Serializable
data class ScreenTrim(val inputPath : String, val details: MediaDetails) : NavKey
@Serializable
data class ScreenOutput(val outPath : String) : NavKey