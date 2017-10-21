package org.home2

import android.arch.lifecycle.MutableLiveData
import org.json.JSONException
import org.json.JSONObject

abstract class BaseMqttLiveData<T>(private val mqtt: BaseMqtt, private val topic: String) : MutableLiveData<T>() {

    abstract fun onNewMessage(message: JSONObject)

    override fun onActive() {
        mqtt.subscribe(topic) { message ->
            val parsed = parse(message)
            parsed?.let { onNewMessage(it) }
        }
    }

    override fun onInactive() {
        mqtt.unsubscribe(topic)
    }

    private fun parse(message: String): JSONObject? {
        return try {
            JSONObject(message)
        } catch (e: JSONException) {
            null
        }
    }
}