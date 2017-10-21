package org.home2

import android.arch.lifecycle.LiveData
import org.json.JSONException
import org.json.JSONObject

/**
 * Created by mtkachenko on 29/05/17.
 */
class RoomInfoRepository(val mqtt: BaseMqtt) {

    fun getRoomInfo(roomName : String) : LiveData<RoomInfo> {
        return RoomInfoLiveData("$roomName/health")
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

    inner class RoomInfoLiveData(topic : String) : BaseMqttLiveData<RoomInfo>(mqtt, topic) {
        override fun onNewMessage(message: JSONObject) {
            parse(message)?.let {
                postValue(it)
            }
        }

        private fun parse(message: JSONObject): RoomInfo? {
            return try {
                RoomInfo("b", message.getInt("t"), message.getInt("h"))
            } catch (e : JSONException) {
                null
            }
        }
    }
}