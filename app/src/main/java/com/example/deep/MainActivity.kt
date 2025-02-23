package com.example.deep

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.util.SparseArray
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.*
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkInfo
import androidx.work.WorkManager
import androidx.work.workDataOf
import coil.compose.AsyncImage


import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.serialization.Serializable
import java.io.File
import java.net.HttpURLConnection
import java.net.URL

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
      //  downloadYouTubeAudio("https://youtu.be/07RlRpNNGmQ?si=cF6PabNk09DINa12",this@MainActivity)
        val youtubeUrl = "https://www.youtube.com/watch?v=4g0CXOMNEJs"
        downloadVideoAsMp3(youtubeUrl)
        setContent {
            val navController = rememberNavController()

            NavHost(navController, startDestination = Home) {
                composable<Home> {
                    SongRepository.iscatalog= MutableStateFlow(false)

                    HomeScreen(resultLauncher,viewmodel,packageName,this@MainActivity,navController)
                }
                composable<DepthNav> {
                    val args=it.toRoute<DepthNav>()
                    var depths:Depth=SongRepository.depths.value[args.index]

                    SongRepository.setServicetoCatlaog(depths.song_catalog)


                    SwitchDepth(depths,args.index,this@MainActivity)
                }

                }
            }

        }
    fun downloadVideoAsMp3(url:String){
        val intent = Intent("com.example.appB.ACTION_RUN_FUNCTION")
        intent.setPackage("com.junkfood.seal.debug") // Ensures it's sent to App B only
        intent.putExtra("EXTRA_MESSAGE", url) // Send the string
        Log.e("ttz","tdtjdjr")
        this.sendBroadcast(intent)
        Log.e("ttz","tdtjdjr")

    }

    }




@Serializable
object Home

@Serializable
data class DepthNav(
    val index: Int
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
                        viewmodel.depth.value = viewmodel.depth.value.toMutableList().apply {
                            Depth("GOT",viewmodel.songs.value.filterIndexed { index, value -> index % 6 == 0 } ,2, imageUri)                        }
                    })

        }
        Row(modifier = Modifier
            .fillMaxWidth()
            .height(300.dp)) {
            val depthlist by viewmodel.depth.collectAsState()

            DepthList(depthlist) { depthIndex ->
                val thedepth:Depth=depthlist[depthIndex]
                navController.navigate(DepthNav(depthIndex))
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
                    SongRepository.iscatalog.value=false

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
            painter = painterResource(id = song.imageResId!!),
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
fun SwitchDepth(depth: Depth,index: Int,context: Context) {
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
        .height(250.dp).width(80.dp)
        .clickable {
            onDepthClick(index)
        },
        contentAlignment = Alignment.Center
    ){
        AsyncImage(
            model = depth.image,
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.matchParentSize()
        )
        Text(textAlign = TextAlign.Center,
            text=depth.title

        )
    }
}
@Composable
fun DepthList(depths: List<Depth>,onDepthClick: (Int) -> Unit) {
    LazyRow() {
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