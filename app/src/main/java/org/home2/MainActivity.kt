package org.home2

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

private const val SENSOR_NAME = "living_motion_01"

class MainActivity : FragmentActivity() {
    private lateinit var room1MeterView: RoomMeterView
    private lateinit var motionSensorView: MotionSensorView
    private lateinit var connectionStatusView: TextView

    private var service: HomeService? = null

    private val serviceConnection = object : ServiceConnection {
        override fun onServiceDisconnected(name: ComponentName?) {
            service = null
        }

        override fun onServiceConnected(name: ComponentName?, binder: IBinder?) {
            service = (binder as HomeService.HomeBinder).service
            service!!.connectionState.observe(this@MainActivity, Observer<ConnectionState> {
                connectionStatusView.text = it.toString()
            })

            service!!.observe(SENSOR_NAME, this@MainActivity, Observer<NetworkResource<DeviceInfo>> {
                it?.let {
                    motionSensorView.setInfo(it)
                }
            })

            service!!.device(HomeService.DEVICE_NAME_ALL).status()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)
        room1MeterView = findViewById(R.id.room_1)
        motionSensorView = findViewById(R.id.motion_sensor)
        connectionStatusView = findViewById(R.id.connection_status)

        motionSensorView.listener = object : MotionSensorView.Listener {
            override fun update(name: String) {
                service?.device(name)?.status()
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

        motionSensorView.name = SENSOR_NAME

        val homeService = Intent(this, HomeService::class.java)
        bindService(homeService, serviceConnection, Context.BIND_AUTO_CREATE)
    }

    override fun onDestroy() {
        unbindService(serviceConnection)
        super.onDestroy()
    }

    private fun updateUi(roomInfo: RoomInfo?) {
        room1MeterView.setRoomInfo(roomInfo ?: RoomInfo("?", 0, 0))
    }

    override fun getApplicationContext(): HomeApplication {
        return super.getApplicationContext() as HomeApplication
    }
}
