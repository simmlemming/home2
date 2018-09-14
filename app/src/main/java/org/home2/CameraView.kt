package org.home2

import android.arch.lifecycle.Observer
import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import org.home2.service.CameraLiveData
import java.util.*
import kotlin.properties.Delegates

class CameraView @JvmOverloads constructor(context: Context, attributeSet: AttributeSet? = null, defStyleAttrs: Int = 0, styleRes: Int = 0)
    : FrameLayout(context, attributeSet, defStyleAttrs, styleRes), Observer<NetworkResource<CameraLiveData.CameraDeviceInfo>> {

    interface Listener {
        fun updatePicture(deviceName: String, timestamp: Date?)
    }

    private lateinit var pictureView: ImageView
    private lateinit var errorView: TextView
    private lateinit var waitingView: View

    private lateinit var minusHourView: View
    private lateinit var minusTenMinView: View
    private lateinit var minusMinView: View
    private lateinit var minusTenSecView: View
    private lateinit var minusFiveSecView: View
    private lateinit var plusHourView: View
    private lateinit var plusTenMinView: View
    private lateinit var plusMinView: View
    private lateinit var plusTenSecView: View
    private lateinit var plusFiveSecView: View
//    private lateinit var nowView: View

    lateinit var name: String
    var listener: Listener? = null

    private var seekDate by Delegates.observable<Date?>(null) { _, _, newValue ->
        listener?.updatePicture(name, newValue)
    }

    override fun onFinishInflate() {
        super.onFinishInflate()

        View.inflate(context, R.layout.camera_view, this)
        pictureView = findViewById(R.id.picture)
        errorView = findViewById(R.id.error)
        waitingView = findViewById(R.id.waiting)

        minusHourView = findViewById(R.id.minus_hour)
        minusTenMinView = findViewById(R.id.minus_ten_min)
        minusMinView = findViewById(R.id.minus_min)
        minusTenSecView = findViewById(R.id.minus_ten_sec)
        minusFiveSecView = findViewById(R.id.minus_five_sec)
        plusHourView = findViewById(R.id.plus_hour)
        plusTenMinView = findViewById(R.id.plus_ten_min)
        plusMinView = findViewById(R.id.plus_min)
        plusTenSecView = findViewById(R.id.plus_ten_sec)
        plusFiveSecView = findViewById(R.id.plus_five_sec)
//        nowView = findViewById(R.id.now)

        pictureView.setOnClickListener { seekDate = null }

        minusHourView.setOnClickListener { seekBy(-3600) }
        minusTenMinView.setOnClickListener { seekBy(-600) }
        minusMinView.setOnClickListener { seekBy(-60) }
        minusTenSecView.setOnClickListener { seekBy(-10) }
        minusFiveSecView.setOnClickListener { seekBy(-5) }
        plusHourView.setOnClickListener { seekBy(3600) }
        plusTenMinView.setOnClickListener { seekBy(600) }
        plusMinView.setOnClickListener { seekBy(60) }
        plusTenSecView.setOnClickListener { seekBy(10) }
        plusFiveSecView.setOnClickListener { seekBy(5) }

//        nowView.setOnClickListener { seekDate = null }
    }

    private fun seekBy(sec: Int) {
        val calendar = Calendar.getInstance()
        seekDate?.let { calendar.time = it }

        calendar.add(Calendar.SECOND, sec)
        seekDate = calendar.time
    }

    override fun onChanged(resource: NetworkResource<CameraLiveData.CameraDeviceInfo>?) {
        when (resource?.state) {
            NetworkResource.State.SUCCESS -> {
                pictureView.setImageBitmap(resource.data!!.picture)
                errorView.visibility = View.GONE
                waitingView.visibility = View.GONE
            }

            NetworkResource.State.ERROR -> {
                pictureView.setImageBitmap(null)
                errorView.text = resource.exception?.message
                errorView.visibility = View.VISIBLE
                waitingView.visibility = View.GONE
            }

            NetworkResource.State.LOADING -> {
                errorView.visibility = View.GONE
                waitingView.visibility = View.VISIBLE
            }
        }
    }
}