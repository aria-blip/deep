package com.example.deep

import android.content.ActivityNotFoundException
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
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
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
import androidx.core.net.toUri
import androidx.documentfile.provider.DocumentFile
import androidx.lifecycle.ViewModel
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
    fun getfilesuriinfile(doc:DocumentFile){
        doc.listFiles()?.forEach { file ->
            Log.e("File Found", file.uri.toString())
            Log.e("File Found", file.name.toString())

            if (file.name == "If I lose it all... [4K] [ID-wtN6iGIo].mp3") {
                Log.e("File Found", file.uri.toString())

            }
        }
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val context =this
        var savedUri:Uri="".toUri()
        viewmodel.songs.value=SongRepository.songs.value
        var resultLauncher = registerForActivityResult(ActivityResultContracts.OpenDocumentTree()) { result: Uri? ->
            viewmodel.loadSongsFromFolder(result,context)

            result?.let {
                contentResolver.takePersistableUriPermission(
                    it,
                    Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_GRANT_WRITE_URI_PERMISSION

                )

            }

            //    Log.e("jjj",)
        }
        var resultLauncher2 = registerForActivityResult(ActivityResultContracts.OpenDocument()) { result: Uri? ->

            result?.let {
                contentResolver.takePersistableUriPermission(
                    it,
                    Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_GRANT_WRITE_URI_PERMISSION

                )
                val sharedPreferences = context.getSharedPreferences("storage_prefs", Context.MODE_PRIVATE)
                sharedPreferences.edit().putString("folder_uri", it.toString()).apply()

                // Update savedUri
                viewmodel.neuestdepthimage.value=it

            }

            //    Log.e("jjj",)
        }


      //  getFolderDocumentFile(this, resultLauncher2 )

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            if (!Environment.isExternalStorageManager()) {
                val intent = Intent(Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION)
                startActivity(intent)
            }
        }




      //  downloadYouTubeAudio("https://youtu.be/07RlRpNNGmQ?si=cF6PabNk09DINa12",this@MainActivity)
//        var message = intent.getStringExtra("TITLE") ?: "none"
//        val fullpath = intent.getStringExtra("PATH") ?: "none"
//        Log.e("1",message)
//
//        if(message != "none"){
//            if(viewmodel.songs.value.any { it.title == message } == false) {
//                viewmodel.songs.value = viewmodel.songs.value + Song(message, fullpath)
//                Log.e("1", "jjjs")
//            }
//        }
        var longclickfun : (Int) -> Unit = { songNum:Int  ->
            viewmodel.showoverlay.value=true
            viewmodel.depthforsongindex.value=songNum
            Log.e("jsa","keekek")
        }
        setContent {
            val navController = rememberNavController()

            NavHost(navController, startDestination = Home) {
                composable<Home> {
                    SongRepository.iscatalog= MutableStateFlow(false)

                    HomeScreen(resultLauncher,viewmodel,packageName,this@MainActivity,navController, onLongSongClick = longclickfun)
                }
                composable<DepthNav> {
                    val args=it.toRoute<DepthNav>()
                    var depths:Depth=SongRepository.depths.value[args.index]

                    SongRepository.setServicetoCatlaog(depths.song_catalog)


                    SwitchDepth(depths,args.index,this@MainActivity, onLongSongClickk = longclickfun)
                }
                composable<DepthForm> {
                    DepthForm(viewmodel, isvisivible = true, resultLauncher2, navController)
                }
                }
            }

        }
    fun addSongFromIntent(title:String,path_ofsong:String){
        viewmodel.songs.value+=Song(title,path_ofsong)


    }


    }


@Serializable
object DepthForm

@Serializable
object Home

@Serializable
data class DepthNav(
    val index: Int
)

fun downloadVideoAsMp3(url:String,con:Context){
    Log.e("ttt","CLICKED")
    val intent = Intent().apply {
        component = ComponentName("com.junkfood.seal.debug", "com.junkfood.seal.DownloadAcitivity")
        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        putExtra("EXTRA_MESSAGE", url)
    }

    con.startActivity(intent)
    Log.e("ttt","sendd to SLAEDE")


}


