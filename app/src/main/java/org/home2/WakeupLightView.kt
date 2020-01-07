package org.home2

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.Button
import android.widget.SeekBar
import android.widget.TextView

/**
 * Created by mtkachenko on 21/10/17.
 */
class WakeupLightView @JvmOverloads constructor(context: Context, attributeSet: AttributeSet? = null, defStyleAttrs: Int = 0, styleRes: Int = 0)
    : BaseDeviceView(context, attributeSet, defStyleAttrs, styleRes) {

    interface Listener : BaseDeviceView.Listener {
        fun switchOn(name: String)
        fun switchOff(name: String)
        fun setValue(name: String, value: Int)
    }

    private lateinit var rootLayout: View
    private lateinit var onOffView: Button
    private lateinit var valueView: SeekBar
    private lateinit var valueTextView: TextView


    private val switchOnListener = SwitchOnOnClickListener()
    private val switchOffListener = SwitchOffOnClickListener()

    var listener: Listener? = null

    override fun onFinishInflate() {
        super.onFinishInflate()
        View.inflate(context, R.layout.wakeup_light_view, this);

        rootLayout = findViewById(R.id.root)
        onOffView = findViewById(R.id.on_off)
        valueView = findViewById(R.id.value)
        valueTextView = findViewById(R.id.value_txt)

        onOffView.setOnClickListener { _ ->
            listener?.switchOn(name)
        }

        rootLayout.setOnClickListener { _ ->
            listener?.update(name)
        }

        valueView.max = 100

        valueView.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onStartTrackingTouch(seekBar: SeekBar?) {
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
                listener?.setValue(name, seekBar!!.progress)
            }

            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                valueTextView.text = "$progress"
            }
        })
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

        valueView.progress = info?.data?.value ?: 0
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

