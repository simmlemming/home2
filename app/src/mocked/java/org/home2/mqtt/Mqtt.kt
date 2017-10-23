package org.home2.mqtt

import android.content.Context
import org.eclipse.paho.client.mqttv3.IMqttMessageListener
import org.eclipse.paho.client.mqttv3.MqttMessage
import org.home2.BaseMqtt
import org.home2.ConnectionState
import org.json.JSONObject
import java.util.*

/**
 * Created by mtkachenko on 29/05/17.
 */

class Mqtt(context: Context) : BaseMqtt() {

    private val rand = Random()
    private var timer: Timer? = null

    override fun subscribeInner(topic: String, listener: IMqttMessageListener) {

    }

    override fun unsubscribeInner(topic: String) {

    }

    override fun connect() {
        timer = Timer()
        connectionStatus.value = ConnectionState.CONNECTED
        timer!!.scheduleAtFixedRate(TempSensorUpdates(), rand.nextInt(1000).toLong(), 1000L)
        timer!!.scheduleAtFixedRate(MotionSensorUpdates("living_motion_01"), rand.nextInt(1000).toLong(), 1000L)
    }

    override fun disconnect() {
        connectionStatus.value = ConnectionState.DISCONNECTED
        timer?.cancel()
        timer = null
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
            val status = rand.nextInt(5)

            val motionUpdate = JSONObject()
            motionUpdate.put("name", deviceName)
            motionUpdate.put("room", "living_room")
            motionUpdate.put("type", "motion_sensor")
            motionUpdate.put("status", status)

            val message = MqttMessage(motionUpdate.toString().toByteArray())
            subscribeListener.messageArrived("home/out", message)
        }
    }
}
