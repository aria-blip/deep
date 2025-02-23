package com.example.deep

import android.annotation.SuppressLint
import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.ClipboardManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.ServiceConnection
import android.content.pm.PackageInfo
import android.net.ConnectivityManager
import android.net.Uri

import android.os.Build
import android.os.IBinder
import android.provider.ContactsContract
import android.util.Log
import android.widget.Toast
import androidx.annotation.OptIn
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.offline.DownloadService
import androidx.work.WorkManager

import com.tencent.mmkv.BuildConfig
import com.tencent.mmkv.MMKV
import com.yausername.ffmpeg.FFmpeg
import com.yausername.youtubedl_android.YoutubeDLException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import com.yausername.aria2c.Aria2c
import com.yausername.youtubedl_android.YoutubeDL

class MyApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                "music_channel",
                "Music Player",
                NotificationManager.IMPORTANCE_HIGH
            )

            val manager = getSystemService(NotificationManager::class.java)
            manager.createNotificationChannel(channel)
        }
        MMKV.initialize(this)


        GlobalScope.launch {
            try {
                withContext(Dispatchers.IO) {
                    YoutubeDL.getInstance().init(this@MyApplication)
                    FFmpeg.getInstance().init(this@MyApplication)
                }
            } catch (e: Exception) {
                Toast.makeText(applicationContext, " R.string.init_failed", Toast.LENGTH_LONG)
                    .show()
            }
        }

    }
}





