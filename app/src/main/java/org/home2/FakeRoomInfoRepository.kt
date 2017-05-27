package org.home2

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import java.util.*

/**
 * Created by mtkachenko on 20/05/17.
 */
class FakeRoomInfoRepository : RoomInfoRepository {
    val timer = Timer()
    val rand = Random()
    val data = MutableLiveData<RoomInfo>()

    init {
        timer.scheduleAtFixedRate(
                object : TimerTask() {
                    override fun run() {
                        val temp = rand.nextInt(15) + 15
                        val hum = rand.nextInt(15) + 30
                        data.postValue(RoomInfo("bedroom", temp, hum))
                    }
                },
                1000L,
                1000L
        )
    }

    override fun getRoomInfo(roomName: String): LiveData<RoomInfo> {
        return data
    }
}