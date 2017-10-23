package org.home2

import android.app.Service
import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.content.Intent
import android.os.Binder
import android.os.IBinder
import android.util.Log
import org.json.JSONException
import org.json.JSONObject

/**
 * Created by mtkachenko on 21/10/17.
 */
private const val OUT_TOPIC = "home/out"
private const val IN_TOPIC = "home/in"

class HomeService : Service() {

    private lateinit var mqtt: BaseMqtt
    lateinit var motionSensorInfo: LiveData<NetworkResource<MotionSensorInfo>>

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

    fun devices(filer: DeviceFilter): DeviceInteraction = DeviceInteraction(mqtt, filer) {
        val currentInfo = motionSensorInfo.value?.data
        (motionSensorInfo as MutableLiveData).postValue(NetworkResource.loading(currentInfo))
    }
}

abstract class DeviceFilter {
    companion object {
        fun withName(name: String) = object : DeviceFilter() {
            override fun addFilers(command: JSONObject) {
                command.put("name", name)
            }
        }
    }

    abstract fun addFilers(command: JSONObject)
}

class DeviceInteraction(private val mqtt: BaseMqtt, private val deviceFilter: DeviceFilter, private val listener: (() -> Unit)?) {
    fun on() = cmd("on")
    fun off() = cmd("off")
    fun reset() = cmd("reset")

    private fun cmd(cmd: String) {
        val message = JSONObject()
        deviceFilter.addFilers(message)
        message.put("cmd", cmd)
        mqtt.publish(IN_TOPIC, message.toString())
        listener?.invoke()
    }
}

private class MotionSensorLiveData(mqtt: BaseMqtt, private val deviceName: String) : BaseMqttLiveData<NetworkResource<MotionSensorInfo>>(mqtt, OUT_TOPIC) {

    override fun onNewMessage(message: JSONObject) {
        Log.i(TAG, "$OUT_TOPIC: $message")

        if (message.optString("name") != deviceName) {
            return
        }

        val info: NetworkResource<MotionSensorInfo> = try {
            val state = message.getInt("state")
            NetworkResource.success(MotionSensorInfo(deviceName, state))
        } catch (e: JSONException) {
            NetworkResource.error(e)
        }

        postValue(info)
    }
}