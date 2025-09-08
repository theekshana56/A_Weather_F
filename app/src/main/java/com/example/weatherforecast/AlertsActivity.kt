package com.example.weatherforecast

import android.os.Bundle
import com.example.weatherforecast.base.BaseActivity

class AlertsActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.alerts_screen)

        // Setup navigation
        setupNavigation()
    }
}


