package org.home2.service

import android.arch.lifecycle.*
import android.arch.lifecycle.Observer
import android.content.Intent
import android.os.Binder
import android.os.IBinder
import org.home2.*
import org.home2.mqtt.ConnectCallback
import org.home2.mqtt.HomeConnectivityChangedListener
import org.home2.service.HomeService.Companion.OUT_TOPIC
import org.json.JSONObject
import java.util.*

/**
 * Created by mtkachenko on 21/10/17.
 */
class HomeService : LifecycleService() {
    companion object {
        const val OUT_TOPIC = "home/out"
        const val IN_TOPIC = "home/in"
        const val DEVICE_NAME_ALL = "all"
    }

    private lateinit var mqtt: BaseMqtt
    private lateinit var deviceRepository: DeviceRepository

    private val liveDevices: MutableMap<String, DeviceLiveData> = mutableMapOf()
    private val liveCameras: MutableMap<String, CameraLiveData> = mutableMapOf()
    val liveConnectionState: LiveData<ConnectionState> = MutableLiveData<ConnectionState>()

    override fun onCreate() {
        super.onCreate()
        mqtt = (applicationContext as HomeApplication).mqtt
        mqtt.connectivityListener = HomeConnectivityChangedListener(liveConnectionState as MutableLiveData<ConnectionState>)
        mqtt.connect(ConnectCallback(liveConnectionState))

        deviceRepository = (applicationContext as HomeApplication).deviceRepository

        deviceRepository.getAll().forEach {
            val ld = DeviceLiveData(mqtt, it.name)
            liveDevices[it.name] = ld
        }

        liveDevices.forEach { entry ->
            entry.value.observe(this, Observer { networkResource ->

                if (networkResource?.state == NetworkResource.State.SUCCESS) {
                    deviceRepository.update(networkResource.data!!)
                }
            })
        }

        liveCameras[CAMERA_NAME_01] = CameraLiveData(CAMERA_NAME_01, 0)
        liveCameras[CAMERA_NAME_02] = CameraLiveData(CAMERA_NAME_02, 1)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        super.onStartCommand(intent, flags, startId)
        return START_STICKY
    }

    override fun onBind(intent: Intent?): IBinder {
        super.onBind(intent)
        return HomeBinder()
    }

    inner class HomeBinder : Binder() {
        val service = this@HomeService
    }

    fun device(deviceName: String) = DeviceInteraction(deviceName, mqtt, deviceRepository, liveDevices)

    fun observe(deviceName: String, owner: LifecycleOwner, observer: Observer<NetworkResource<DeviceInfo>>) {
        liveDevices[deviceName]?.observe(owner, observer)
    }

    fun observeCamera(deviceName: String, owner: LifecycleOwner, observer: Observer<NetworkResource<CameraLiveData.CameraDeviceInfo>>) {
        liveCameras[deviceName]?.observe(owner,  observer)
    }

    fun refreshPicture(deviceName: String, timestamp: Date) {
        liveCameras[deviceName]?.refreshPicture(timestamp)
    }

    override fun onDestroy() {
        super.onDestroy()
        mqtt.disconnect()
    }
}

class DeviceLiveData(mqtt: BaseMqtt, private val deviceName: String) : BaseMqttLiveData<NetworkResource<DeviceInfo>>(mqtt, OUT_TOPIC) {

    override fun onNewMessage(message: JSONObject) {
        val name = message.optString("name")
        if (name != deviceName && name != HomeService.DEVICE_NAME_ALL) {
            return
        }

        val deviceInfo = DeviceInfo.fromJson(message)
        val info = if (deviceInfo == null) {
            NetworkResource.error(IllegalArgumentException("Cannot parse message $message"))
        } else {
            NetworkResource.success(deviceInfo)
        }

        postValue(info)
    }
}