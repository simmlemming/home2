package org.home2.gcm

import com.google.firebase.iid.FirebaseInstanceIdService
import org.home2.HomeApplication

/**
 * Created by mtkachenko on 21/11/17.
 */
class HomeInstanceIdService : FirebaseInstanceIdService() {

    override fun onCreate() {
        super.onCreate()
    }

    override fun onTokenRefresh() {
        super.onTokenRefresh()
        (applicationContext as HomeApplication).sendGcmToken()
    }
}