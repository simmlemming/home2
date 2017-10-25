package org.home2.mqtt

import android.arch.lifecycle.MutableLiveData
import org.eclipse.paho.client.mqttv3.IMqttActionListener
import org.eclipse.paho.client.mqttv3.IMqttToken
import org.home2.BaseMqtt
import org.home2.ConnectionState

/**
 * Created by mtkachenko on 25/10/17.
 */
class HomeConnectivityChangedListener(private val liveData: MutableLiveData<ConnectionState>) : BaseMqtt.ConnectivityChangedListener {
    override fun onConnecting() {
        liveData.postValue(ConnectionState.CONNECTING)
    }

    override fun onConnected() {
        liveData.postValue(ConnectionState.CONNECTED)
    }

    override fun onDisconnected() {
        liveData.postValue(ConnectionState.DISCONNECTED)
    }
}

class ConnectCallback(private val liveData: MutableLiveData<ConnectionState>) : IMqttActionListener {
    override fun onSuccess(asyncActionToken: IMqttToken?) {
        liveData.postValue(ConnectionState.CONNECTED)
    }

    override fun onFailure(asyncActionToken: IMqttToken?, exception: Throwable?) {
        liveData.postValue(ConnectionState.DISCONNECTED)
    }
}
