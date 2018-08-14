package org.home2

import android.arch.lifecycle.Observer
import android.content.Context
import android.content.res.ColorStateList
import android.util.AttributeSet
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import android.widget.ViewFlipper
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

    //    private val nameView: TextView? by lazy { findViewById<TextView>(R.id.name) }
    private val signalWaitingViewFlipper: ViewFlipper by lazy { findViewById<ViewFlipper>(R.id.signal_waiting) }
    private val roomNameView: TextView by lazy { findViewById<TextView>(R.id.room) }
    private val signalView: ImageView by lazy { findViewById<ImageView>(R.id.signal) }

    var name: String by Delegates.observable("unknown") { _, _, newValue ->
        //        nameView?.text = newValue
    }

    override fun onChanged(info: NetworkResource<DeviceInfo>?) {
        backgroundTintList = ColorStateList.valueOf(info?.data.bgColor(context))
        signalWaitingViewFlipper.displayedChild = info.toDisplayedChild()
        roomNameView.text = info?.data?.room
        signalView.setWifiSignalStrength(info?.data?.signal ?: 0)
    }
}

private fun NetworkResource<DeviceInfo>?.toDisplayedChild() =
        when {
            this?.state == NetworkResource.State.LOADING -> 1
            this?.data?.state == DeviceInfo.STATE_PAUSED -> 2
            else -> 0
        }

private fun DeviceInfo?.bgColor(context: Context): Int {
    val resId = when {
        this == null -> R.color.sensor_unknown
        state == DeviceInfo.STATE_OFF -> R.color.sensor_off
        state == DeviceInfo.STATE_OK -> R.color.sensor_ok
        state == DeviceInfo.STATE_INIT -> R.color.sensor_connecting
        state == DeviceInfo.STATE_ERROR -> R.color.sensor_error
        state == DeviceInfo.STATE_ALARM -> R.color.sensor_alarm
        state == DeviceInfo.STATE_ALARM_PENDING -> R.color.sensor_connecting
        state == DeviceInfo.STATE_PAUSED -> R.color.sensor_off
        else -> R.color.sensor_unknown
    }

    return context.resources.getColor(resId, context.theme)
}

private fun ImageView.setWifiSignalStrength(signal: Int) {
    val level = Math.abs(signal)

    if (level == 0) {
        setImageLevel(Int.MAX_VALUE) // This shows empty drawable
    } else {
        setImageLevel(level)
    }
}