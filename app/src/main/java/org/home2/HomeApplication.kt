package org.home2

import android.app.Application

/**
 * Created by mtkachenko on 27/05/17.
 */
class HomeApplication : Application() {
    internal val mqttRepository : MqttRoomInfoRepository = MqttRoomInfoRepository()
}