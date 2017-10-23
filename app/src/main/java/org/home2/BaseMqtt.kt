package org.home2

import android.arch.lifecycle.MutableLiveData
import org.eclipse.paho.client.mqttv3.IMqttMessageListener

/**
 * Created by mtkachenko on 29/05/17.
 */
abstract class BaseMqtt {
    val connectionStatus = MutableLiveData<ConnectionState>()

    private val listeners = mutableMapOf<String, MutableList<(String) -> Unit>>()

    protected abstract fun subscribeInner(topic: String, listener: IMqttMessageListener)
    protected abstract fun unsubscribeInner(topic: String)
    abstract fun connect()
    abstract fun disconnect()

    protected val subscribeListener = IMqttMessageListener { topic, message ->
        listeners[topic]?.forEach { listener ->
            listener.invoke(message.toString())
        }
    }

    fun subscribe(topic: String, listener: (String) -> Unit) {
        if (listeners.containsKey(topic)) {
            listeners[topic]!!.add(listener)
        } else {
            listeners[topic] = mutableListOf(listener)
        }

        if (listeners[topic]!!.size == 1) {
            subscribeInner(topic, subscribeListener)
        }
    }

    fun unsubscribe(topic: String, listener: (String) -> Unit) {
        if (!listeners.containsKey(topic)) {
            return
        }

        listeners[topic]!!.remove(listener)

        if (listeners[topic]!!.isEmpty()) {
            unsubscribeInner(topic)
        }
    }
}