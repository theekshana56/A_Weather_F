package com.example.weatherforecast

import android.os.Bundle
import com.example.weatherforecast.base.BaseActivity

class WeatherRadarActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.weather_radar)

        // Setup navigation
        setupNavigation()
    }
}


