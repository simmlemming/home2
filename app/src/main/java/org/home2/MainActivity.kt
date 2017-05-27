package org.home2

import android.arch.lifecycle.LifecycleActivity
import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle

class MainActivity : LifecycleActivity() {
    lateinit var room1MeterView : RoomMeterView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        lifecycle.addObserver(applicationContext.mqttRepository)

        setContentView(R.layout.activity_main)
        room1MeterView = findViewById(R.id.room_1) as RoomMeterView

        val viewModel = ViewModelProviders
                .of(this)
                .get(MainActivityViewModel::class.java)

        viewModel.roomInfo.observe(this, Observer<RoomInfo> { it -> updateUi(it) })

    }

    private fun updateUi(roomInfo: RoomInfo?) {
        room1MeterView.setRoomInfo(roomInfo ?: RoomInfo("?", 0, 0))
    }

    override fun onDestroy() {
        // Is it necessary?
        lifecycle.removeObserver(applicationContext.mqttRepository)

        super.onDestroy()
    }

    override fun getApplicationContext(): HomeApplication {
        return super.getApplicationContext() as HomeApplication
    }
}
