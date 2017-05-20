package org.home2

import android.arch.lifecycle.LifecycleActivity
import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.widget.TextView

class MainActivity : LifecycleActivity() {
    lateinit var tempView: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        tempView = findViewById(R.id.temp) as TextView

        val viewModel = ViewModelProviders
                .of(this)
                .get(MainActivityViewModel::class.java)

        viewModel.temp.observe(this, Observer<Int> { temp -> updateUi(temp) })
    }

    private fun updateUi(t: Int?) {
        tempView.text = t.toString()
    }
}
