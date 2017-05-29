package org.home2.mqtt

import android.content.Context
import android.os.Handler
import android.util.Log
import org.eclipse.paho.android.service.MqttAndroidClient
import org.eclipse.paho.client.mqttv3.*
import org.home2.BaseMqtt
import org.home2.ConnectionState
import org.home2.TAG




/**
 * Created by mtkachenko on 20/05/17.
 */
private const val MQTT_SERVER_URL = "tcp://192.168.0.110"
private const val MQTT_CLIENT_ID = "android-app"
private const val MQTT_QOS = 1

class Mqtt(context: Context) : BaseMqtt() {
    private val mqttClient = MqttAndroidClient(context, MQTT_SERVER_URL, MQTT_CLIENT_ID)
    private val handler = Handler()

    init {
        mqttClient.setCallback(HomeMqttCallback())
    }

    override fun subscribe(topic: String, listener: (String) -> Unit) {
        whenConnected {
            mqttClient.setBufferOpts(HomeDisconnectedBufferOptions())
            mqttClient.subscribe(topic, MQTT_QOS, null, null) { _, message ->
                listener.invoke(message.toString())
            }
        }
    }

    override fun unsubscribe(topic: String) {
        if (isConnected()) {
            mqttClient.unsubscribe(topic)
        }
    }

    override fun connect() {
        connectionStatus.value = ConnectionState.CONNECTING
        mqttClient.connect(HomeConnectOptions(), null, ConnectCallback())
    }

    override fun disconnect() {
        if (isConnected()) {
            connectionStatus.value = ConnectionState.DISCONNECTED
            mqttClient.disconnect()
        }
    }

    private fun whenConnected(f : () -> Unit) {
        if (isConnected()) {
            f.invoke()
        } else {
            handler.postDelayed({ whenConnected(f) }, 200)
        }
    }

    private fun isConnected() = connectionStatus.value == ConnectionState.CONNECTED

    private inner class HomeMqttCallback : MqttCallbackExtended {
        override fun connectComplete(reconnect: Boolean, serverURI: String?) {
            connectionStatus.postValue(ConnectionState.CONNECTED)
        }

        override fun messageArrived(topic: String?, message: MqttMessage?) {
            // This is not called, the one provided to subscribe() is called
            Log.i(TAG, "${javaClass.simpleName}.messageArrived, topic = $topic, message = $message, thread = ${Thread.currentThread().name}")
        }

        override fun connectionLost(cause: Throwable?) {
            Log.e(TAG, "${javaClass.simpleName}.connectionLost", cause)
            connectionStatus.postValue(ConnectionState.DISCONNECTED)
        }

        override fun deliveryComplete(token: IMqttDeliveryToken?) {

        }
    }

    private inner class ConnectCallback : IMqttActionListener {
        override fun onSuccess(asyncActionToken: IMqttToken?) {
            Log.i(TAG, "${javaClass.simpleName}.onSuccess()")
            connectionStatus.postValue(ConnectionState.CONNECTED)
        }

        override fun onFailure(asyncActionToken: IMqttToken?, exception: Throwable?) {
            Log.e(TAG, "${javaClass.simpleName}.onFailure()", exception)
            connectionStatus.postValue(ConnectionState.DISCONNECTED)
        }
    }
}
