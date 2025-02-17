package com.example.deep

import android.net.Uri

data class Depth(
    var title:String,
    var song_catalog:List<Song>,
    var depth_id:Int,
    var image:Uri

)
