package com.example.deep

import android.net.Uri
import androidx.core.net.toUri

data class Song(
    val title: String,
    val uriPath: Uri,
    val imageResId: Int=R.drawable.h,
    var song_prev:Song=Song("null","null".toUri()),
    var song_nex:Song=Song("null","null".toUri())

    )
