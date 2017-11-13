package org.home2

import android.content.Context
import android.content.SharedPreferences
import java.util.*

/**
 * Created by mtkachenko on 13/11/17.
 */
class HomeSettings(private val context: Context) {
    companion object {
        private const val KEY_INSTANCE_ID = "instance_id"
        private const val PREFS_NAME = "Home"
    }

    fun getInstanceId(): String {
        val preferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        return preferences.getStringOrElse(KEY_INSTANCE_ID) { UUID.randomUUID().toString() }
    }
}

private fun SharedPreferences.getStringOrElse(key: String, factory: (Unit) -> String): String {
    val value = getString(key, null)

    if (value != null) {
        return value
    }

    val newValue = factory.invoke(Unit)

    edit()
            .putString(key, newValue)
            .apply()

    return newValue
}