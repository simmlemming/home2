package org.home2.service

import android.arch.lifecycle.MutableLiveData
import org.home2.BaseMqtt
import org.home2.DeviceInfo
import org.home2.DeviceRepository
import org.home2.NetworkResource

class DeviceInteraction(private val deviceName: String, private val mqtt: BaseMqtt, private val deviceRepository: DeviceRepository, private val liveData: Map<String, MutableLiveData<NetworkResource<DeviceInfo>>>) {

    fun on() = execute(DeviceCommand.on(deviceName, deviceRepository))

    fun off() = execute(DeviceCommand.off(deviceName, deviceRepository))

    fun reset() = execute(DeviceCommand.reset(deviceName, deviceRepository))

    fun state() = execute(DeviceCommand.state(deviceName, deviceRepository))

    fun pause(sec: Int) = execute(DeviceCommand.pause(deviceName, deviceRepository, sec))

    private fun execute(command: DeviceCommand) {
        mqtt.publish(HomeService.IN_TOPIC, command.mqttMessage().toString())

        command.expectedDeviceInfoUpdates().forEach({ device ->
            liveData[device.name]?.postValue(NetworkResource.loading(device))
        })
    }
}