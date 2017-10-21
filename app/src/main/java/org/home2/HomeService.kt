package org.home2

import android.app.Service
import android.arch.lifecycle.LiveData
import android.content.Intent
import android.os.Binder
import android.os.IBinder
import android.util.Log
import org.json.JSONObject

/**
 * Created by mtkachenko on 21/10/17.
 */
private const val OUT_TOPIC = "home/out"

class HomeService : Service() {

    private lateinit var mqtt: BaseMqtt
    lateinit var motionSensorInfo: LiveData<MotionSensorInfo>

    override fun onCreate() {
        super.onCreate()
        mqtt = (applicationContext as HomeApplication).mqtt
        motionSensorInfo = MotionSensorLiveData(mqtt, "living_motion_01")
    }

    override fun onBind(intent: Intent?): IBinder {
        return HomeBinder()
    }

    inner class HomeBinder : Binder() {
        val service = this@HomeService
    }
}

private class MotionSensorLiveData(mqtt: BaseMqtt, private val deviceName : String) : BaseMqttLiveData<MotionSensorInfo>(mqtt, OUT_TOPIC) {

    override fun onNewMessage(message: JSONObject) {
        Log.i(TAG, "$OUT_TOPIC: $message")
    }
}