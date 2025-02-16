package com.example.deep

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

object SongRepository {
    val songs: MutableStateFlow<List<Song>> = MutableStateFlow<List<Song>>(emptyList())

    private val _currentIndex = MutableStateFlow(0)
    var currentIndex: MutableStateFlow<Int> = _currentIndex


}