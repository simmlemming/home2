package org.home2.mqtt

import org.eclipse.paho.client.mqttv3.MqttConnectOptions

class HomeConnectOptions : MqttConnectOptions() {
    init {
        isAutomaticReconnect = true
        isCleanSession = false
        connectionTimeout = 5;
    }
}