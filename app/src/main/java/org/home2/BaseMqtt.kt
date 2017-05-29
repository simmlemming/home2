package org.home2

import android.arch.lifecycle.MutableLiveData

/**
 * Created by mtkachenko on 29/05/17.
 */
open abstract class BaseMqtt() {
    val connectionStatus = MutableLiveData<ConnectionState>()

    abstract fun subscribe(topic: String, listener: (String) -> Unit)
    abstract fun unsubscribe(topic: String)
    abstract fun connect()
    abstract fun disconnect()

    init {
        connectionStatus.value = ConnectionState.DISCONNECTED
    }
}