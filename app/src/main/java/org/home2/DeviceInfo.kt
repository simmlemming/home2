package org.home2

import org.json.JSONException
import org.json.JSONObject
import java.io.Serializable

/**
 * Created by mtkachenko on 21/10/17.
 */
data class DeviceInfo(val name: String,
                      val room: String,
                      val state: Int,
                      val value: Int,
                      val signal: Int,
                      val timeUnixS: Long,
                      val wakeupTimeUnixS: Long,
                      val wakeupDelayS: Long,
                      val br: Int,
                      val r: Int,
                      val g: Int,
                      val b: Int,
                      val wakeupBr: Int,
                      val wakeupR: Int,
                      val wakeupG: Int,
                      val wakeupB: Int
) : Serializable {
    companion object {
        const val STATE_OFF = 0
        const val STATE_OK = 1
        const val STATE_INIT = 2
        const val STATE_ERROR = 3
        const val STATE_ALARM = 4
        const val STATE_ALARM_PENDING = 5
        const val STATE_PAUSED = 6

        fun nameOnly(name: String, room: String) = DeviceInfo(name, room, STATE_INIT, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0)

        fun fromJson(json: String) = fromJson(JSONObject(json))

        fun fromJson(json: JSONObject): DeviceInfo? {
            return try {
                DeviceInfo(
                        json.getString("name"),
                        json.optString("room", ""),
                        json.getInt("state"),
                        json.optInt("value"),
                        json.optInt("signal"),
                        json.optInt("time_unix_s").toLong() * 1000,
                        json.optInt("wakeup_time_ms").toLong(),
                        json.optInt("wakeup_delay_ms").toLong(),
                        json.optInt("br"),
                        json.optInt("r"),
                        json.optInt("g"),
                        json.optInt("b"),
                        json.optInt("wakeup_br"),
                        json.optInt("wakeup_r"),
                        json.optInt("wakeup_g"),
                        json.optInt("wakeup_b")
                )
            } catch (e: JSONException) {
                null
            }
        }
    }
}
