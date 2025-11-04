package com.github.purofle.sandauschool.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import com.github.purofle.sandauschool.R
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put
import kotlinx.serialization.json.putJsonObject

class SandauBackgroundService : Service() {
    private val channelId = "test_channel_id"
    private val notificationId = 1
    private lateinit var manager: NotificationManager
    private lateinit var builder: Notification.Builder
    private var job: Job? = null

    private var totalMinutes = 45
    private var courseName = "上课"

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        courseName = intent?.getStringExtra("courseName") ?: courseName
        totalMinutes = intent?.getIntExtra("duration", totalMinutes) ?: totalMinutes
        return START_STICKY
    }

    override fun onCreate() {
        super.onCreate()
        manager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        manager.createNotificationChannel(
            NotificationChannel(
                channelId,
                "test_change_name",
                NotificationManager.IMPORTANCE_DEFAULT
            )
        )

        builder = Notification.Builder(this, channelId)
            .setSmallIcon(R.mipmap.ic_launcher)

        val n = builder.setContentTitle(courseName)
            .setContentText("倒计时开始")
            .build()

        n.extras.putString("miui.focus.param", buildIslandJson(0).toString())
        startForeground(notificationId, n)

        val startTime = System.currentTimeMillis()

        job = CoroutineScope(Dispatchers.Default).launch {
            while (true) {
                val pass = ((System.currentTimeMillis() - startTime) / 60000).toInt()
                val progress = (pass * 100 / totalMinutes).coerceIn(0, 100)
                updateProgress(progress, totalMinutes - pass)
                if (pass >= totalMinutes) break
                delay(60_000)
            }
            stopSelf()
        }
    }

    private fun buildIslandJson(progress: Int) = buildJsonObject {
        putJsonObject("param_v2") {
            putJsonObject("baseInfo") {
                put("type", 5)
                put("title", courseName)
                put("content", "倒计时")
                put("specialTitle", courseName.take(1))
            }
            putJsonObject("progressInfo") {
                put("progress", progress)
                put("colorProgress", "#FF8514")
                put("colorProgressEnd", "#FF8514")
            }
        }
    }


    private fun updateProgress(p: Int, remain: Int) {
        val n = builder
            .setContentTitle(courseName)
            .setContentText("剩余 $remain 分钟")
            .build()

        n.extras.putString("miui.focus.param", buildIslandJson(p).toString())
        manager.notify(notificationId, n)
    }

    override fun onDestroy() {
        job?.cancel()
        stopForeground(STOP_FOREGROUND_REMOVE)
        super.onDestroy()
    }

    override fun onBind(intent: Intent?) = null
}

