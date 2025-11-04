package com.github.purofle.sandauschool.utils

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Context.NOTIFICATION_SERVICE
import android.os.Bundle
import androidx.core.net.toUri
import com.github.purofle.sandauschool.R


// 耗时操作
// OS1之前无焦点通知功能的版本，返回false
// OS1 OS2 OS3上，焦点通知权限关闭时，返回false,焦点通知权限打开时，返回true
fun hasFocusPermission(ctx: Context): Boolean {
    var canShowFocus = false
    try {
        val uri = "content://miui.statusbar.notification.public".toUri()
        val extras = Bundle()
        extras.putString("package", ctx.packageName)
        val bundle = ctx.contentResolver.call(uri, "canShowFocus", null, extras)
        canShowFocus = bundle!!.getBoolean("canShowFocus", false)
    } catch (e: Exception) {
    }
    return canShowFocus
}

fun landTest(context: Context) {
    val channelId = "test_channel_id"
    val channelName = "test_change_name"
    val testTitle = "test_title"
    val testText = "test_text"

    val notificationManager =
        context.getSystemService(NOTIFICATION_SERVICE) as NotificationManager

    val channel =
        NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_DEFAULT)
    notificationManager.createNotificationChannel(channel)

    val notification = Notification.Builder(context, channelId)
        .setContentTitle(testTitle)
        .setContentText(testText)
        .setSmallIcon(R.mipmap.ic_launcher)
        .build()

    val islandParams = """{
  "param_v2": {
    "baseInfo": {
        "type": 5,
        "title": "日落果",
        "content": "今天也是日落果",
        "specialTitle": "果"
    },
    "progressInfo": {
        "progress": 20,
        "colorProgress": "#FF8514",
        "colorProgressEnd": "#FF8514"
    }
  }
}
"""

    // 添加岛参数
    notification.extras.putString("miui.focus.param", islandParams)

    notificationManager.notify(1, notification)
}