package org.home2.mqtt

import android.os.Handler
import android.util.Log
import org.eclipse.paho.android.service.MqttAndroidClient
import org.eclipse.paho.client.mqttv3.*
import org.home2.BaseMqtt
import org.home2.HomeApplication
import org.home2.TAG

/**
 * Created by mtkachenko on 20/05/17.
 */
private const val MQTT_QOS = 1

class Mqtt(private val app: HomeApplication) : BaseMqtt() {
    private val mqttClientId = "android-app-" + app.settings.getInstanceId()
    private val mqttClient = MqttAndroidClient(app, MQTT_SERVER_URL, mqttClientId)
    private val handler = Handler()

    init {
        mqttClient.setCallback(HomeMqttCallback())
    }

    override fun publish(topic: String, message: String) {
        whenConnected {
            Log.i(TAG, "$topic <-- $message")
            mqttClient.publish(topic, MqttMessage(message.toByteArray()))
        }
    }

    override fun subscribeInner(topic: String, listener: IMqttMessageListener) {
        whenConnected {
            mqttClient.setBufferOpts(HomeDisconnectedBufferOptions())
            mqttClient.subscribe(topic, MQTT_QOS, null, null, listener)
        }
    }

    override fun unsubscribeInner(topic: String) {
        whenConnected {
            mqttClient.unsubscribe(topic)
        }
    }

    override fun connect(listener: IMqttActionListener) {
        connectivityListener?.onConnecting()
        mqttClient.connect(HomeConnectOptions(), null, listener)
    }

    override fun disconnect() {
        if (mqttClient.isConnected) {
            mqttClient.disconnect()
        }
    }

    private fun whenConnected(f: () -> Unit) {
        if (mqttClient.isConnected) {
            f.invoke()
        } else {
            handler.postDelayed({ whenConnected(f) }, 200)
        }
    }

    private inner class HomeMqttCallback : MqttCallbackExtended {
        override fun connectComplete(reconnect: Boolean, serverURI: String?) {
            connectivityListener?.onConnected()
        }

        override fun messageArrived(topic: String?, message: MqttMessage?) {
            // This is not called, the one provided to subscribe() is called
            Log.i(TAG, "${javaClass.simpleName}.messageArrived, topic = $topic, message = $message, thread = ${Thread.currentThread().name}")
        }

        override fun connectionLost(cause: Throwable?) {
            Log.e(TAG, "${javaClass.simpleName}.connectionLost", cause)
            connectivityListener?.onDisconnected()
        }

        override fun deliveryComplete(token: IMqttDeliveryToken?) {

        }
    }
}