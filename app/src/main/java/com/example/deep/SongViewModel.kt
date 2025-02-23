package com.example.deep

import android.app.PendingIntent
import android.content.Context
import android.media.MediaPlayer
import android.net.Uri
import android.util.Log
import androidx.documentfile.provider.DocumentFile
import androidx.lifecycle.ViewModel

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

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
                newsong.imageResId=R.drawable.h

                newlistr.add(newsong)

            }
        }

        songs.value= newlistr


    }
    fun addrandom(){
        songs.value+= Song("dsa","as",R.drawable.idn)

    }


}