@Composable
fun HomeScreen(resultLauncher: ActivityResultLauncher<Uri?>,viewmodel:SongViewModel,packageName:String,    context: Context,navController: NavController,onLongSongClick: (Int) -> Unit) {

    Column(modifier = Modifier
        .fillMaxSize()
        .background(Color.LightGray), verticalArrangement = Arrangement.SpaceEvenly) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            var text by remember { mutableStateOf("") }
            ShowOverlayDepth(viewmodel)

            TextField(
                value = text,
                onValueChange = { text = it },
                label = { Text("Enter text") }
            )
            Text(modifier = Modifier
                .size(20.dp)
                .clickable {
                    downloadVideoAsMp3(text, context)
                }, text = "CLICK TO INSTALL", fontSize = 28.sp)
        }
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
                        val imageUri =
                            Uri.parse("android.resource://${packageName}/${R.drawable.pian}")
                        viewmodel.depth.value += Depth(
                            "Nocturne",
                            viewmodel.songs.value.filterIndexed { index, value -> index % 2 == 0 },
                            1,
                            imageUri
                        )
                        viewmodel.depth.value += Depth(
                            "Bes",
                            viewmodel.songs.value.filterIndexed { index, value -> index % 3 == 0 },
                            2,
                            imageUri)
                        viewmodel.depth.value += Depth(
                            "Sunnya",
                            viewmodel.songs.value.filterIndexed { index, value -> index % 4 == 0 },
                            2,
                            imageUri)
                        viewmodel.depth.value += Depth(
                            "GOT",
                            viewmodel.songs.value.filterIndexed { index, value -> index % 6 == 0 },
                            2,
                            imageUri)
                        viewmodel.depth.value = viewmodel.depth.value.toMutableList().apply {
                            Depth(
                                "GOT",
                                viewmodel.songs.value.filterIndexed { index, value -> index % 6 == 0 },
                                2,
                                imageUri)
                        }
                    })

        }
        Row(modifier = Modifier
            .fillMaxWidth()
            .height(300.dp)) {
            val depthlist by viewmodel.depth.collectAsState()

            DepthList(depthlist) { depthIndex ->
                val thedepth:Depth=depthlist[depthIndex]
                if(thedepth.depth_id !=-1){
                navController.navigate(DepthNav(depthIndex))}
                else{
                    navController.navigate(DepthForm)}
                }
            }
      //      if(thenum.value!=-1){
        //        SwitchDepth(depthlist[thenum.value],thenum.value)
          //  }
        Column(){
            val songlist by viewmodel.songs.collectAsState()

            SongList(songlist, longsongclick = onLongSongClick) { clickedIndex ->
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
fun DepthForm(viewmodel: SongViewModel,isvisivible:Boolean=false, resultLauncher:  ActivityResultLauncher<Array<String>>, navigation:NavController){
    var count by remember { mutableStateOf(isvisivible) }
    val imageofDepth by viewmodel.neuestdepthimage.collectAsState()

    if(count==true){
        Box(modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.5f)) ,  contentAlignment = Alignment.Center   ){
            Box(modifier = Modifier
                .fillMaxWidth(0.7f)
                .fillMaxHeight(0.9f)
                .padding(30.dp)){
                Card(
                    modifier = Modifier
                        .fillMaxWidth(0.9f)
                        .padding(16.dp),
                    shape = RoundedCornerShape(16.dp),
                    elevation = CardDefaults.cardElevation(8.dp)) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally , verticalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Enter Your Text", fontSize = 18.sp, fontWeight = FontWeight.Bold)

                        var inputText by remember { mutableStateOf("") }
                        TextField(
                            value = inputText,
                            onValueChange = { inputText = it },
                            label = { Text("ENTER DEPTH NAME ___ ") },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth()
                        )
                        Text( textAlign = TextAlign.Center,text="Add Image" , modifier = Modifier
                            .padding(30.dp)
                            .clickable {
                                resultLauncher.launch(arrayOf("image/*"))
                            })

                        Text( textAlign = TextAlign.Center,text="FINISH" , modifier = Modifier
                            .padding(30.dp)
                            .clickable {
                                viewmodel.addNewDepth(inputText, imageofDepth)
                                count = false
                                navigation.navigate(Home)

                    })
                    }
                }

            }
        }

    }

}

@Composable
fun ShowOverlayDepth(viewmodel :SongViewModel) {
    val showit by  viewmodel.showoverlay.collectAsState()
    val songindex by  viewmodel.depthforsongindex.collectAsState()


    Log.e("ksk",showit.toString())
    if(showit){
        Log.e("ksk",showit.toString())

        Box(modifier = Modifier
        .fillMaxSize()
        .background(Color.Black.copy(alpha = 0.34f)) ,  contentAlignment = Alignment.Center   ){
        Box(modifier = Modifier.size(height = 200.dp, width = 300.dp).fillMaxWidth().background(Color.LightGray)){
            Column(modifier = Modifier.matchParentSize(), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
                Text(textAlign = TextAlign.Start, text = "CHOOSE DEPTH")

                val depthlist by viewmodel.depth.collectAsState()

                DepthList(depthlist.filter{it.depth_id!=-1}) { depthindex:Int ->
                    viewmodel.depth.value[depthindex].song_catalog += viewmodel.songs.value[songindex]
                    viewmodel.showoverlay.value=false
                }
            }

            }
        }


    }
}


@OptIn(ExperimentalFoundationApi::class)
@Composable
fun SongItem(song: Song, index: Int,onLongSongClick :(Int) -> Unit, onSongClick: (Int) -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .combinedClickable(onLongClick = {
                onLongSongClick(index)
            }, onClick = { onSongClick(index) })
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
fun SwitchDepth(depth: Depth,index: Int,context: Context,onLongSongClickk: (Int) -> Unit) {

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
            Row(modifier = Modifier
                .fillMaxWidth()
                .height(70.dp)
                .padding(10.dp) , horizontalArrangement = Arrangement.Center) {
                Text(textAlign = TextAlign.Center,
                    text=depth.title
                    )

            }
            Column(){



                SongList(depth.song_catalog, longsongclick = onLongSongClickk) { clickedIndex ->
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
        .height(250.dp)
        .width(80.dp)
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
fun SongList(songs: List<Song>,longsongclick:(Int)-> Unit,onSongClick: (Int) -> Unit) {

    LazyColumn {
        itemsIndexed(songs) { index,song   ->
            SongItem(song,index,longsongclick,onSongClick)
        }
    }
}
@Preview(showBackground = true)
@Composable
fun GreetingPreview() {


}