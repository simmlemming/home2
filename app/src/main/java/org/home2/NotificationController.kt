package org.home2

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.graphics.BitmapFactory
import android.os.Build
import android.support.v4.app.NotificationCompat

/**
 * Created by mtkachenko on 26/10/17.
 */
open class NotificationController(private val context: Context) {
    companion object {
        const val NOTIFICATION_ID = 1
    }

    private val notificationManager: NotificationManager
        get() = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

    var muteAllNotifications = false

    open fun notifyAlarm() {
        if (muteAllNotifications) {
            return
        }

        val notification = with(newNotification(context)) {
            setSmallIcon(R.drawable.ic_notification_small_alarm)
            setContentText(context.getString(R.string.notification_text_alarm))
            build()
        }

        notificationManager.notify(NOTIFICATION_ID, notification)
    }

    fun cancelNotification() {
        notificationManager.cancel(NOTIFICATION_ID)
    }

//    open fun notifyDisconnected() {
//        val notification = with(newNotification(context)) {
//            setSmallIcon(R.drawable.ic_notification_small_disconnected)
//            setContentText(context.getString(R.string.notification_text_disconnected))
//            build()
//        }
//
//        notificationManager.notify(NOTIFICATION_ID, notification)
//    }
//
//    open fun notifyConnected() {
//        val notification = with(newNotification(context)) {
//            setSmallIcon(R.drawable.ic_notification_small_connected)
//            setContentText(context.getString(R.string.notification_text_connected))
//            build()
//        }
//
//        notificationManager.notify(NOTIFICATION_ID, notification)
//    }

//    open fun notifyOk() = notifyConnected()
//
//    open fun newDisconnectedNotification(): Notification {
//        return with(newNotification(context)) {
//            setSmallIcon(R.drawable.ic_notification_small_disconnected)
//            setContentText(context.getString(R.string.notification_text_connected))
//            build()
//        }
//    }

    private fun newNotification(context: Context): NotificationCompat.Builder {
        return NotificationCompat.Builder(context, "home").apply {
            setChannelId("home")
            setLargeIcon(BitmapFactory.decodeResource(context.resources, R.drawable.ic_notification_large_icon))
            setContentTitle(context.getString(R.string.app_name))
            setPriority(NotificationCompat.PRIORITY_MAX)
            setCategory(Notification.CATEGORY_ALARM)
            setContentIntent(MainActivity.intentToOpen(context))
        }
    }

    fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= 26) {
            val channel = NotificationChannel("home", "Home", NotificationManager.IMPORTANCE_HIGH)
            notificationManager.createNotificationChannel(channel)
        }
    }
}