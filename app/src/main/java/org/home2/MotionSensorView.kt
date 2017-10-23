package org.home2

import android.content.Context
import android.content.res.ColorStateList
import android.util.AttributeSet
import android.view.View
import android.widget.Button
import android.widget.FrameLayout
import android.widget.TextView
import kotlin.properties.Delegates

/**
 * Created by mtkachenko on 21/10/17.
 */
class MotionSensorView : FrameLayout {
    interface Listener {
        fun switchOn(name : String)
        fun switchOff(name : String)
        fun reset(name : String)
    }

    private lateinit var rootLayout: View
    private lateinit var nameView: TextView
    private lateinit var onView : Button
    private lateinit var offView : Button
    private lateinit var resetView : Button
    private lateinit var waitingView: View

    var listener : Listener? = null
    var name : String by Delegates.observable("unknown") {_, _, newValue ->
        nameView.text = newValue
    }


    @JvmOverloads
    constructor(context: Context, attributeSet: AttributeSet? = null, defStyleAttrs: Int = 0, styleRes: Int = 0) : super(context, attributeSet, defStyleAttrs, styleRes)

    override fun onFinishInflate() {
        super.onFinishInflate()
        View.inflate(context, R.layout.motion_sensor_view, this);

        rootLayout = findViewById(R.id.root)
        nameView = findViewById(R.id.name)
        onView = findViewById(R.id.on)
        offView = findViewById(R.id.off)
        resetView = findViewById(R.id.reset)
        waitingView = findViewById(R.id.waiting)

        onView.setOnClickListener { _ ->
            listener?.switchOn(name)
        }

        offView.setOnClickListener { _ ->
            listener?.switchOff(name)
        }

        resetView.setOnClickListener { _ ->
            listener?.reset(name)
        }
    }

    fun setInfo(info: NetworkResource<MotionSensorInfo>) {
        backgroundTintList = ColorStateList.valueOf(info.data.bgColor(context))
        waitingView.visibility = if (info.state == NetworkResource.State.LOADING) View.VISIBLE else View.GONE
    }
}

private fun MotionSensorInfo?.bgColor(context: Context): Int {
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