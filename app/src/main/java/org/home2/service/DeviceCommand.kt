package org.home2.service

import org.home2.DeviceInfo
import org.home2.DeviceRepository
import org.json.JSONObject

abstract class DeviceCommand(private val deviceName: String, private val deviceRepository: DeviceRepository) {
    companion object {
        fun on(deviceName: String, deviceRepository: DeviceRepository): DeviceCommand = object : DeviceCommand(deviceName, deviceRepository) {
            override fun mqttMessage() = mqttMessage(deviceName, "on")
            override fun expectedDeviceInfo(deviceInfo: DeviceInfo) = deviceInfo.copy(state = DeviceInfo.STATE_OK)
        }

        fun off(deviceName: String, deviceRepository: DeviceRepository): DeviceCommand = object : DeviceCommand(deviceName, deviceRepository) {
            override fun mqttMessage() = mqttMessage(deviceName, "off")
            override fun expectedDeviceInfo(deviceInfo: DeviceInfo) = deviceInfo.copy(state = DeviceInfo.STATE_OFF)
        }

        fun reset(deviceName: String, deviceRepository: DeviceRepository): DeviceCommand = object : DeviceCommand(deviceName, deviceRepository) {
            override fun mqttMessage() = mqttMessage(deviceName, "reset")
            override fun expectedDeviceInfo(deviceInfo: DeviceInfo) = deviceInfo.copy(state = DeviceInfo.STATE_OK)
        }

        fun state(deviceName: String, deviceRepository: DeviceRepository): DeviceCommand = object : DeviceCommand(deviceName, deviceRepository) {
            override fun mqttMessage() = mqttMessage(deviceName, "state")
            override fun expectedDeviceInfo(deviceInfo: DeviceInfo) = deviceInfo
        }

        fun pause(deviceName: String, deviceRepository: DeviceRepository, sec: Int): DeviceCommand = object : DeviceCommand(deviceName, deviceRepository) {
            override fun mqttMessage() = mqttMessage(deviceName, "pause", sec)
            override fun expectedDeviceInfo(deviceInfo: DeviceInfo) = deviceInfo
        }
    }

    abstract fun mqttMessage(): JSONObject
    protected abstract fun expectedDeviceInfo(deviceInfo: DeviceInfo): DeviceInfo

    protected fun mqttMessage(deviceName: String, cmd: String, value: Int? = null): JSONObject {
        return JSONObject().apply {
            put("name", deviceName)
            put("cmd", cmd)
            value?.let { put("value", it) }
        }
    }

    fun expectedDeviceInfoUpdates(): Collection<DeviceInfo> {
        val result: MutableCollection<DeviceInfo> = mutableSetOf()

        deviceRepository
                .find(deviceName)
                .forEach { device -> result.add(expectedDeviceInfo(device)) }

        return result
    }
}