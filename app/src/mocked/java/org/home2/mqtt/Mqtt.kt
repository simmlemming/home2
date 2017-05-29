package org.home2.mqtt

import android.content.Context
import org.home2.BaseMqtt
import org.home2.ConnectionState
import java.util.*
import kotlin.collections.HashMap

/**
 * Created by mtkachenko on 29/05/17.
 */

class Mqtt(context: Context) : BaseMqtt() {
    private val rand = Random()
    private var timer : Timer? = null
    private val tasks = HashMap<String, TimerTask>()

    override fun subscribe(topic: String, listener: (String) -> Unit) {
        val task = object : TimerTask() {
            override fun run() {
                val temp = rand.nextInt(15) + 15
                val hum = rand.nextInt(15) + 30
                listener.invoke("{\"t\": $temp, \"h\": $hum, \"name\": \"$topic\"}")
            }
        }

        tasks[topic] = task

        timer?.scheduleAtFixedRate(
                task,
                1000L,
                1000L
        )
    }

    override fun unsubscribe(topic: String) {
        tasks[topic]?.cancel()
        tasks -= topic
    }

    override fun connect() {
        timer = Timer()
        connectionStatus.value = ConnectionState.CONNECTED
    }

    override fun disconnect() {
        connectionStatus.value = ConnectionState.DISCONNECTED
        timer?.cancel()
        timer = null
    }
}
