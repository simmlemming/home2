package org.home2.mqtt

import android.util.Log
import org.eclipse.paho.client.mqttv3.IMqttActionListener
import org.eclipse.paho.client.mqttv3.IMqttMessageListener
import org.eclipse.paho.client.mqttv3.MqttMessage
import org.home2.BaseMqtt
import org.home2.DeviceInfo
import org.home2.HomeApplication
import org.home2.TAG
import org.home2.service.HomeService
import org.json.JSONObject
import java.util.*
import java.util.concurrent.ScheduledThreadPoolExecutor
import java.util.concurrent.TimeUnit

/**
 * Created by mtkachenko on 29/05/17.
 */

class Mqtt(app: HomeApplication) : BaseMqtt() {
    private val rand = Random()
    private var timer: Timer? = null
    private val executor = ScheduledThreadPoolExecutor(1)
    private val knownDevices = app
            .deviceRepository
            .getAll()

    override fun subscribeInner(topic: String, listener: IMqttMessageListener) {

    }

    override fun unsubscribeInner(topic: String) {

    }

    private fun newResponse(deviceInfo: DeviceInfo, state: Int, value: Int, signal: Int): JSONObject {
        return JSONObject().apply {
            put("name", deviceInfo.name)
            put("room", deviceInfo.room)
            put("value", value)
            put("signal", -66)
            put("state", state)
        }
    }

    override fun connect(listener: IMqttActionListener) {
        listener.onSuccess(null)
        timer = Timer()

        knownDevices
                .filter { !it.name.contains("temp") }
                .forEach { timer!!.scheduleAtFixedRate(TempSensorUpdates(it), rand.nextInt(1000).toLong(), 1000L) }

        knownDevices
                .filter { !it.name.contains("humidity") }
                .forEach { timer!!.scheduleAtFixedRate(HumSensorUpdates(it), rand.nextInt(1000).toLong(), 1000L) }

//        timer!!.scheduleAtFixedRate(MotionSensorUpdates("living_motion_01"), 0, 2000L)
    }

    override fun disconnect() {
        timer?.cancel()
        timer = null
    }

    override fun publish(topic: String, message: String) {
        Log.i(TAG, "$topic <-- $message")

        if (topic != HomeService.IN_TOPIC) {
            return
        }

        val messageObject = JSONObject(message)
        val cmd = messageObject.optString("cmd")
        val name = messageObject.optString("name")

        val devicesToRespondTo = if (name == HomeService.DEVICE_NAME_ALL) knownDevices else knownDevices.filter { it.name == name }

        devicesToRespondTo.forEach { deviceToRespondTo ->
            val response: JSONObject? = when (cmd) {
                "on" -> newResponse(deviceToRespondTo, DeviceInfo.STATE_OK, 45, -96)
                "off" -> newResponse(deviceToRespondTo, DeviceInfo.STATE_OFF, 11, -96)
                "reset" -> newResponse(deviceToRespondTo, DeviceInfo.STATE_OK, 23, -96)
                "state" -> newResponse(deviceToRespondTo, DeviceInfo.STATE_OK, 98, -96)
                else -> null
            }

            response?.let {
                executor.schedule({ subscribeListener.messageArrived(HomeService.OUT_TOPIC, MqttMessage(it.toString().toByteArray())) }, 500L, TimeUnit.MILLISECONDS)
            }
        }
    }

    private open inner class TempSensorUpdates(private val device: DeviceInfo) : TimerTask() {
        override fun run() {
            val tempUpdate = newResponse(device, DeviceInfo.STATE_OK, newValue(), newSignal())
            val message = MqttMessage(tempUpdate.toString().toByteArray())
            subscribeListener.messageArrived("home/out", message)
        }

        open protected fun newSignal() = -69

        open protected fun newValue() = rand.nextInt(15) + 15
    }

    private inner class HumSensorUpdates(private val name: DeviceInfo) : TempSensorUpdates(name) {
        override fun newValue() = rand.nextInt(30) + 30
        override fun newSignal() = -73
    }

    private inner class MotionSensorUpdates(val deviceName: String) : TimerTask() {
        override fun run() {
            val state = rand.nextInt(5)

            val motionUpdate = JSONObject()
            motionUpdate.put("name", deviceName)
            motionUpdate.put("room", "living_room")
            motionUpdate.put("signal", -81)
            motionUpdate.put("type", "motion_sensor")
            motionUpdate.put("state", state)

            val message = MqttMessage(motionUpdate.toString().toByteArray())
            subscribeListener.messageArrived("home/out", message)
        }
    }
}
