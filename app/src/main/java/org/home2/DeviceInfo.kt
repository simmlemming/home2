package org.home2

/**
 * Created by mtkachenko on 21/10/17.
 */
data class DeviceInfo(val name : String, val state: Int, val value: Int) {
    companion object {
        const val STATE_OFF = 0
        const val STATE_OK = 1
        const val STATE_INIT = 2
        const val STATE_ERROR = 3
        const val STATE_ALARM = 4

        fun nameOnly(name: String) = DeviceInfo(name, STATE_INIT, 0)
    }
}
