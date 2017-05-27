package org.home2

import android.arch.lifecycle.LiveData

/**
 * Created by mtkachenko on 21/05/17.
 */
interface RoomInfoRepository {
    fun getRoomInfo(roomName: String): LiveData<RoomInfo>
}