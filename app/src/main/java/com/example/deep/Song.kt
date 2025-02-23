package com.example.deep

import android.net.Uri
import androidx.core.net.toUri
import kotlinx.serialization.Serializable

@Serializable
data class Song(
    val title: String,
    val uriPath: String,
    var imageResId: Int?=null
    )
