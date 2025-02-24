package com.example.deep

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp
import androidx.core.content.FileProvider
import java.io.File

class ReceivedDownload : ComponentActivity()  {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.e("ttt","RECEIVED ")

        val message = intent.getStringExtra("TITLE") ?: "No message received"
        val path = intent.getStringExtra("PATH") ?: "No message received"


        SongRepository.songs.value= SongRepository.songs.value + Song(message,path+".mp3")


        val intent = Intent(this, MainActivity::class.java).apply {
            putExtra("TITLE", "Hello World")
            putExtra("PATH", "/storage/emulated/0/Music/song.mp3")
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        startActivity(intent)
        setContent {

        }



    }




}
