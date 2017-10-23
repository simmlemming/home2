package org.home2

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Bundle
import android.os.IBinder
import android.support.v4.app.FragmentActivity
import android.widget.TextView

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
            service!!.motionSensorInfo.observe(this@MainActivity, Observer<NetworkResource<MotionSensorInfo>> {
                it?.let {
                    if (motionSensorView.name == it.data?.name) {
                        motionSensorView.setInfo(it)
                    }
                }
            })
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)
        room1MeterView = findViewById(R.id.room_1)
        motionSensorView = findViewById(R.id.motion_sensor)
        connectionStatusView = findViewById(R.id.connection_status)

        val viewModel = ViewModelProviders
                .of(this)
                .get(MainActivityViewModel::class.java)


        motionSensorView.listener = object : MotionSensorView.Listener {
            override fun switchOn(name: String) {
                service?.devices(DeviceFilter.withName(name))?.on()
            }

            override fun switchOff(name: String) {
                service?.devices(DeviceFilter.withName(name))?.off()
            }

            override fun reset(name: String) {
                service?.devices(DeviceFilter.withName(name))?.reset()
            }
        }

        motionSensorView.name = "living_motion_01"

        viewModel.room1Info.observe(this, Observer<RoomInfo> { updateUi(it) })
        viewModel.getConnectionStatus().observe(this, Observer<ConnectionState> {
            connectionStatusView.text = it.toString()
        })

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
