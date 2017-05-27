package org.home2

import android.arch.lifecycle.*
import android.util.Log
import java.util.*

/**
 * Created by mtkachenko on 20/05/17.
 */
class MqttRoomInfoRepository() : RoomInfoRepository, LifecycleObserver {
    val rand = Random()
    val data = MutableLiveData<RoomInfo>()

    var timer : Timer? = null

    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    fun start() {
        timer = Timer()
        timer!!.scheduleAtFixedRate(
                object : TimerTask() {
                    override fun run() {
                        Log.i("Home", "run")
                        val temp = rand.nextInt(15) + 15
                        val hum = rand.nextInt(15) + 30
                        data.postValue(RoomInfo("bedroom", temp, hum))
                    }
                },
                1000L,
                1000L
        )
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    fun stop() {
        timer?.cancel()
    }

    override fun getRoomInfo(roomName: String): LiveData<RoomInfo> {
        return data
    }
}