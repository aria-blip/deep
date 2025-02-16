package com.example.deep

import android.net.Uri
import androidx.core.net.toUri

data class Song(
    val title: String,
    val uriPath: Uri,
    val imageResId: Int=R.drawable.h
    )
