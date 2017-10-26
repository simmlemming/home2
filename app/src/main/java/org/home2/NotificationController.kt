package org.home2

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.graphics.BitmapFactory
import android.os.Build
import android.support.v4.app.NotificationCompat
import org.home2.service.HomeService

@Suppress("ClassName")
/**
 * Created by mtkachenko on 26/10/17.
 */
object notificationController {
    const val NOTIFICATION_ID = 1;

    fun newNotification(context: Context): Notification {
        val notification = NotificationCompat.Builder(context, "home")
        notification.setChannelId("home")
        notification.setSmallIcon(R.drawable.ic_notification_small_connected)
        notification.setLargeIcon(BitmapFactory.decodeResource(context.resources, R.drawable.ic_notification_large_icon))
        notification.setContentTitle(context.getString(R.string.app_name))
        notification.setContentText(context.getString(R.string.notification_text_connected))
        notification.setPriority(NotificationCompat.PRIORITY_LOW)
        notification.setCategory(Notification.CATEGORY_SYSTEM)

        notification.setContentIntent(MainActivity.intentToOpen(context))
        val stop = NotificationCompat.Action.Builder(0, context.getString(R.string.stop), HomeService.stopIntent(context))
        notification.addAction(stop.build())

        return notification.build()
    }

    fun createNotificationChannel(context: Context) {
        if (Build.VERSION.SDK_INT >= 26) {
            val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            val channel = NotificationChannel("home", "Home", NotificationManager.IMPORTANCE_LOW)
            notificationManager.createNotificationChannel(channel)
        }
    }
}