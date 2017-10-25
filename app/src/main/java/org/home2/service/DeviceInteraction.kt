package org.home2.service

import org.home2.BaseMqtt
import org.home2.NetworkResource

class DeviceInteraction(private val deviceName: String, private val mqtt: BaseMqtt, private val liveData: Map<String, DeviceLiveData>) {

    fun on() = execute(DeviceCommand.on(deviceName, liveData))

    fun off() = execute(DeviceCommand.off(deviceName, liveData))

    fun reset() = execute(DeviceCommand.reset(deviceName, liveData))

    fun status() = execute(DeviceCommand.status(deviceName, liveData))

    private fun execute(command: DeviceCommand) {
        mqtt.publish(HomeService.IN_TOPIC, command.mqttMessage().toString())

        command.expectedDeviceInfoUpdates().forEach({ entry ->
            entry.value.postValue(NetworkResource.loading(entry.key))
        })
    }
}