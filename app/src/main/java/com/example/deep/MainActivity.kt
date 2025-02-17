package com.example.deep

import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.animation.AnimatedContentScope
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.*
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.net.toUri
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import coil.compose.AsyncImage
import kotlinx.serialization.Serializable
import kotlin.math.absoluteValue

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
            val navController = rememberNavController()

            NavHost(navController, startDestination = Home) {
                composable<Home> {
                    HomeScreen(resultLauncher,viewmodel,packageName,this@MainActivity,navController)
                }
                composable<DepthNav> {
                    val args=it.toRoute<DepthNav>()
                    val newDepth:Depth=Depth(args.title,args.songs,args.index,Uri.parse(args.imageUri))
                    SwitchDepth(newDepth,args.index)
                }

                }
            }

        }
    }

@Serializable
object Home

@Serializable
data class DepthNav(
    val title: String,
    val index: Int,
    val songs: List<Song>,
    val imageUri: String
)


@Composable
fun HomeScreen(resultLauncher: ActivityResultLauncher<Uri?>,viewmodel:SongViewModel,packageName:String,    context: Context,navController: NavController) {

    Column(modifier = Modifier
        .fillMaxSize()
        .background(Color.LightGray), verticalArrangement = Arrangement.SpaceEvenly) {
        Row(modifier=Modifier
            .fillMaxWidth()
            .wrapContentHeight()){
            Text(
                text="CLICK TO LOAD FILES",
                modifier = Modifier
                    .padding(10.dp)
                    .background(color = Color.Blue)
                    .height(200.dp)
                    .clickable {

                        resultLauncher.launch(null)

                    }

            )
            Text(
                text="CLICK TO DEPTH",
                modifier = Modifier
                    .padding(10.dp)
                    .background(color = Color.Blue)
                    .height(200.dp)
                    .clickable {
                        val imageUri = Uri.parse("android.resource://${packageName}/${R.drawable.pian}")
                        viewmodel.depth.value += Depth("Nocturne",viewmodel.songs.value.filterIndexed { index, value -> index % 2 == 0 },1, imageUri)
                        viewmodel.depth.value += Depth("Bes",viewmodel.songs.value.filterIndexed { index, value -> index % 3 == 0 } ,2, imageUri)
                        viewmodel.depth.value += Depth("Sunnya",viewmodel.songs.value.filterIndexed { index, value -> index % 4 == 0 } ,2, imageUri)
                        viewmodel.depth.value += Depth("GOT",viewmodel.songs.value.filterIndexed { index, value -> index % 6 == 0 } ,2, imageUri)

                    })

        }
        Row(modifier = Modifier
            .fillMaxWidth()
            .height(300.dp)) {
            val depthlist by viewmodel.depth.collectAsState()
            var thenum = remember{mutableStateOf(-1) }

            DepthList(depthlist) { depthIndex ->
                val thedepth:Depth=depthlist[depthIndex]
                navController.navigate(DepthNav(thedepth.title,depthIndex,thedepth.song_catalog, imageUri = thedepth.image.toString()))
            }
      //      if(thenum.value!=-1){
        //        SwitchDepth(depthlist[thenum.value],thenum.value)
          //  }
        }
        Column(){
            val songlist by viewmodel.songs.collectAsState()

            SongList(songlist) { clickedIndex ->
                SongRepository.currentIndex.value = clickedIndex
                Intent(context, SongService::class.java).also { intent ->
                    Log.e("jjj","uiisis")

                    intent.action = "PLAY"
                    context.startService(intent)

                }
            }
        }
    }
}

@Composable
fun SongItem(song: Song, index: Int, onSongClick: (Int) -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .clickable {
                onSongClick(index)
            }
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
fun SwitchDepth(depth: Depth,index: Int) {
    Box(
        modifier = Modifier
            .fillMaxSize()
    ) {
        AsyncImage(
            model = depth.image,  // Use your Uri here
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )

        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            Row(modifier = Modifier.fillMaxWidth().height(70.dp).padding(10.dp) , horizontalArrangement = Arrangement.Center) {
                Text(textAlign = TextAlign.Center,
                    text=depth.title
                    )

            }
            Column(){



                SongList(depth.song_catalog) { clickedIndex ->
                    SongRepository.currentIndex.value = clickedIndex
                    Intent(context, SongService::class.java).also { intent ->
                        Log.e("jjj","uiisis")

                        intent.action = "PLAY"
                        context.startService(intent)

                    }
                }
            }

        }
    }
}

@Composable
fun DepthIteam(depth: Depth, index: Int, onDepthClick: (Int) -> Unit){
    Box(modifier = Modifier
        .fillMaxHeight()
        .clickable {
            onDepthClick(index)
        }){

    }
}
@Composable
fun DepthList(depths: List<Depth>,onDepthClick: (Int) -> Unit) {
    LazyRow {
        itemsIndexed(depths) { index,depth   ->
            DepthIteam(depth,index,onDepthClick)
        }
    }
}

@Composable
fun SongList(songs: List<Song>,onSongClick: (Int) -> Unit) {
    LazyColumn {
        itemsIndexed(songs) { index,song   ->
            SongItem(song,index,onSongClick)
        }
    }
}
@Preview(showBackground = true)
@Composable
fun GreetingPreview() {


}