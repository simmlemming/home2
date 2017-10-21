package org.home2

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import org.json.JSONObject

/**
 * Created by mtkachenko on 29/05/17.
 */
class RoomInfoRepository(val mqtt: BaseMqtt) {

    fun getRoomInfo(roomName : String) : LiveData<RoomInfo> {
        return MqttLiveData("$roomName/health")
    }

    fun getConnectionStatus() : LiveData<ConnectionState> {
        return mqtt.connectionStatus
    }

    fun start() {
        mqtt.connect()
    }

    fun stop() {
        mqtt.disconnect()
    }

    inner class MqttLiveData(val topic : String) : MutableLiveData<RoomInfo>() {

        override fun onActive() {
            mqtt.subscribe(topic) { message -> postValue(parse(message)) }
        }

        override fun onInactive() {
            mqtt.unsubscribe(topic)
        }

        private fun parse(message: String): RoomInfo {
            val info = JSONObject(message)
            return RoomInfo("b", info.getInt("t"), info.getInt("h"))
        }
    }
}