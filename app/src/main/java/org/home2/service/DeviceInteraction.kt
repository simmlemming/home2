package org.home2.service

import org.home2.BaseMqtt
import org.home2.DeviceInfo
import org.home2.NetworkResource

class DeviceInteraction(private val deviceName: String, private val mqtt: BaseMqtt, private val liveData: DeviceLiveData?) {

    fun on() = execute(DeviceCommand.on(deviceName, getCurrentDeviceInfo(liveData)))

    fun off() = execute(DeviceCommand.off(deviceName, getCurrentDeviceInfo(liveData)))

    fun reset() = execute(DeviceCommand.reset(deviceName, getCurrentDeviceInfo(liveData)))

    private fun execute(command: DeviceCommand) {
        mqtt.publish(IN_TOPIC, command.newMqttMessage().toString())

        val newValue = NetworkResource.loading(command.newExpectedDeviceInfo())
        liveData?.postValue(newValue)
    }

    private fun getCurrentDeviceInfo(liveData: DeviceLiveData?): DeviceInfo {
        return liveData?.value?.data ?: DeviceInfo.nameOnly(deviceName)
    }
}