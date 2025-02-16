package com.example.deep

import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.media.MediaPlayer
import android.os.IBinder
import androidx.core.app.NotificationCompat
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

class SongService : Service() {
    private var mediaPlayer: MediaPlayer? = null
    private var currentIndex = 0

    override fun onCreate() {
        super.onCreate()

        SongRepository.songs.onEach {
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
        val song = SongRepository.songs.value[SongRepository.currentIndex.value]
        if (song != null) {
            mediaPlayer?.release()
            mediaPlayer = MediaPlayer.create(this, song.uriPath)
            mediaPlayer?.start()
            showNotification()
        }
    }

    private fun pause() {
        mediaPlayer?.pause()
        showNotification()
    }

    private fun next() {
        val nextIndex = (currentIndex + 1) % SongRepository.songs.value.size
        SongRepository.currentIndex.value= nextIndex

    }

    private fun prev() {
        val prevIndex = if (currentIndex - 1 < 0) SongRepository.songs.value.size - 1 else currentIndex - 1
        SongRepository.currentIndex.value= prevIndex
    }

    private fun showNotification() {
        val song =  SongRepository.songs.value[SongRepository.currentIndex.value]
        val notification = NotificationCompat.Builder(this, "music_channel")
            .setSmallIcon(R.drawable.idn)
            .setContentTitle(song?.title)
            .setContentText("Playing Music")
            .addAction(R.drawable.prev, "Prev", mediaAction("PREV"))
            .addAction(
                if (mediaPlayer?.isPlaying == true) R.drawable.pause else R.drawable.play,
                if (mediaPlayer?.isPlaying == true) "Pause" else "Play",
                mediaAction(if (mediaPlayer?.isPlaying == true) "PAUSE" else "PLAY")
            )
            .addAction(R.drawable.nexz, "Next", mediaAction("NEXT"))
            .setStyle(androidx.media.app.NotificationCompat.MediaStyle())
            .build()

        startForeground(1, notification)
    }

    private fun mediaAction(action: String): PendingIntent {
        val intent = Intent(this, SongService::class.java).apply { this.action = action }
        return PendingIntent.getService(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)
    }

    override fun onDestroy() {
        mediaPlayer?.release()
        mediaPlayer = null
        stopForeground(true)
    }

    override fun onBind(intent: Intent?): IBinder? = null
}