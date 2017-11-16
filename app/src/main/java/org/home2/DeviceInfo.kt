package org.home2

/**
 * Created by mtkachenko on 21/10/17.
 */
data class DeviceInfo(val name: String, val room: String, val state: Int, val value: Int, val signal: Int) {
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

        @JvmStatic
        fun nameOnly(name: String, room: String) = DeviceInfo(name, room, STATE_INIT, 0, 0)
    }
}
