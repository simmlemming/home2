package org.home2

import android.arch.lifecycle.Observer
import android.content.Context
import android.content.res.ColorStateList
import android.util.AttributeSet
import android.view.View
import android.widget.FrameLayout
import android.widget.TextView
import kotlin.properties.Delegates

/**
 * Created by mtkachenko on 29/10/17.
 */
abstract class BaseDeviceView @JvmOverloads constructor(context: Context, attributeSet: AttributeSet? = null, defStyleAttrs: Int = 0, styleRes: Int = 0)
    : FrameLayout(context, attributeSet, defStyleAttrs, styleRes),
        Observer<NetworkResource<DeviceInfo>> {

    interface Listener {
        fun update(name: String)
    }

    private val nameView: TextView by lazy { findViewById<TextView>(R.id.name) }
    private val waitingView: View by lazy { findViewById<View>(R.id.waiting) }

    var name: String by Delegates.observable("unknown") { _, _, newValue ->
        nameView.text = newValue
    }

    override fun onChanged(info: NetworkResource<DeviceInfo>?) {
        backgroundTintList = ColorStateList.valueOf(info?.data.bgColor(context))
        waitingView.visibility = if (info?.state == NetworkResource.State.LOADING) View.VISIBLE else View.GONE
    }
}

private fun DeviceInfo?.bgColor(context: Context): Int {
    val resId = when {
        this == null -> R.color.sensor_unknown
        state == 0 -> R.color.sensor_off
        state == 1 -> R.color.sensor_ok
        state == 2 -> R.color.sensor_connecting
        state == 3 -> R.color.sensor_error
        state == 4 -> R.color.sensor_alarm
        else -> R.color.sensor_unknown
    }

    return context.resources.getColor(resId, context.theme)
}
