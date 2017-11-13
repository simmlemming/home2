package org.home2

import android.app.Application
import android.support.annotation.VisibleForTesting
import org.home2.mqtt.Mqtt

/**
 * Created by mtkachenko on 27/05/17.
 */

const val TAG = "Home"

class HomeApplication : Application() {
    lateinit var settings: HomeSettings
        private set

    lateinit var mqtt: BaseMqtt
        private set

    lateinit var notificationController: NotificationController
        private set

    override fun onCreate() {
        super.onCreate()

        settings = HomeSettings(this)
        mqtt = Mqtt(this)
        notificationController = NotificationController(this)

        notificationController.createNotificationChannel()
    }

    @VisibleForTesting
    fun setMockedMqtt(mqtt: BaseMqtt) {
        this.mqtt = mqtt
    }

    @VisibleForTesting
    fun setMockedNotificationController(notificationController: NotificationController) {
        this.notificationController = notificationController
    }

    @VisibleForTesting
    fun cleanMockedDependencies() {
        mqtt = Mqtt(this)
        notificationController = NotificationController(this)
    }
}