package org.home2

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.Button

/**
 * Created by mtkachenko on 21/10/17.
 */
class MotionSensorView @JvmOverloads constructor(context: Context, attributeSet: AttributeSet? = null, defStyleAttrs: Int = 0, styleRes: Int = 0)
    : BaseDeviceView(context, attributeSet, defStyleAttrs, styleRes) {
    interface Listener {
        fun switchOn(name: String)
        fun switchOff(name: String)
        fun reset(name: String)
        fun update(name: String)
    }

    private lateinit var rootLayout: View
    private lateinit var onOffView: Button
    private lateinit var resetView: Button
    private lateinit var updateView: Button
    private lateinit var waitingView: View

    private val switchOnListener = SwitchOnOnClickListener()
    private val switchOffListener = SwitchOffOnClickListener()

    var listener: Listener? = null

    override fun onFinishInflate() {
        super.onFinishInflate()
        View.inflate(context, R.layout.motion_sensor_view, this);

        rootLayout = findViewById(R.id.root)
        onOffView = findViewById(R.id.on_off)
        resetView = findViewById(R.id.reset)
        updateView = findViewById(R.id.update)
        waitingView = findViewById(R.id.waiting)

        onOffView.setOnClickListener { _ ->
            listener?.switchOn(name)
        }

        resetView.setOnClickListener { _ ->
            listener?.reset(name)
        }

        updateView.setOnClickListener { _ ->
            listener?.update(name)
        }
    }

    override fun onChanged(info: NetworkResource<DeviceInfo>?) {
        super.onChanged(info)

        if (info?.data?.state == DeviceInfo.STATE_OFF) {
            onOffView.text = context.getString(R.string.on)
            onOffView.setOnClickListener(switchOnListener)
        } else {
            onOffView.text = context.getString(R.string.off)
            onOffView.setOnClickListener(switchOffListener)
        }
    }

    private inner class SwitchOnOnClickListener : OnClickListener {
        override fun onClick(v: View?) {
            listener?.switchOn(name)
        }
    }

    private inner class SwitchOffOnClickListener : OnClickListener {
        override fun onClick(v: View?) {
            listener?.switchOff(name)
        }
    }
}

