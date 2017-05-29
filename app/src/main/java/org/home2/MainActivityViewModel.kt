package org.home2

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import android.arch.lifecycle.LiveData

/**
 * Created by mtkachenko on 20/05/17.
 */
class MainActivityViewModel(app : Application) : AndroidViewModel(app) {
    private val repo: RoomInfoRepository
    val room1Info: LiveData<RoomInfo>

    init {
        if (app is HomeApplication) {
            repo = app.mqttRoomInfoRepository
            room1Info = repo.getRoomInfo("nicole")
        } else {
            throw IllegalStateException("Application is not HomeApplication")
        }
    }

    fun getConnectionStatus() : LiveData<ConnectionState> {
        return repo.getConnectionStatus()
    }
}