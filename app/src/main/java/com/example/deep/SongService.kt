package com.example.deep

import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.graphics.BitmapFactory
import android.media.MediaPlayer
import android.net.Uri
import android.os.IBinder
import android.support.v4.media.session.MediaSessionCompat
import android.util.Log
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorMatrix
import androidx.core.app.NotificationCompat
import androidx.core.net.toUri
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

class SongService : Service() {
    private var mediaPlayer: MediaPlayer? = null
    private var currentIndex = 0
    private lateinit var mediaSession: MediaSessionCompat
    var songs: MutableStateFlow<List<Song>> = SongRepository.songs

    override fun onCreate() {
        super.onCreate()
        songs = SongRepository.songs
        Log.e("kk",songs.value.size.toString())
        if(SongRepository.iscatalog.value==true){ songs=SongRepository.catalog_songs}
        Log.e("kk",songs.value.size.toString())

        mediaSession = MediaSessionCompat(this, "MusicService").apply {
            isActive = true  // Makes it appear on the lock screen
        }
        songs.onEach {
            if (it.isNotEmpty()) play()
        }.launchIn(CoroutineScope(Dispatchers.Main))

        SongRepository.currentIndex.onEach {
            currentIndex = it
            play()
        }.launchIn(CoroutineScope(Dispatchers.Main))
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {


        when (intent?.action) {
            "PLAY" -> play()
            "PAUSE" -> pause()
            "NEXT" -> next()
            "PREV" -> prev()
            "STOP" -> stopSelf()
        }
        return START_STICKY
    }

    private fun play() {
         songs = SongRepository.songs
        if(SongRepository.iscatalog.value==true){ songs=SongRepository.catalog_songs}

        val song = songs.value[SongRepository.currentIndex.value]

        if (song != null) {
            mediaPlayer?.release()
            mediaPlayer = MediaPlayer.create(this, Uri.parse(song.uriPath))
            Log.e("s",song.uriPath)
            mediaPlayer?.start()
            showNotification()
        }
    }

    private fun pause() {
        mediaPlayer?.pause()
        showNotification()
    }

    private fun next() {
        val nextIndex = (currentIndex + 1) % songs.value.size
        SongRepository.currentIndex.value= nextIndex

    }

    private fun prev() {
        val prevIndex = if (currentIndex - 1 < 0) songs.value.size - 1 else currentIndex - 1
        SongRepository.currentIndex.value= prevIndex
    }

    private fun showNotification() {

        val song =  songs.value[SongRepository.currentIndex.value]

        // Convert drawable to Bitmap for large icon and background
        val albumArt = BitmapFactory.decodeResource(resources, R.drawable.back)

        val notification = NotificationCompat.Builder(this, "music_channel")
            .setSmallIcon(R.drawable.h)
            .setContentTitle(song?.title ?: "Unknown Title")
            .setContentText("Playing Music")
            .setLargeIcon(albumArt)
            .setStyle(
                androidx.media.app.NotificationCompat.MediaStyle()
                    .setMediaSession(mediaSession.sessionToken) // Connects to MediaSession
                    .setShowActionsInCompactView(0, 1, 2)
            )
            .addAction(R.drawable.prev, "Prev", mediaAction("PREV"))
            .addAction(
                if (mediaPlayer?.isPlaying == true) R.drawable.pause else R.drawable.play,
                if (mediaPlayer?.isPlaying == true) "Pause" else "Play",
                mediaAction(if (mediaPlayer?.isPlaying == true) "PAUSE" else "PLAY")
            )
            .addAction(R.drawable.nexz, "Next", mediaAction("NEXT"))
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .setOngoing(true)
            .build()

        startForeground(1, notification)
    }

    private fun mediaAction(action: String): PendingIntent {
        val intent = Intent(this, SongService::class.java).apply { this.action = action }
        return PendingIntent.getService(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT  or PendingIntent.FLAG_IMMUTABLE)
    }

    override fun onDestroy() {
        mediaPlayer?.release()
        mediaPlayer = null
        stopForeground(true)
    }

    override fun onBind(intent: Intent?): IBinder? = null
}