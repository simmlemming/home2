package org.home2

import android.app.Application
import android.support.annotation.VisibleForTesting
import android.util.Log
import com.google.firebase.iid.FirebaseInstanceId
import org.home2.mqtt.Mqtt
import org.json.JSONObject

/**
 * Created by mtkachenko on 27/05/17.
 */

const val TAG = "Home"

class HomeApplication : Application() {
    lateinit var deviceRepository: DeviceRepository
        private set

    lateinit var settings: HomeSettings
        private set

    lateinit var mqtt: BaseMqtt
        private set

    lateinit var notificationController: NotificationController
        private set

    override fun onCreate() {
        super.onCreate()

        settings = HomeSettings(this)
        deviceRepository = DeviceRepository()
        mqtt = Mqtt(this)
        notificationController = NotificationController(this)

        notificationController.createNotificationChannel()
    }

    fun sendGcmToken() {
        val newGcmToken = FirebaseInstanceId.getInstance().token
        Log.i(TAG, "new token = $newGcmToken")

        val deviceRegistration = JSONObject().apply {
            put("cmd", "add_device")
            val device = JSONObject().apply {
                put("name", settings.getInstanceName())
                put("token", newGcmToken)
            }
            put("device", device)
        }

        mqtt.publish("home/out", deviceRegistration.toString())
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
    fun setMockedDeviceRepository(deviceRepository: DeviceRepository) {
        this.deviceRepository = deviceRepository
    }

    @VisibleForTesting
    fun cleanMockedDependencies() {
        mqtt = Mqtt(this)
        notificationController = NotificationController(this)
        deviceRepository = DeviceRepository()
    }
}