package org.home2

import org.eclipse.paho.client.mqttv3.IMqttActionListener
import org.eclipse.paho.client.mqttv3.IMqttMessageListener
import org.eclipse.paho.client.mqttv3.MqttMessage

/**
 * Created by mtkachenko on 29/05/17.
 */
abstract class BaseMqtt {
    interface ConnectivityChangedListener {
        fun onConnecting()
        fun onConnected()
        fun onDisconnected()
    }

    var connectivityListener: ConnectivityChangedListener? = null
    private val subscribeListeners = mutableMapOf<String, MutableList<(String) -> Unit>>()

    protected abstract fun subscribeInner(topic: String, listener: IMqttMessageListener)
    protected abstract fun unsubscribeInner(topic: String)
    abstract fun connect(listener: IMqttActionListener)
    abstract fun disconnect()
    abstract fun publish(topic: String, message: String)

    protected val subscribeListener = IMqttMessageListener { topic, message ->
        onNewMessage(topic, message)
    }

    fun subscribe(topic: String, listener: (String) -> Unit) {
        if (subscribeListeners.containsKey(topic)) {
            subscribeListeners[topic]!!.add(listener)
        } else {
            subscribeListeners[topic] = mutableListOf(listener)
        }

        if (subscribeListeners[topic]!!.size == 1) {
            subscribeInner(topic, subscribeListener)
        }
    }

    fun unsubscribe(topic: String, listener: (String) -> Unit) {
        if (!subscribeListeners.containsKey(topic)) {
            return
        }

        subscribeListeners[topic]!!.remove(listener)

        if (subscribeListeners[topic]!!.isEmpty()) {
            unsubscribeInner(topic)
        }
    }

    protected open fun onNewMessage(topic: String, message: MqttMessage) {
        subscribeListeners[topic]?.forEach { listener ->
            listener.invoke(message.toString())
        }
    }
}