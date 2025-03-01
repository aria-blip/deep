package com.example.deep

import android.app.Activity
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
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.Toast
import android.widget.VideoView
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.animation.animateColor
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.animateValue
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.keyframes
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
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
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.HorizontalAlignmentLine
import androidx.compose.ui.layout.Placeable
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
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
import androidx.work.Configuration
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkInfo
import androidx.work.WorkManager
import androidx.work.workDataOf
import coil.compose.AsyncImage
import com.google.android.material.shape.TriangleEdgeTreatment


import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
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
        val sharedPreferences = context.getSharedPreferences("storage_prefs", MODE_PRIVATE)
        val savedUri = sharedPreferences.getString("folder_urii", null)

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
        viewmodel.initizialseModel(this@MainActivity)
        val context =this


        var savedUri:Uri="".toUri()
        SongRepository.songs.value=viewmodel.songs.value
        SongRepository.depths.value=viewmodel.depth.value


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
                val sharedPreferences = context.getSharedPreferences("storage_prefs", MODE_PRIVATE)
                sharedPreferences.edit().putString("folder_urii", it.toString()).apply()

                // Update savedUri
                viewmodel.neuestdepthimage.value=it

            }

            //    Log.e("jjj",)
        }

        var resultLauncher3 = registerForActivityResult(ActivityResultContracts.OpenDocument()) { result: Uri? ->

            result?.let {
                contentResolver.takePersistableUriPermission(
                    it,
                    Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_GRANT_WRITE_URI_PERMISSION

                )

                // Update savedUri
                viewmodel.neuestdepthrealimage.value=it

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
                    DepthForm(viewmodel, isvisivible = true, resultLauncher2,resultLauncher3, navController,this@MainActivity)
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
        .fillMaxWidth().fillMaxHeight()
        .background(Color(0xFF121212)), verticalArrangement = Arrangement.SpaceEvenly) {
        var text by remember { mutableStateOf("ENTER TEXT") }
        ShowOverlayDepth(viewmodel, context)

        Row(
            modifier = Modifier.fillMaxWidth().height(90.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {

            TextField(

                value = text,
                onValueChange = { text = it },

                label = {
                    if(text== "ENTER TEXT") {
                        Text(

                            text = text,
                            color = Color.Green,
                            modifier = Modifier.fillMaxWidth().fillMaxHeight(0.9f)
                                .paddingFromBaseline(top = 80.dp),
                            textAlign = TextAlign.Center
                        )
                    }
                    else{
                        text=text.replace("ENTER TEXT", "")
                        Text(

                            text = text,
                            color = Color.Green,
                            modifier = Modifier.fillMaxWidth().fillMaxHeight(0.9f)
                                .paddingFromBaseline(top = 80.dp),
                            textAlign = TextAlign.Center
                        )
                    }
                },

                keyboardOptions = KeyboardOptions.Default.copy(
                    imeAction = ImeAction.Done // Set Enter action to Done
                ),
                keyboardActions = KeyboardActions(
                    onDone = {
                        downloadVideoAsMp3(text, context) // Call function when Enter is pressed
                        text = "" // Clear text after submission
                    }
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFF121212)).height(100.dp), // Background color
                colors = TextFieldDefaults.colors(
                    focusedTextColor = Color.Green, // Green text when focused
                    unfocusedTextColor = Color.Green, // Green text when unfocused
                    focusedContainerColor = Color(0xFF121212), // Dark background when focused
                    unfocusedContainerColor = Color(0xFF121212), // Dark background when unfocused
                    cursorColor = Color.Green, // Green cursor
                    focusedIndicatorColor = Color.Green, // Green underline when foc
                    unfocusedIndicatorColor = Color.Green

                )
            )
        }

        Row(modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight(0.2f).padding(top = 3.dp, bottom = 20.dp)) {
            val depthlist by viewmodel.depth.collectAsState()

            DepthList(depthlist, theres = resultLauncher, onDepthClick = { depthIndex ->
                val thedepth:Depth=depthlist[depthIndex]
                if(thedepth.depth_id !=-1){
                navController.navigate(DepthNav(depthIndex))}
                else{
                    navController.navigate(DepthForm)}
                })
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
fun DepthForm(viewmodel: SongViewModel,isvisivible:Boolean=false, resultLauncher:  ActivityResultLauncher<Array<String>>,imaggeresultLauncher:ActivityResultLauncher<Array<String>>, navigation:NavController, thecon:Context){
    var count by remember { mutableStateOf(isvisivible) }
    val imageofDepth by viewmodel.neuestdepthimage.collectAsState()
    val realimageofDepth by viewmodel.neuestdepthrealimage.collectAsState()
    if(count==true){
        Box(modifier = Modifier
            .fillMaxSize()
            .background(Color.Black) ,  contentAlignment = Alignment.Center   ){

                Card(
                    modifier = Modifier
                        .fillMaxWidth(0.9f).background(Color(0xFF121212))
                        .padding(16.dp),elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
                    ) {
                    Column(
                        modifier = Modifier.padding(6.dp).background(Color(0xFF121212)),
                        horizontalAlignment = Alignment.CenterHorizontally , verticalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("F O R M", color = Color.White, fontSize = 48.sp, fontWeight = FontWeight.Bold)

                        var inputText by remember { mutableStateOf("") }
                        TextField(
                            value = inputText,
                            onValueChange = { inputText = it },
                            label = { Text("ENTER DEPTH NAME ___ ") },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth(),
                                 colors   = TextFieldDefaults.colors(
                                    focusedTextColor = Color.Green, // Green text when focused
                            unfocusedTextColor = Color.Green, // Green text when unfocused
                            focusedContainerColor = Color(0xFF121212), // Dark background when focused
                            unfocusedContainerColor = Color(0xFF121212), // Dark background when unfocused
                            cursorColor = Color.Green, // Green cursor
                            focusedIndicatorColor = Color.Green, // Green underline when foc
                            unfocusedIndicatorColor = Color.Green

                        )
                        )
                        Text( textAlign = TextAlign.Center,text="V I D E O BACKGROUND" , color = Color.Green, fontFamily = FontFamily(
                            Font(R.font.pixel2)
                        ), modifier = Modifier
                            .padding(30.dp)
                            .clickable {
                                resultLauncher.launch(arrayOf("video/*"))
                            })
                        Text( textAlign = TextAlign.Center,text="A D D IDENTITY" ,color = Color.Green, fontFamily = FontFamily(
                            Font(R.font.pixel2)), modifier = Modifier
                            .padding(30.dp)
                            .clickable {
                                imaggeresultLauncher.launch(arrayOf("image/*"))
                            })

                        Text( textAlign = TextAlign.Center,text="F I N I S H " ,color = Color.Green, fontFamily = FontFamily(
                            Font(R.font.pixel2)), modifier = Modifier
                            .padding(30.dp)
                            .clickable {
                                viewmodel.addNewDepth(inputText, imageofDepth,realimageofDepth,thecon)
                                count = false
                                navigation.navigate(Home)

                    })
                    }
                }

            }
        }



}

@Composable
fun ShowOverlayDepth(viewmodel :SongViewModel,thecon:Context) {
    val showit by  viewmodel.showoverlay.collectAsState()
    val songindex by  viewmodel.depthforsongindex.collectAsState()


    Log.e("ksk",showit.toString())
    if(showit){
        Log.e("ksk",showit.toString())

        Box(modifier = Modifier
        .fillMaxSize().clickable {

                viewmodel.showoverlay.value=false

            }
        .background(Color.Black) ,  contentAlignment = Alignment.Center   ){



        Box(modifier = Modifier.fillMaxHeight(0.4f).fillMaxWidth().background(Color(0xFF121212)).clickable {  }){
            Column(modifier = Modifier.matchParentSize(), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
                Text(textAlign = TextAlign.Center, text = "CHOOSE D E P T H", color = Color.Green, fontFamily = FontFamily(
                    Font(R.font.wewe)
                ))

                val depthlist by viewmodel.depth.collectAsState()

                DepthList(
                    depthlist.filter { it.depth_id != -1 }, theres = null, onDepthClick =
                 { depthindex:Int ->
                    if(viewmodel.depth.value[depthindex].song_catalog.any { it.title == viewmodel.songs.value[songindex].title }){
                        viewmodel.showoverlay.value=false

                    }else{
                        viewmodel.depth.value[depthindex].song_catalog += viewmodel.songs.value[songindex]
                        SongRepository.saveData(thecon,viewmodel.songs.value,viewmodel.depth.value)
                        viewmodel.showoverlay.value=false

                    }


                })
            }

            }
        }


    }
}


@OptIn(ExperimentalFoundationApi::class)
@Composable
fun SongItem(song: Song, index: Int,onLongSongClick :(Int) -> Unit, onSongClick: (Int) -> Unit,thesize:Int) {
    val theindex by SongRepository.currentIndex.collectAsState()

    val counter  by SongRepository.counter.collectAsState()


    val boxWidth = 411.dp - 40.dp  // Custom width
    val boxHeight = 100.dp // Custom height
    val xOffset = 40.dp // Offset

    val density = LocalDensity.current

    val boxWidthPx = remember { with(density) { boxWidth.toPx() } }
    val boxHeightPx = remember { with(density) { boxHeight.toPx() } }
    val xOffsetPx = remember { with(density) { xOffset.toPx() } }

    val lineLength = 38f // Adjustable line length

// Infinite animation transition
    val transition = rememberInfiniteTransition(label = "BorderAnimation")

    val animatedProgress = remember(theindex) {
        mutableStateOf(0f)
    }

    LaunchedEffect(theindex) {
        if (theindex == index) {
            animatedProgress.value = 0f // Reset animation progress
        }
    }

    val progress by transition.animateFloat(
        initialValue = animatedProgress.value,
        targetValue = (boxWidthPx * 2) + (boxHeightPx * 2), // Full perimeter
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "MovingBorder"
    )

    if (theindex == index) {
        Box {
            Canvas(
                modifier = Modifier.matchParentSize()
            ) {
                val strokeWidth = 6f

                when {
                    progress < boxWidthPx -> {
                        drawLine(
                            color = Color.Green,
                            start = Offset(progress + xOffsetPx, 0f),
                            end = Offset(progress + xOffsetPx + lineLength, 0f),
                            strokeWidth = strokeWidth
                        )
                    }

                    progress < boxWidthPx + boxHeightPx -> {
                        val move = progress - boxWidthPx
                        drawLine(
                            color = Color.Green,
                            start = Offset(boxWidthPx + xOffsetPx, move),
                            end = Offset(boxWidthPx + xOffsetPx, move + lineLength),
                            strokeWidth = strokeWidth
                        )
                    }

                    progress < (boxWidthPx * 2) + boxHeightPx -> {
                        val move = progress - (boxWidthPx + boxHeightPx)
                        drawLine(
                            color = Color.Green,
                            start = Offset(boxWidthPx - move + xOffsetPx, boxHeightPx),
                            end = Offset(boxWidthPx - move - lineLength + xOffsetPx, boxHeightPx),
                            strokeWidth = strokeWidth
                        )
                    }

                    else -> {
                        val move = progress - ((boxWidthPx * 2) + boxHeightPx)
                        drawLine(
                            color = Color.Green,
                            start = Offset(xOffsetPx, boxHeightPx - move),
                            end = Offset(xOffsetPx, boxHeightPx - move - lineLength),
                            strokeWidth = strokeWidth
                        )
                    }
                }
            }
        }
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .combinedClickable(onLongClick = {

                onLongSongClick(index)
            }, onClick = {

                onSongClick(index) })
    ) {
//        Image(
//            painter = painterResource(id = song.imageResId!!),
//            contentDescription = song.title,
//            modifier = Modifier
//                .size(64.dp)
//                .clip(CircleShape)
//                .border(2.dp, Color.Gray, CircleShape)
//        )
        var color: Color
        if(counter==index){
            color=Color(0,100,0)
        }else
        {
            color=Color.Green
        }
        Text(text = index.toString(), fontSize = 40.sp, fontWeight = FontWeight.Thin, fontFamily =  FontFamily(Font(R.font.theone)), color = color
        )

        Spacer(modifier = Modifier.width(8.dp))
        Column(
            modifier = Modifier.align(Alignment.CenterVertically)
        ) {
            Text(text = song.title, fontSize = 20.sp, fontFamily = FontFamily(Font(R.font.pixel2)), color = Color.Green)
            Text(text = song.uriPath.toString(), fontSize = 13.sp, color = Color.Gray)
        }
    }
}

@Composable
fun SwitchDepth(depth: Depth,index: Int,context: Context,onLongSongClickk: (Int) -> Unit) {

    Box(
        modifier = Modifier
            .fillMaxSize().background(Color.Black)
    ) {
        var isVisible by remember { mutableStateOf(false) }
        val alphaValue by animateFloatAsState(
            targetValue = if (isVisible) 1f else 0f,
            animationSpec = tween(durationMillis = 10000),
            label = "FadeInAnimation"
        )

        LaunchedEffect(Unit) {
            isVisible = true // Trigger fade-in on composition
        }
        AndroidView(
            modifier = Modifier.fillMaxSize().background(Color.Black)
            ,
            factory = { ctx ->
                VideoView(ctx).apply {

                    setVideoURI( depth.image.toUri())
                    alpha=0f

                    setOnPreparedListener { mediaPlayer ->
                        mediaPlayer.isLooping = true   // Loop infinitely
                        mediaPlayer.setVolume(0f, 0f) // Mute the audio
                        start() // Auto-play the video
                        animate().alpha(1f).setDuration(1000).start()

                    }
                    setOnClickListener { /* Disable click actions */ }
                }
            },
        )
        Box(
            modifier = Modifier
                .fillMaxSize()
        ) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                val gradient = Brush.verticalGradient(
                    colors = listOf(
                        Color.Black,
                        Color.Black.copy(alpha = 0.90f),
                        Color.Black.copy(alpha = 0.60f),

                        Color.Transparent,
                        Color.Transparent,
                        Color.Black.copy(alpha = 0.60f),

                        Color.Black.copy(alpha = 0.90f),
                        Color.Black
                    ),
                    startY = 0f,
                    endY = size.height
                )
                drawRect(brush = gradient, size = size)
            }
        }
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
fun DepthIteam(depth: Depth, index: Int, onDepthClick: (Int) -> Unit,theres: ActivityResultLauncher<Uri?>?){
    if(depth.depth_id != -1){
    Box(modifier = Modifier
        .size(160.dp) // Ensures a strict square container
        .padding(horizontal = 5.dp)
        .clickable {
            onDepthClick(index)
        },
        contentAlignment = Alignment.Center
    ) {

            AsyncImage(
                model = depth.realimage.toUri(),
                contentDescription = null,
                contentScale = ContentScale.Crop,

                modifier = Modifier.matchParentSize()
                        .fillMaxSize() // Ensures it covers the entire Box
                    .clip(RoundedCornerShape(7.dp)) // Force square cropping
            )
            Text(textAlign = TextAlign.Center, fontSize = 10.sp, color = Color.Green,
                text=depth.title.toCharArray().joinToString("\n") , fontFamily = FontFamily(Font(R.font.pixel2))

            )


    }
    }
    else{


        Column(modifier = Modifier.size(160.dp).background(Color(0xFF121212)), verticalArrangement = Arrangement.Top, horizontalAlignment = Alignment.CenterHorizontally) {
            Spacer(modifier = Modifier.fillMaxHeight(0.1f))
            Image(
                painter = painterResource(id = R.drawable.kk),
                contentDescription = "Round Image",
                modifier = Modifier
                    .clip(CircleShape)
                    .fillMaxWidth()
                    .fillMaxHeight(0.4f)
                    .clickable {
                        onDepthClick(index)

                    }
            )
            Image(
                painter = painterResource(id = R.drawable.hij),
                contentDescription = "Round Image",
                modifier = Modifier
                    .clip(CircleShape)
                    .fillMaxWidth()
                    .fillMaxHeight(0.9f)
                    .clickable {
                        if (theres != null) {
                            theres.launch(null)
                        }
                    }
            )
        }

    }
}
@Composable
fun DepthList(depths: List<Depth>,onDepthClick: (Int) -> Unit,theres:
ActivityResultLauncher<Uri?>?) {
    LazyRow(modifier = Modifier.paddingFromBaseline(top = 20.dp, bottom = 20.dp)) {
        itemsIndexed(depths) { index,depth   ->
                DepthIteam(
                    depth, index, onDepthClick,
                    theres = theres
                )

        }
    }
}

@Composable
fun SongList(songs: List<Song>,longsongclick:(Int)-> Unit,onSongClick: (Int) -> Unit) {
    val counter  by SongRepository.counter.collectAsState()

    LaunchedEffect(Unit) {
        while (true) { // Infinite loop
            delay(100L) // 200ms delay before incrementing

            val currentCounter = SongRepository.counter.value
            val nextCounter = if (currentCounter >= songs.size) 0 else currentCounter + 1

            SongRepository.counter.value = nextCounter
        }
    }

    val xOffset = 4.dp      // Fixed X position
    val lineHeight = 120.dp   // Green line height
    val moveDistance = 50.dp  // Distance it moves per step

    val density = LocalDensity.current
    val xOffsetPx = with(density) { xOffset.toPx() }
    val lineHeightPx = with(density) { lineHeight.toPx() }

    var parentHeightPx by remember { mutableStateOf(0f) } // Dynamically store height

    val transition = rememberInfiniteTransition(label = "VerticalLineAnimation")

// Animate from top (0) to the bottom of the parent
    val animatedProgress by transition.animateFloat(
        initialValue = 0f,
        targetValue = parentHeightPx, // Moves to the full height of the parent
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1000, easing = LinearEasing), // Smooth infinite movement
            repeatMode = RepeatMode.Restart // Restarts from top when it reaches the bottom
        ),
        label = "MovingBorder"
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .onGloballyPositioned { layoutCoordinates ->
                parentHeightPx = layoutCoordinates.size.height.toFloat() // Capture height dynamically
            }
    ) {
        Canvas(
            modifier = Modifier.matchParentSize()
        ) {
            val strokeWidth = 6f

            // Draw a vertical moving line that loops infinitely
            drawLine(
                color = Color.Green,
                start = Offset(xOffsetPx, animatedProgress),
                end = Offset(xOffsetPx, animatedProgress + lineHeightPx),
                strokeWidth = strokeWidth
            )

            drawLine(
                color = Color.Green,
                start = Offset(xOffsetPx, animatedProgress),
                end = Offset(xOffsetPx, animatedProgress + lineHeightPx),
                strokeWidth = strokeWidth
            )
        }

        LazyColumn {
            itemsIndexed(songs) { index, song ->
                SongItem(song, index, longsongclick, onSongClick, songs.size)
            }
        }
    }

}

@Composable
fun LoadFromFolderButton(theres: ActivityResultLauncher<Uri?>){

    Row(
        modifier = Modifier
            .fillMaxWidth().fillMaxHeight()
            ,
        horizontalArrangement = Arrangement.Center, verticalAlignment = Alignment.Bottom
    ) {
        Box(modifier = Modifier.background(Color(0xFF121212))) {
            // 🔥 Canvas to draw the moving line


            // 🔥 Image centered on top of the line
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    ,
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(id = R.drawable.down),
                    contentDescription = "Round Image",
                    modifier = Modifier
                        .size(100.dp)
                        .clip(CircleShape)
                        .clickable {
                            theres.launch(null)
                        }
                )
            }
        }
    }
}
@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    Column(modifier = Modifier.size(160.dp).background(Color(0xFF121212)), verticalArrangement = Arrangement.Top, horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text ="23", fontSize = 40.sp, fontWeight = FontWeight.Thin, fontFamily =  FontFamily(Font(R.font.wewe)), color = Color.Green)


    }
}