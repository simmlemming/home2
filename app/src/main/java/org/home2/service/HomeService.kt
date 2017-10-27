package org.home2.service

import android.app.PendingIntent
import android.app.Service
import android.arch.lifecycle.LifecycleOwner
import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.Observer
import android.content.Context
import android.content.Intent
import android.os.Binder
import android.os.IBinder
import org.home2.*
import org.home2.mqtt.ConnectCallback
import org.home2.mqtt.HomeConnectivityChangedListener
import org.home2.service.HomeService.Companion.OUT_TOPIC
import org.json.JSONException
import org.json.JSONObject

/**
 * Created by mtkachenko on 21/10/17.
 */

class HomeService : Service() {
    companion object {
        const val OUT_TOPIC = "home/out"
        const val IN_TOPIC = "home/in"
        const val DEVICE_NAME_ALL = "all"
        private const val ACTION_STOP = "stop"

        fun stopIntent(context: Context): PendingIntent {
            val intent = Intent(context, HomeService::class.java)
            intent.action = ACTION_STOP

            return PendingIntent.getService(context, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT)
        }
    }

    private lateinit var mqtt: BaseMqtt
    private lateinit var notificationController: NotificationController
    private val liveData: MutableMap<String, DeviceLiveData> = mutableMapOf()

    val connectionState: LiveData<ConnectionState> = MutableLiveData<ConnectionState>()
    private val notificationUpdater = Observer<ConnectionState> { connectionState ->
        when (connectionState) {
            ConnectionState.CONNECTED -> notificationController.notifyConnected()
            else -> notificationController.notifyDisconnected()
        }
    }

    private val innerSubscribeListener = { message: String ->
        try {
            if (JSONObject(message).optInt("state") == DeviceInfo.STATE_ALARM) {
                notificationController.notifyAlarm()
            }
        } catch (e: JSONException) {

        }
    }

    override fun onCreate() {
        super.onCreate()
        notificationController = (applicationContext as HomeApplication).notificationController
        startForeground(NotificationController.NOTIFICATION_ID, notificationController.newDisconnectedNotification())

        connectionState.observeForever(notificationUpdater)

        mqtt = (applicationContext as HomeApplication).mqtt
        mqtt.connectivityListener = HomeConnectivityChangedListener(connectionState as MutableLiveData<ConnectionState>)
        mqtt.connect(ConnectCallback(connectionState))

        mqtt.subscribe(OUT_TOPIC, innerSubscribeListener)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (ACTION_STOP == intent?.action) {
            stopSelf()
        }

        return START_STICKY
    }

    override fun onBind(intent: Intent?): IBinder {
        return HomeBinder()
    }

    inner class HomeBinder : Binder() {
        val service = this@HomeService
    }

    fun device(deviceName: String) = DeviceInteraction(deviceName, mqtt, liveData)

    fun observe(deviceName: String, owner: LifecycleOwner, observer: Observer<NetworkResource<DeviceInfo>>) {
        if (liveData[deviceName] == null) {
            liveData[deviceName] = DeviceLiveData(mqtt, deviceName)
        }

        liveData[deviceName]!!.observe(owner, observer)
    }

    override fun onDestroy() {
        mqtt.unsubscribe(OUT_TOPIC, innerSubscribeListener)
        mqtt.disconnect()
        connectionState.removeObserver(notificationUpdater)
        super.onDestroy()
    }
}

class DeviceLiveData(mqtt: BaseMqtt, private val deviceName: String) : BaseMqttLiveData<NetworkResource<DeviceInfo>>(mqtt, OUT_TOPIC) {

    override fun onNewMessage(message: JSONObject) {
        val name = message.optString("name")
        if (name != deviceName && name != HomeService.DEVICE_NAME_ALL) {
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