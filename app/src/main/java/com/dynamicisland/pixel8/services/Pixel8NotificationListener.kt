package com.dynamicisland.pixel8.services

import android.content.Intent
import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification

class Pixel8NotificationListener : NotificationListenerService() {

    override fun onNotificationPosted(sbn: StatusBarNotification?) {
        super.onNotificationPosted(sbn)
        sbn?.let {
            val extras = it.notification.extras
            val title = extras.getString("android.title") ?: ""
            val text = extras.getCharSequence("android.text")?.toString() ?: ""

            if (title.isBlank() && text.isBlank()) return

            val intent = Intent("com.dynamicisland.pixel8.NOTIFICATION_RECEIVED").apply {
                putExtra("package", it.packageName)
                putExtra("title", title)
                putExtra("message", text)
            }
            sendBroadcast(intent)
        }
    }
}
