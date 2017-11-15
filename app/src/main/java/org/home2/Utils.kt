package org.home2

import android.util.Log

/**
 * Created by mtkachenko on 13/11/17.
 */
fun <T : Any?> T.logi(prefix : String = "") {
    Log.i(TAG, "$prefix $this")
}

fun <T : Throwable> T.loge(message : String = "") {
    Log.e(TAG, message, this)
}
