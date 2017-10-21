package org.home2

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.FrameLayout
import android.widget.TextView

/**
 * Created by mtkachenko on 27/05/17.
 */
class RoomMeterView : FrameLayout {
//    private var inited : Boolean = false
    private lateinit var tempView : TextView
    private lateinit var humView : TextView

    constructor(context: Context, attributeSet: AttributeSet) : super(context, attributeSet)

    constructor(context: Context, attributeSet: AttributeSet, defStyleAttrs : Int) : super(context, attributeSet, defStyleAttrs)

    constructor(context: Context, attributeSet: AttributeSet, defStyleAttrs : Int, styleRes: Int) : super(context, attributeSet, defStyleAttrs, styleRes)

    override fun onFinishInflate() {
        super.onFinishInflate()
        View.inflate(context, R.layout.room_meter_view, this)

        tempView = findViewById(R.id.temp)
        humView = findViewById(R.id.hum)
    }

    fun setRoomInfo(roomInfo: RoomInfo) {
        tempView.text = "${roomInfo.temp} C"
        humView.text = "${roomInfo.hum} %"
    }
}