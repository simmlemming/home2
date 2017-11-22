package org.home2.gcm

import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import org.home2.DeviceInfo
import org.home2.HomeApplication
import org.home2.NotificationController

/**
 * Created by mtkachenko on 21/11/17.
 */
class HomeMessagingService : FirebaseMessagingService() {
    private lateinit var notificationController: NotificationController

    override fun onCreate() {
        super.onCreate()
        notificationController = (applicationContext as HomeApplication).notificationController
    }

    override fun onMessageReceived(message: RemoteMessage) {
        val deviceString = message.data["message"] ?: return

        DeviceInfo.fromJson(deviceString)?.let {
            if (it.state == DeviceInfo.STATE_ALARM) {
                notificationController.notifyAlarm()
            }
        }
    }
}