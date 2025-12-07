package com.tarlanus.simplevideotrimmer.domain

import kotlinx.serialization.Serializable

@Serializable
data class MediaDetails(
    val mediaDetails: String = "Media details",
    var name: String = "Name: ",
    var size: String = "Size: ",
    var resolution: String = "Resolution: ",
    var path: String = "Path: ",
    var bitrate : String = "Bitrate: ",
    var frameRate : String = "Framerate: ",
    val duration: String = "Duration"

) : java.io.Serializable