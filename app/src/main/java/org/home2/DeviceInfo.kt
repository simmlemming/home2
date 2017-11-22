package org.home2

import org.json.JSONException
import org.json.JSONObject
import java.io.Serializable

/**
 * Created by mtkachenko on 21/10/17.
 */
data class DeviceInfo(val name: String, val room: String, val state: Int, val value: Int, val signal: Int) : Serializable {
//    enum class Type(val typeAsString: String) {
//        MOTION_SENSOR("motion_sensor"),
//        TEMP_SENSOR("temp_sensor"),
//        HUMIDITY_SENSOR("humidity_sensor"),
//        UNKNOWN("unknown");
//
//        fun formString(typeAsString: String): Type {
//            return values().firstOrNull { it.typeAsString == typeAsString } ?: UNKNOWN
//        }
//    }

    companion object {
        const val STATE_OFF = 0
        const val STATE_OK = 1
        const val STATE_INIT = 2
        const val STATE_ERROR = 3
        const val STATE_ALARM = 4

        fun nameOnly(name: String, room: String) = DeviceInfo(name, room, STATE_INIT, 0, 0)

        fun fromJson(json: String) = fromJson(JSONObject(json))

        fun fromJson(json: JSONObject): DeviceInfo? {
            return try {
                val name = json.getString("name")
                val state = json.getInt("state")

                val room = json.optString("room", "")!!
                val value = json.optInt("value")
                val signal = json.optInt("signal")
                DeviceInfo(name, room, state, value, signal)
            } catch (e : JSONException) {
                null
            }
        }
    }
}
