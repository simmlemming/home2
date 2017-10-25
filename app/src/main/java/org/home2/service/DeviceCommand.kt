package org.home2.service

import org.home2.DeviceInfo
import org.json.JSONObject

abstract class DeviceCommand(private val deviceName: String, private val liveData: Map<String, DeviceLiveData>) {
    companion object {
        fun on(deviceName: String, liveData: Map<String, DeviceLiveData>): DeviceCommand = object : DeviceCommand(deviceName, liveData) {
            override fun mqttMessage() = mqttMessage(deviceName, "on")
            override fun expectedDeviceInfo(deviceInfo: DeviceInfo) = deviceInfo.copy(state = DeviceInfo.STATE_OK)
        }

        fun off(deviceName: String, liveData: Map<String, DeviceLiveData>): DeviceCommand = object : DeviceCommand(deviceName, liveData) {
            override fun mqttMessage() = mqttMessage(deviceName, "off")
            override fun expectedDeviceInfo(deviceInfo: DeviceInfo) = deviceInfo.copy(state = DeviceInfo.STATE_OFF)
        }

        fun reset(deviceName: String, liveData: Map<String, DeviceLiveData>): DeviceCommand = object : DeviceCommand(deviceName, liveData) {
            override fun mqttMessage() = mqttMessage(deviceName, "reset")
            override fun expectedDeviceInfo(deviceInfo: DeviceInfo) = deviceInfo.copy(state = DeviceInfo.STATE_OK)
        }

        fun status(deviceName: String, liveData: Map<String, DeviceLiveData>): DeviceCommand = object : DeviceCommand(deviceName, liveData) {
            override fun mqttMessage() = mqttMessage(deviceName, "status")
            override fun expectedDeviceInfo(deviceInfo: DeviceInfo) = deviceInfo
        }
    }

    abstract fun mqttMessage(): JSONObject
    protected abstract fun expectedDeviceInfo(deviceInfo: DeviceInfo): DeviceInfo

    protected fun mqttMessage(deviceName: String, cmd: String): JSONObject {
        return JSONObject().apply {
            put("name", deviceName)
            put("cmd", cmd)
        }
    }

    fun expectedDeviceInfoUpdates(): Map<DeviceInfo, DeviceLiveData> {
        val result: MutableMap<DeviceInfo, DeviceLiveData> = mutableMapOf()

        liveData.filter { entry -> entry.key == deviceName || HomeService.DEVICE_NAME_ALL == deviceName }
                .forEach({ entry ->
                    val expectedDeviceInfo = expectedDeviceInfo(entry.value.value?.data ?: DeviceInfo.nameOnly(deviceName))
                    result[expectedDeviceInfo] = entry.value
                })


        return result
    }
}