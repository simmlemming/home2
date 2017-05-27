package org.home2

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import android.arch.lifecycle.LiveData

/**
 * Created by mtkachenko on 20/05/17.
 */
class MainActivityViewModel(app : Application) : AndroidViewModel(app) {
    val roomInfoRepository : RoomInfoRepository
    var roomInfo: LiveData<RoomInfo>

    init {
        if (app is HomeApplication) {
            roomInfoRepository = app.mqttRepository
            roomInfo = roomInfoRepository.getRoomInfo("b")
        } else {
            throw IllegalStateException("Application is not HomeApplication")
        }
    }
}