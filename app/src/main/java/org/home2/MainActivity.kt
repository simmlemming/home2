package org.home2

import android.app.PendingIntent
import android.arch.lifecycle.Observer
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Bundle
import android.os.IBinder
import android.support.v4.app.FragmentActivity
import android.widget.TextView
import org.home2.service.HomeService

private const val MOTION_SENSOR_NAME = "living_motion_01"
private const val TEMP_SENSOR_NAME = "temp_sensor_01"
private const val HUMIDITY_SENSOR_NAME = "humidity_sensor_01"

class MainActivity : FragmentActivity() {
    companion object {
        fun intentToOpen(context: Context): PendingIntent {
            val activity = Intent(context, MainActivity::class.java)
            return PendingIntent.getActivity(context, 0, activity, PendingIntent.FLAG_UPDATE_CURRENT)
        }
    }

    private lateinit var tempView: ValueView
    private lateinit var humView: ValueView

    private lateinit var motionSensorView: MotionSensorView
    private lateinit var connectionStatusView: TextView

    private var service: HomeService? = null

    private val baseDeviceListener = object : BaseDeviceView.Listener {
        override fun update(name: String) {
            service?.device(name)?.state()
        }
    }

    private val motionSensorListener = object : MotionSensorView.Listener {
        override fun update(name: String) {
            service?.device(name)?.state()
        }

        override fun switchOn(name: String) {
            service?.device(name)?.on()
        }

        override fun switchOff(name: String) {
            service?.device(name)?.off()
        }

        override fun reset(name: String) {
            service?.device(name)?.reset()
        }
    }

    private val serviceConnection = object : ServiceConnection {
        override fun onServiceDisconnected(name: ComponentName?) {
            service = null
        }

        override fun onServiceConnected(name: ComponentName?, binder: IBinder?) {
            service = (binder as HomeService.HomeBinder).service
            service!!.connectionState.observe(this@MainActivity, Observer<ConnectionState> {
                connectionStatusView.text = it.toString()
            })

            service!!.observe(MOTION_SENSOR_NAME, this@MainActivity, motionSensorView)
            service!!.observe(TEMP_SENSOR_NAME, this@MainActivity, tempView)
            service!!.observe(HUMIDITY_SENSOR_NAME, this@MainActivity, humView)

            service!!.device(HomeService.DEVICE_NAME_ALL).state()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)
        tempView = findViewById(R.id.temp)
        humView = findViewById(R.id.hum)
        motionSensorView = findViewById(R.id.motion_sensor)
        connectionStatusView = findViewById(R.id.connection_status)

        motionSensorView.name = MOTION_SENSOR_NAME
        tempView.name = TEMP_SENSOR_NAME
        tempView.units = "°C"
        humView.name = HUMIDITY_SENSOR_NAME
        humView.units = "%"

        motionSensorView.listener = motionSensorListener
        tempView.listener = baseDeviceListener
        humView.listener = baseDeviceListener

        val homeService = Intent(this, HomeService::class.java)
        startService(homeService)
        bindService(homeService, serviceConnection, Context.BIND_AUTO_CREATE)
    }

    override fun onDestroy() {
        unbindService(serviceConnection)
        super.onDestroy()
    }

    override fun getApplicationContext(): HomeApplication {
        return super.getApplicationContext() as HomeApplication
    }
}
