package org.home2.mqtt

import android.content.Context
import android.util.Log
import org.eclipse.paho.client.mqttv3.IMqttMessageListener
import org.eclipse.paho.client.mqttv3.MqttMessage
import org.home2.BaseMqtt
import org.home2.ConnectionState
import org.home2.DeviceInfo
import org.home2.TAG
import org.home2.service.IN_TOPIC
import org.home2.service.OUT_TOPIC
import org.json.JSONException
import org.json.JSONObject
import java.util.*
import java.util.concurrent.ScheduledThreadPoolExecutor
import java.util.concurrent.TimeUnit

/**
 * Created by mtkachenko on 29/05/17.
 */

class Mqtt(context: Context) : BaseMqtt() {
    private val rand = Random()
    private var timer: Timer? = null
    private val executor = ScheduledThreadPoolExecutor(1)

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

    override fun connect() {
        timer = Timer()
        connectionStatus.value = ConnectionState.CONNECTED
        timer!!.scheduleAtFixedRate(TempSensorUpdates(), rand.nextInt(1000).toLong(), 1000L)
//        timer!!.scheduleAtFixedRate(MotionSensorUpdates("living_motion_01"), 0, 2000L)
    }

    override fun disconnect() {
        connectionStatus.value = ConnectionState.DISCONNECTED
        timer?.cancel()
        timer = null
    }

    override fun publish(topic: String, message: String) {
        Log.i(TAG, "$topic <-- $message")

        if (topic != IN_TOPIC) {
            return
        }

        var response: JSONObject? = null

        try {
            val messageObject = JSONObject(message)
            val cmd = messageObject.getString("cmd")
            val name = messageObject.getString("name")

            response = when (cmd) {
                "on" -> newResponse(name, DeviceInfo.STATE_OK)
                "off" -> newResponse(name, DeviceInfo.STATE_OFF)
                "reset" -> newResponse(name, DeviceInfo.STATE_OK)
                else -> null
            }
        } catch (e: JSONException) {

        }

        response?.let {
            executor.schedule({ subscribeListener.messageArrived(OUT_TOPIC, MqttMessage(it.toString().toByteArray())) }, 500L, TimeUnit.MILLISECONDS)
        }
    }

    private inner class TempSensorUpdates : TimerTask() {
        override fun run() {
            val temp = rand.nextInt(15) + 15
            val hum = rand.nextInt(15) + 30
            val tempUpdate = "{\"t\": $temp, \"h\": $hum}"

            val message = MqttMessage(tempUpdate.toByteArray())
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
