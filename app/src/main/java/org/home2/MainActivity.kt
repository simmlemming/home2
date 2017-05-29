package org.home2

import android.arch.lifecycle.LifecycleActivity
import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.widget.TextView

class MainActivity : LifecycleActivity() {
    lateinit var room1MeterView : RoomMeterView
    lateinit var connectionStatusView : TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)
        room1MeterView = findViewById(R.id.room_1) as RoomMeterView
        connectionStatusView = findViewById(R.id.connection_status) as TextView

        // How to get rid of this?
        lifecycle.addObserver(applicationContext.mqttRoomInfoRepository)

        val viewModel = ViewModelProviders
                .of(this)
                .get(MainActivityViewModel::class.java)

        viewModel.room1Info.observe(this, Observer<RoomInfo> {updateUi(it) })
        viewModel.getConnectionStatus().observe(this, Observer<ConnectionState> {
            connectionStatusView.text = it.toString()
        })
    }

    private fun updateUi(roomInfo: RoomInfo?) {
        room1MeterView.setRoomInfo(roomInfo ?: RoomInfo("?", 0, 0))
    }

    override fun getApplicationContext(): HomeApplication {
        return super.getApplicationContext() as HomeApplication
    }
}
