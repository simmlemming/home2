package org.home2

import org.home2.service.HomeService

/**
 * Created by mtkachenko on 13/11/17.
 */
class DeviceRepository {
    private val devices = mutableMapOf<String, DeviceInfo>().apply {
        put("temp_sensor_01", DeviceInfo.nameOnly("temp_sensor_01", "brown_bedroom"))
        put("humidity_sensor_01", DeviceInfo.nameOnly("humidity_sensor_01", "brown_bedroom"))
        put("temp_sensor_02", DeviceInfo.nameOnly("temp_sensor_02", "violet_bedroom"))
        put("humidity_sensor_02", DeviceInfo.nameOnly("humidity_sensor_02", "violet_bedroom"))
        put("temp_sensor_03", DeviceInfo.nameOnly("temp_sensor_03", "green_bedroom"))
        put("humidity_sensor_03", DeviceInfo.nameOnly("humidity_sensor_03", "green_bedroom"))
        put("motion_sensor_01", DeviceInfo.nameOnly("motion_sensor_01", "living_room"))
        put("motion_sensor_02", DeviceInfo.nameOnly("motion_sensor_02", "corridor"))
        put("main_light", DeviceInfo.nameOnly("main_light", "living_room"))
        put("wakeup_light_01", DeviceInfo.nameOnly("wakeup_light_01", "brown_bedroom"))
    }

    fun find(name: String) = find { device -> name == HomeService.DEVICE_NAME_ALL || device.name == name }

    fun findAlarmed() = find { device -> device.state == DeviceInfo.STATE_ALARM }

    private fun find(filter: (DeviceInfo) -> Boolean): Collection<DeviceInfo> {
        return devices
                .filter { filter.invoke(it.value) }
                .values
    }

    fun getAll() = find(HomeService.DEVICE_NAME_ALL)

    fun update(deviceInfo: DeviceInfo) {
        if (!devices.containsKey(deviceInfo.name)) {
            return
        }

        devices[deviceInfo.name] = deviceInfo
    }

    fun add(deviceInfo: DeviceInfo) {
        devices.put(deviceInfo.name, deviceInfo)
    }
}