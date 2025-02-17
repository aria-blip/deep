package com.example.deep

import android.app.Application
import android.content.Context
import android.media.MediaPlayer
import android.net.Uri
import android.util.Log
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.net.toUri
import androidx.documentfile.provider.DocumentFile
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class SongViewModel : ViewModel() {
    private var mediaPlayer: MediaPlayer? = null
    val songs = SongRepository.songs
    val depth =SongRepository.depths
    fun loadSongsFromFolder(uri: Uri?, context: Context) {

        var newlistr= mutableListOf<Song>()
        val data = uri

        val docRoot: DocumentFile? = data?.let { DocumentFile.fromTreeUri(context, it) }
        var urist: Array<DocumentFile>? = docRoot?.listFiles()
        if (urist != null) {
            Log.e("jj",urist.size.toString())
        }
        if (urist != null) {
            for(u in urist){
                var newsong : Song=Song(u.name.toString(),u.uri.toString() )
                newlistr.add(newsong)

            }
        }

        songs.value= newlistr


    }
    fun addrandom(){
        songs.value+= Song("dsa","as")
    }
}