package com.example.deep

import android.app.PendingIntent
import android.content.Context
import android.media.MediaPlayer
import android.net.Uri
import android.util.Log
import androidx.collection.emptyObjectList
import androidx.core.net.toUri
import androidx.documentfile.provider.DocumentFile
import androidx.lifecycle.ViewModel

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.last
import kotlinx.coroutines.launch

class SongViewModel : ViewModel() {

    val songs = SongRepository.songs
    val depth =SongRepository.depths
    var neuestdepthimage  =  MutableStateFlow("".toUri())
    var neuestdepthrealimage  =  MutableStateFlow("".toUri())


    var showoverlay= MutableStateFlow(false)
    var depthforsongindex =MutableStateFlow(0)

    fun initizialseModel(con:Context){
        var returned = SongRepository.returnData(con)
        songs.value=returned.thesong
        depth.value=returned.thedepth
    }


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

        songs.value+= newlistr
        SongRepository.saveData(context,songs.value,depth.value)

    }
    fun addNewDepth(titel:String,imageUri:Uri,realimageUri: Uri, thecon:Context){
        depth.value= depth.value.filter{ it.depth_id != -1 } + Depth(titel, song_catalog = emptyList(), depth_id = (depth.value[depth.value.lastIndex].depth_id+1),imageUri.toString(),realimageUri.toString()) + Depth("ADD DEPTH", song_catalog =  emptyList(), depth_id = -1 , "sss".toUri().toString()  ,"" )
        SongRepository.saveData(thecon,songs.value,depth.value)

    }

    fun addrandom(){
        songs.value+= Song("dsa","as",R.drawable.idn)

    }


}



