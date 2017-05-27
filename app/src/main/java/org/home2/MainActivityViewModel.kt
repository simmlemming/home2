package org.home2

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.ViewModel

/**
 * Created by mtkachenko on 20/05/17.
 */
class MainActivityViewModel(val tempRepository: RoomInfoRepository) : ViewModel() {
    var roomInfo: LiveData<RoomInfo> = tempRepository.getRoomInfo("bedroom")

    @Suppress("unused")
    constructor() : this(FakeRoomInfoRepository())
}