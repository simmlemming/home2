package org.home2

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.FrameLayout
import android.widget.Switch
import android.widget.TextView

/**
 * Created by mtkachenko on 21/10/17.
 */
class MotionSensorView : FrameLayout {
    private lateinit var nameView : TextView
    private lateinit var switcherView : Switch

    @JvmOverloads
    constructor(context: Context, attributeSet: AttributeSet? = null, defStyleAttrs : Int = 0, styleRes: Int = 0) : super(context, attributeSet, defStyleAttrs, styleRes)


    override fun onFinishInflate() {
        super.onFinishInflate()
        View.inflate(context, R.layout.motion_sensor_view, this);

        nameView = findViewById(R.id.name)
        switcherView = findViewById(R.id.switcher)
    }

    fun setName(name : String) {
        nameView.text = name
    }
}