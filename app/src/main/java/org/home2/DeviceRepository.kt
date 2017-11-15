package org.home2

import org.home2.service.HomeService

/**
 * Created by mtkachenko on 13/11/17.
 */
class DeviceRepository {
    private val devices = mutableMapOf<String, DeviceInfo>().apply {
        put("temp_sensor_01", DeviceInfo.nameOnly("temp_sensor_01"))
        put("humidity_sensor_01", DeviceInfo.nameOnly("humidity_sensor_01"))
        put("temp_sensor_02", DeviceInfo.nameOnly("temp_sensor_02"))
        put("humidity_sensor_02", DeviceInfo.nameOnly("humidity_sensor_02"))
        put("temp_sensor_03", DeviceInfo.nameOnly("temp_sensor_03"))
        put("humidity_sensor_03", DeviceInfo.nameOnly("humidity_sensor_03"))
        put("motion_sensor_01", DeviceInfo.nameOnly("motion_sensor_01"))
    }

    fun find(name: String): Collection<DeviceInfo> {
        return devices
                .filter({ entry -> name == HomeService.DEVICE_NAME_ALL || entry.key == name })
                .values
    }

    fun getAll() = find(HomeService.DEVICE_NAME_ALL)

    fun update(deviceInfo: DeviceInfo) {
        if (!devices.containsKey(deviceInfo.name)) {
            return
        }

        devices[deviceInfo.name] = deviceInfo
    }
}