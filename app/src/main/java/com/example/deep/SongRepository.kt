package com.example.deep

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

object SongRepository {
    val songs: MutableStateFlow<List<Song>> = MutableStateFlow<List<Song>>(emptyList())
    var depthSongs: MutableStateFlow<List<List<Song>>> = MutableStateFlow(emptyList())
    val depths: MutableStateFlow<List<Depth>> = MutableStateFlow(emptyList())

    private val _currentIndex = MutableStateFlow(0)
    var currentIndex: MutableStateFlow<Int> = _currentIndex
    var iscatalog: MutableStateFlow<Boolean> = MutableStateFlow(false)
    var catalog_songs: MutableStateFlow<List<Song>> = MutableStateFlow<List<Song>>(emptyList())

    fun setServicetoCatlaog(list:List<Song>){
        catalog_songs= MutableStateFlow(list)
        iscatalog=MutableStateFlow(true)


    }

}