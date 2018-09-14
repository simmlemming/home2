package org.home2

import android.arch.lifecycle.Observer
import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import org.home2.service.CameraLiveData

class CameraView @JvmOverloads constructor(context: Context, attributeSet: AttributeSet? = null, defStyleAttrs: Int = 0, styleRes: Int = 0)
    : FrameLayout(context, attributeSet, defStyleAttrs, styleRes), Observer<NetworkResource<CameraLiveData.CameraDeviceInfo>> {

    interface Listener {
        fun updatePicture(deviceName: String)
    }

    private lateinit var pictureView: ImageView
    private lateinit var errorView: TextView
    private lateinit var waitingView: View

    lateinit var name: String
    var listener: Listener? = null

    override fun onFinishInflate() {
        super.onFinishInflate()

        View.inflate(context, R.layout.camera_view, this)
        pictureView = findViewById(R.id.picture)
        errorView = findViewById(R.id.error)
        waitingView = findViewById(R.id.waiting)

        pictureView.setOnClickListener { listener?.updatePicture(name) }
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