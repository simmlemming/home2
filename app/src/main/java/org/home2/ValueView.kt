package org.home2

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.TextView
import kotlin.properties.Delegates

/**
 * Created by mtkachenko on 27/05/17.
 */
class ValueView @JvmOverloads constructor(context: Context, attributeSet: AttributeSet? = null, defStyleAttrs: Int = 0, styleRes: Int = 0) : BaseDeviceView(context, attributeSet, defStyleAttrs, styleRes) {
    var units: String = ""
    private lateinit var valueView: TextView

    private var value: Int by Delegates.observable(0, { _, _: Int, newValue: Int ->
        valueView.text = "$newValue $units"
    })

    override fun onChanged(info: NetworkResource<DeviceInfo>?) {
        super.onChanged(info)
        value = info?.data?.value ?: 0
    }

    override fun onFinishInflate() {
        super.onFinishInflate()
        View.inflate(context, R.layout.value_view, this)

        valueView = findViewById(R.id.value)
    }
}