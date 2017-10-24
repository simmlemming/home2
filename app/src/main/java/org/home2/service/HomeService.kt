package org.home2.service

import android.app.Service
import android.arch.lifecycle.LifecycleOwner
import android.arch.lifecycle.Observer
import android.content.Intent
import android.os.Binder
import android.os.IBinder
import android.util.Log
import org.home2.*
import org.json.JSONException
import org.json.JSONObject

/**
 * Created by mtkachenko on 21/10/17.
 */
const val OUT_TOPIC = "home/out"
const val IN_TOPIC = "home/in"

class HomeService : Service() {
    private lateinit var mqtt: BaseMqtt
    private val liveData: MutableMap<String, DeviceLiveData> = mutableMapOf()

    override fun onCreate() {
        super.onCreate()
        mqtt = (applicationContext as HomeApplication).mqtt
    }

    override fun onBind(intent: Intent?): IBinder {
        return HomeBinder()
    }

    inner class HomeBinder : Binder() {
        val service = this@HomeService
    }

    fun device(deviceName: String) = DeviceInteraction(deviceName, mqtt, liveData[deviceName])

    fun observe(deviceName: String, owner: LifecycleOwner, observer: Observer<NetworkResource<DeviceInfo>>) {
        if (liveData[deviceName] == null) {
            liveData[deviceName] = DeviceLiveData(mqtt, deviceName)
        }

        liveData[deviceName]!!.observe(owner, observer)
    }
}

class DeviceLiveData(mqtt: BaseMqtt, private val deviceName: String) : BaseMqttLiveData<NetworkResource<DeviceInfo>>(mqtt, OUT_TOPIC) {

    override fun onNewMessage(message: JSONObject) {
        Log.i(TAG, "${OUT_TOPIC}: $message")

        if (message.optString("name") != deviceName) {
            return
        }

        val info: NetworkResource<DeviceInfo> = try {
            val state = message.getInt("state")
            NetworkResource.success(DeviceInfo(deviceName, state))
        } catch (e: JSONException) {
            NetworkResource.error(e)
        }

        postValue(info)
    }
}