package org.home2.service

import org.home2.DeviceInfo
import org.json.JSONObject

abstract class DeviceCommand {
    companion object {
        fun on(deviceName: String, currentDeviceInfo: DeviceInfo): DeviceCommand = object : DeviceCommand() {
            override fun newMqttMessage() = newMqttMessage(deviceName, "on")
            override fun newExpectedDeviceInfo() = currentDeviceInfo.copy(state = DeviceInfo.STATE_OK)
        }

        fun off(deviceName: String, currentDeviceInfo: DeviceInfo): DeviceCommand = object : DeviceCommand() {
            override fun newMqttMessage() = newMqttMessage(deviceName, "off")
            override fun newExpectedDeviceInfo() = currentDeviceInfo.copy(state = DeviceInfo.STATE_OFF)
        }

        fun reset(deviceName: String, currentDeviceInfo: DeviceInfo): DeviceCommand = object : DeviceCommand() {
            override fun newMqttMessage() = newMqttMessage(deviceName, "reset")
            override fun newExpectedDeviceInfo() = currentDeviceInfo.copy(state = DeviceInfo.STATE_OK)
        }
    }

    abstract fun newMqttMessage(): JSONObject
    abstract fun newExpectedDeviceInfo(): DeviceInfo

    protected fun newMqttMessage(deviceName: String, cmd: String): JSONObject {
        return JSONObject().apply {
            put("name", deviceName)
            put("cmd", cmd)
        }
    }
}