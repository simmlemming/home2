package org.home2

import android.app.Application
import org.home2.mqtt.Mqtt

/**
 * Created by mtkachenko on 27/05/17.
 */

const val TAG = "Home"
class HomeApplication : Application() {
    internal val mqtt = Mqtt(this)
}