package com.example.deep

import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.*
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.net.toUri

class MainActivity : ComponentActivity() {
    val viewmodel by viewModels<SongViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val context =this

        var resultLauncher = registerForActivityResult(ActivityResultContracts.OpenDocumentTree()) { result: Uri? ->
            viewmodel.loadSongsFromFolder(result,context)

        //    Log.e("jjj",)
        }
        setContent {
            Column(modifier = Modifier.fillMaxSize().background(Color.LightGray)) {
                Row(modifier=Modifier.fillMaxWidth().wrapContentHeight()){
                    Text(
                        text="CLICK TO LOAD FILES",
                        modifier = Modifier.padding(10.dp).background(color = Color.Blue).height(200.dp).clickable{

                            resultLauncher.launch(null)

                        }

                    )
                    Text(
                        text="CLICK TO ADD",
                        modifier = Modifier.padding(10.dp).background(color = Color.Blue).height(200.dp).clickable{

                            viewmodel.addrandom()
                        })

                }
                Column(){
                    val songlist by viewmodel.songs.collectAsState()

                    SongList(songlist)
                }
            }
        }
    }
}

@Composable
fun SongItem(song: Song) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
    ) {
        Image(
            painter = painterResource(id = song.imageResId),
            contentDescription = song.title,
            modifier = Modifier
                .size(64.dp)
                .clip(CircleShape)
                .border(2.dp, Color.Gray, CircleShape)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Column(
            modifier = Modifier.align(Alignment.CenterVertically)
        ) {
            Text(text = song.title, fontSize = 20.sp, fontWeight = FontWeight.Bold)
            Text(text = song.uriPath.toString(), fontSize = 14.sp, color = Color.Gray)
        }
    }
}

@Composable
fun SongList(songs: List<Song>) {
    LazyColumn {
        items(songs) { song ->
            SongItem(song)
        }
    }
}
@Preview(showBackground = true)
@Composable
fun GreetingPreview() {


}