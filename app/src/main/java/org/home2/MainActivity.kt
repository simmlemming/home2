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

private const val MOTION_SENSOR_NAME = "motion_sensor_01"
private const val TEMP_SENSOR_NAME_01 = "temp_sensor_01"
private const val HUMIDITY_SENSOR_NAME_01 = "humidity_sensor_01"
private const val TEMP_SENSOR_NAME_02 = "temp_sensor_02"
private const val HUMIDITY_SENSOR_NAME_02 = "humidity_sensor_02"
private const val TEMP_SENSOR_NAME_03 = "temp_sensor_03"
private const val HUMIDITY_SENSOR_NAME_03 = "humidity_sensor_03"

class MainActivity : FragmentActivity() {
    companion object {
        fun intentToOpen(context: Context): PendingIntent {
            val activity = Intent(context, MainActivity::class.java)
            return PendingIntent.getActivity(context, 0, activity, PendingIntent.FLAG_UPDATE_CURRENT)
        }
    }

    private lateinit var temp01View: ValueView
    private lateinit var hum01View: ValueView
    private lateinit var temp02View: ValueView
    private lateinit var hum02View: ValueView
    private lateinit var temp03View: ValueView
    private lateinit var hum03View: ValueView

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
            service!!.observe(TEMP_SENSOR_NAME_01, this@MainActivity, temp01View)
            service!!.observe(HUMIDITY_SENSOR_NAME_01, this@MainActivity, hum01View)
            service!!.observe(TEMP_SENSOR_NAME_02, this@MainActivity, temp02View)
            service!!.observe(HUMIDITY_SENSOR_NAME_02, this@MainActivity, hum02View)
            service!!.observe(TEMP_SENSOR_NAME_03, this@MainActivity, temp03View)
            service!!.observe(HUMIDITY_SENSOR_NAME_03, this@MainActivity, hum03View)

            service!!.device(HomeService.DEVICE_NAME_ALL).state()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)
        temp01View = findViewById(R.id.temp_01)
        hum01View = findViewById(R.id.hum_01)
        temp02View = findViewById(R.id.temp_02)
        hum02View = findViewById(R.id.hum_02)
        temp03View = findViewById(R.id.temp_03)
        hum03View = findViewById(R.id.hum_03)
        motionSensorView = findViewById(R.id.motion_sensor)
        connectionStatusView = findViewById(R.id.connection_status)

        motionSensorView.name = MOTION_SENSOR_NAME

        temp01View.name = TEMP_SENSOR_NAME_01
        temp01View.units = "°C"
        hum01View.name = HUMIDITY_SENSOR_NAME_01
        hum01View.units = "%"

        temp02View.name = TEMP_SENSOR_NAME_02
        temp02View.units = "°C"
        hum02View.name = HUMIDITY_SENSOR_NAME_02
        hum02View.units = "%"

        temp03View.name = TEMP_SENSOR_NAME_03
        temp03View.units = "°C"
        hum03View.name = HUMIDITY_SENSOR_NAME_03
        hum03View.units = "%"

        motionSensorView.listener = motionSensorListener

        temp01View.listener = baseDeviceListener
        hum01View.listener = baseDeviceListener

        temp02View.listener = baseDeviceListener
        hum02View.listener = baseDeviceListener

        temp03View.listener = baseDeviceListener
        hum03View.listener = baseDeviceListener

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
