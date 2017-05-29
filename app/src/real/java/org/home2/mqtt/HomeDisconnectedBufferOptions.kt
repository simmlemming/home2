package org.home2.mqtt

import org.eclipse.paho.client.mqttv3.DisconnectedBufferOptions

class HomeDisconnectedBufferOptions : DisconnectedBufferOptions() {
    init {
        isBufferEnabled = true
        bufferSize = 100
        isPersistBuffer = false
        isDeleteOldestMessages = false
    }
}