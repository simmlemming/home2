package org.home2.gcm

import android.util.Log
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import org.home2.TAG

/**
 * Created by mtkachenko on 21/11/17.
 */
class HomeMessagingService : FirebaseMessagingService() {
    override fun onMessageReceived(message: RemoteMessage) {
        Log.i(TAG, message.data.toString())
    }
}