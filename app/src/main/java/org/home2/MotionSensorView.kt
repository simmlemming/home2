package org.home2

import android.content.Context
import android.content.res.ColorStateList
import android.util.AttributeSet
import android.view.View
import android.widget.FrameLayout
import android.widget.Switch
import android.widget.TextView

/**
 * Created by mtkachenko on 21/10/17.
 */
class MotionSensorView : FrameLayout {

    private lateinit var rootLayout: View
    private lateinit var nameView: TextView
    private lateinit var switcherView: Switch
    private lateinit var waitingView: View


    @JvmOverloads
    constructor(context: Context, attributeSet: AttributeSet? = null, defStyleAttrs: Int = 0, styleRes: Int = 0) : super(context, attributeSet, defStyleAttrs, styleRes)

    override fun onFinishInflate() {
        super.onFinishInflate()
        View.inflate(context, R.layout.motion_sensor_view, this);

        rootLayout = findViewById(R.id.root)
        nameView = findViewById(R.id.name)
        switcherView = findViewById(R.id.switcher)
        waitingView = findViewById(R.id.waiting)
    }

    fun setInfo(info: NetworkResource<MotionSensorInfo>) {
        nameView.text = info.data?.name ?: "-"
        backgroundTintList = ColorStateList.valueOf(info.data.bgColor(context))
        waitingView.visibility = if (info.state == NetworkResource.State.LOADING) View.VISIBLE else View.GONE
        switcherView.isChecked = info.data?.status != 0
    }
}

private fun MotionSensorInfo?.bgColor(context: Context): Int {
    val resId = when {
        this == null -> R.color.sensor_unknown
        status == 0 -> R.color.sensor_off
        status == 1 -> R.color.sensor_ok
        status == 2 -> R.color.sensor_connecting
        status == 3 -> R.color.sensor_error
        status == 4 -> R.color.sensor_alarm
        else -> R.color.sensor_unknown
    }

    return context.resources.getColor(resId, context.theme)
}