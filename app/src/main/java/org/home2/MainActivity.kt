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
import java.util.*

private const val MOTION_SENSOR_NAME_01 = "motion_sensor_01"
private const val MOTION_SENSOR_NAME_02 = "motion_sensor_02"
private const val TEMP_SENSOR_NAME_01 = "temp_sensor_01"
private const val HUMIDITY_SENSOR_NAME_01 = "humidity_sensor_01"
private const val TEMP_SENSOR_NAME_02 = "temp_sensor_02"
private const val HUMIDITY_SENSOR_NAME_02 = "humidity_sensor_02"
private const val TEMP_SENSOR_NAME_03 = "temp_sensor_03"
private const val HUMIDITY_SENSOR_NAME_03 = "humidity_sensor_03"
const val CAMERA_NAME_01 = "camera_01"
const val CAMERA_NAME_02 = "camera_02"
const val MAIN_LIGHT_NAME = "main_light"
const val WAKEUP_LIGHT_NAME_01 = "wakeup_light_01"

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

    private lateinit var motionSensor01View: MotionSensorView
    private lateinit var motionSensor02View: MotionSensorView

    private lateinit var cameraO1View: CameraView
    private lateinit var cameraO2View: CameraView

    private lateinit var mainLightView: LightView
    private lateinit var wakeupLight01View: WakeupLightView

    private lateinit var connectionStatusView: TextView

    private var service: HomeService? = null
    private lateinit var notificationController: NotificationController

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

        override fun pause(name: String, sec: Int) {
            service?.device(name)?.pause(sec)
        }
    }

    private val cameraListener = object : CameraView.Listener {
        override fun updatePicture(deviceName: String, timestamp: Date?) {
            service!!.refreshPicture(deviceName, timestamp ?: Date())
        }
    }

    private val mainLightListener = object : LightView.Listener {
        override fun switchOn(name: String) {
            service?.device(name)?.on()
        }

        override fun switchOff(name: String) {
            service?.device(name)?.off()
        }

        override fun setValue(name: String, value: Int) {
            service?.device(name)?.value(value)
        }

        override fun update(name: String) {
            service?.device(name)?.state()
        }

    }

    private val wakeupLightListener = object : WakeupLightView.Listener {
        override fun switchOn(name: String) {
        }

        override fun switchOff(name: String) {
        }

        override fun setValue(name: String, value: Int) {
        }

        override fun update(name: String) {
            service?.device(name)?.state()
        }
    }

    private val serviceConnection = object : ServiceConnection {
        override fun onServiceDisconnected(name: ComponentName?) {
            service = null
        }

        override fun onServiceConnected(name: ComponentName?, binder: IBinder?) {
            service = (binder as HomeService.HomeBinder).service
            service!!.liveConnectionState.observe(this@MainActivity, Observer<ConnectionState> {
                connectionStatusView.text = it.toString()
            })

            service!!.observe(MOTION_SENSOR_NAME_01, this@MainActivity, motionSensor01View)
            service!!.observe(MOTION_SENSOR_NAME_02, this@MainActivity, motionSensor02View)
            service!!.observe(TEMP_SENSOR_NAME_01, this@MainActivity, temp01View)
            service!!.observe(HUMIDITY_SENSOR_NAME_01, this@MainActivity, hum01View)
            service!!.observe(TEMP_SENSOR_NAME_02, this@MainActivity, temp02View)
            service!!.observe(HUMIDITY_SENSOR_NAME_02, this@MainActivity, hum02View)
            service!!.observe(TEMP_SENSOR_NAME_03, this@MainActivity, temp03View)
            service!!.observe(HUMIDITY_SENSOR_NAME_03, this@MainActivity, hum03View)
            service!!.observeCamera(CAMERA_NAME_01, this@MainActivity, cameraO1View)
            service!!.observeCamera(CAMERA_NAME_02, this@MainActivity, cameraO2View)
            service!!.observe(MAIN_LIGHT_NAME, this@MainActivity, mainLightView)
            service!!.observe(WAKEUP_LIGHT_NAME_01, this@MainActivity, wakeupLight01View)

            service!!.device(HomeService.DEVICE_NAME_ALL).state()

            service!!.refreshPicture(CAMERA_NAME_01, Date())
            service!!.refreshPicture(CAMERA_NAME_02, Date())
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
        motionSensor01View = findViewById(R.id.motion_sensor)
        motionSensor02View = findViewById(R.id.motion_sensor_02)
        cameraO1View = findViewById(R.id.camera_01)
        cameraO2View = findViewById(R.id.camera_02)
        mainLightView = findViewById(R.id.main_light)
        wakeupLight01View = findViewById(R.id.wakeup_light_01)

        connectionStatusView = findViewById(R.id.connection_status)

        notificationController = applicationContext.notificationController

        motionSensor01View.name = MOTION_SENSOR_NAME_01
        motionSensor02View.name = MOTION_SENSOR_NAME_02

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

        motionSensor01View.listener = motionSensorListener
        motionSensor02View.listener = motionSensorListener

        temp01View.listener = baseDeviceListener
        hum01View.listener = baseDeviceListener

        temp02View.listener = baseDeviceListener
        hum02View.listener = baseDeviceListener

        temp03View.listener = baseDeviceListener
        hum03View.listener = baseDeviceListener

        cameraO1View.name = CAMERA_NAME_01
        cameraO1View.listener = cameraListener

        cameraO2View.name = CAMERA_NAME_02
        cameraO2View.listener = cameraListener

        mainLightView.name = MAIN_LIGHT_NAME
        mainLightView.listener = mainLightListener

        wakeupLight01View.name = WAKEUP_LIGHT_NAME_01
        wakeupLight01View.listener = wakeupLightListener

        val homeService = Intent(this, HomeService::class.java)
        bindService(homeService, serviceConnection, Context.BIND_AUTO_CREATE)

        applicationContext.sendGcmToken()
    }

    override fun onStart() {
        super.onStart()
        notificationController.cancelNotification()
        notificationController.muteAllNotifications = true
    }

    override fun onStop() {
        notificationController.muteAllNotifications = false
        super.onStop()
    }

    override fun onDestroy() {
        unbindService(serviceConnection)
        super.onDestroy()
    }

    override fun getApplicationContext(): HomeApplication {
        return super.getApplicationContext() as HomeApplication
    }
}
