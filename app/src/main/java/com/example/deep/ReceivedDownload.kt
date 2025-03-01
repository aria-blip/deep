package com.example.deep

import android.content.ContentUris
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
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

class ReceivedDownload : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.e("ttt", "RECEIVED ")

        var resultLauncher2 = registerForActivityResult(ActivityResultContracts.OpenDocumentTree()) { result: Uri? ->
            result?.let {
                contentResolver.takePersistableUriPermission(
                    it,
                    Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_GRANT_WRITE_URI_PERMISSION
                )
                val sharedPreferences = this.getSharedPreferences("storage_prefs", Context.MODE_PRIVATE)
                sharedPreferences.edit().putString("folder_urii", it.toString()).apply()
            }
        }

        val message = intent.getStringExtra("TITLE") ?: "No message received"
        val path = intent.getStringExtra("PATH") ?: "No message received"

        Log.e("ReceivedDownload", "Received PATH: $path")


        SongRepository.saveData(
            this@ReceivedDownload,
            SongRepository.songs.value + Song(message, getUriFromPath(this,path).toString()),
            thedepth = SongRepository.depths.value
        )

        val intent = Intent(this, MainActivity::class.java).apply {
            putExtra("TITLE", "Hello World")
            putExtra("PATH", "/")
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        startActivity(intent)

        setContent { }
    }

    fun getUriFromPath(context: Context, filePath: String): Uri {
        val file = File(filePath)
        return if (file.exists()) {
            val contentResolver = context.contentResolver
            val uri = MediaStore.Files.getContentUri("external")

            val projection = arrayOf(MediaStore.Files.FileColumns._ID)
            val selection = MediaStore.Files.FileColumns.DATA + "=?"
            val selectionArgs = arrayOf(file.absolutePath)

            contentResolver.query(uri, projection, selection, selectionArgs, null)?.use { cursor ->
                if (cursor.moveToFirst()) {
                    val id = cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns._ID))
                    return ContentUris.withAppendedId(uri, id)
                }
            }
            Uri.EMPTY
        } else {
            Log.e("File Error", "File does not exist: $filePath")
            Uri.EMPTY
        }
    }


}
