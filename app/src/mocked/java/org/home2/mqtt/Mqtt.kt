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
    private val knownDeviceNames = listOf<String>("living_motion_01", "living_motion_02", "temp_sensor_01", "humidity_sensor_01")

    override fun subscribeInner(topic: String, listener: IMqttMessageListener) {

    }

    override fun unsubscribeInner(topic: String) {

    }

    private fun newResponse(name: String, state: Int): JSONObject {
        return JSONObject().apply {
            put("name", name)
            put("state", state)
        }
    }

    override fun connect(listener: IMqttActionListener) {
        listener.onSuccess(null)
        timer = Timer()
        timer!!.scheduleAtFixedRate(TempSensorUpdates(), rand.nextInt(1000).toLong(), 1000L)
        timer!!.scheduleAtFixedRate(HumSensorUpdates(), rand.nextInt(1000).toLong(), 1000L)
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

        val namesToRespondTo = if (name == HomeService.DEVICE_NAME_ALL) knownDeviceNames else listOf(name)

        namesToRespondTo.forEach { nameToRespondTo ->
            val response: JSONObject? = when (cmd) {
                "on" -> newResponse(nameToRespondTo, DeviceInfo.STATE_OK)
                "off" -> newResponse(nameToRespondTo, DeviceInfo.STATE_OFF)
                "reset" -> newResponse(nameToRespondTo, DeviceInfo.STATE_OK)
                "state" -> newResponse(nameToRespondTo, DeviceInfo.STATE_OK)
                else -> null
            }

            response?.let {
                executor.schedule({ subscribeListener.messageArrived(HomeService.OUT_TOPIC, MqttMessage(it.toString().toByteArray())) }, 500L, TimeUnit.MILLISECONDS)
            }
        }
    }

    private inner class TempSensorUpdates : TimerTask() {
        override fun run() {
            val temp = rand.nextInt(15) + 15
            val tempUpdate = JSONObject().apply {
                put("name", "temp_sensor_01")
                put("state", 1)
                put("value", temp)
            }

            val message = MqttMessage(tempUpdate.toString().toByteArray())
            subscribeListener.messageArrived("home/out", message)
        }
    }

    private inner class HumSensorUpdates : TimerTask() {
        override fun run() {
            val hum = rand.nextInt(30) + 30
            val tempUpdate = JSONObject().apply {
                put("name", "humidity_sensor_01")
                put("state", 1)
                put("value", hum)
            }

            val message = MqttMessage(tempUpdate.toString().toByteArray())
            subscribeListener.messageArrived("home/out", message)
        }
    }

    private inner class MotionSensorUpdates(val deviceName: String) : TimerTask() {
        override fun run() {
            val state = rand.nextInt(5)

            val motionUpdate = JSONObject()
            motionUpdate.put("name", deviceName)
            motionUpdate.put("room", "living_room")
            motionUpdate.put("type", "motion_sensor")
            motionUpdate.put("state", state)

            val message = MqttMessage(motionUpdate.toString().toByteArray())
            subscribeListener.messageArrived("home/out", message)
        }
    }
}
