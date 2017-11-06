package org.home2

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.graphics.BitmapFactory
import android.os.Build
import android.support.v4.app.NotificationCompat
import org.home2.service.HomeService

/**
 * Created by mtkachenko on 26/10/17.
 */
open class NotificationController(private val context: Context) {
    companion object {
        const val NOTIFICATION_ID = 1
    }

    private val notificationManager: NotificationManager
        get() = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

    open fun notifyDisconnected() {
        val notification = with(newNotification(context)) {
            setSmallIcon(R.drawable.ic_notification_small_disconnected)
            setContentText(context.getString(R.string.notification_text_disconnected))
            build()
        }

        notificationManager.notify(NOTIFICATION_ID, notification)
    }

    open fun notifyConnected() {
        val notification = with(newNotification(context)) {
            setSmallIcon(R.drawable.ic_notification_small_connected)
            setContentText(context.getString(R.string.notification_text_connected))
            build()
        }

        notificationManager.notify(NOTIFICATION_ID, notification)
    }

    open fun notifyAlarm() {
        val notification = with(newNotification(context)) {
            setSmallIcon(R.drawable.ic_notification_small_alarm)
            setContentText(context.getString(R.string.notification_text_alarm))
            build()
        }

        notificationManager.notify(NOTIFICATION_ID, notification)
    }

    open fun newDisconnectedNotification(): Notification {
        return with(newNotification(context)) {
            setSmallIcon(R.drawable.ic_notification_small_disconnected)
            setContentText(context.getString(R.string.notification_text_connected))
            build()
        }
    }

    private fun newNotification(context: Context): NotificationCompat.Builder {
        return NotificationCompat.Builder(context, "home").apply {
            setChannelId("home")
            setLargeIcon(BitmapFactory.decodeResource(context.resources, R.drawable.ic_notification_large_icon))
            setContentTitle(context.getString(R.string.app_name))
            setPriority(NotificationCompat.PRIORITY_LOW)
            setCategory(Notification.CATEGORY_SYSTEM)
            setContentIntent(MainActivity.intentToOpen(context))

            val stop = NotificationCompat.Action.Builder(0, context.getString(R.string.stop), HomeService.stopIntent(context))
            addAction(stop.build())
        }
    }

    fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= 26) {
            val channel = NotificationChannel("home", "Home", NotificationManager.IMPORTANCE_LOW)
            notificationManager.createNotificationChannel(channel)
        }
    }
}