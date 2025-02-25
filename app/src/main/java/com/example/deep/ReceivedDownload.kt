package com.example.deep

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.core.net.toUri
import androidx.documentfile.provider.DocumentFile
import java.io.File

class ReceivedDownload : ComponentActivity()  {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.e("ttt","RECEIVED ")

        var resultLauncher2 = registerForActivityResult(ActivityResultContracts.OpenDocumentTree()) { result: Uri? ->

            result?.let {
                contentResolver.takePersistableUriPermission(
                    it,
                    Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_GRANT_WRITE_URI_PERMISSION

                )
                val sharedPreferences = this.getSharedPreferences("storage_prefs", Context.MODE_PRIVATE)
                sharedPreferences.edit().putString("folder_uri", it.toString()).apply()

                // Update savedUri

            }

            //    Log.e("jjj",)
        }

        val message = intent.getStringExtra("TITLE") ?: "No message received"
        val path = intent.getStringExtra("PATH") ?: "No message received"

        var fol=getFolderDocumentFile(this,resultLauncher2)
        var urooffile= fol?.let { getfilesuriinfile(it,path+".mp3") }


        SongRepository.songs.value= SongRepository.songs.value + Song(message,urooffile.toString())

        val intent = Intent(this, MainActivity::class.java).apply {
            putExtra("TITLE", "Hello World")
            putExtra("PATH", "/storage/emulated/0/Music/song.mp3")
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        startActivity(intent)

        setContent {

        }



    }
    fun getFolderDocumentFile(context: Context, resultLauncher: ActivityResultLauncher<Uri?>): DocumentFile? {
        val sharedPreferences = context.getSharedPreferences("storage_prefs", Context.MODE_PRIVATE)
        val savedUri = sharedPreferences.getString("folder_uri", null)

        return if (savedUri != null) {
            DocumentFile.fromTreeUri(context, Uri.parse(savedUri))
        } else {
            // Ask the user to select a folder
            resultLauncher.launch(null)
            null
        }
    }
    fun getfilesuriinfile(doc: DocumentFile , title:String):Uri{
        doc.listFiles()?.forEach { file ->
            Log.e("File Found", file.uri.toString())
            Log.e("File Found", file.name.toString())

            if (file.name == title) {
                Log.e("File Found", file.uri.toString())
                return file.uri

            }
        }
        return "null".toUri()
    }



}
