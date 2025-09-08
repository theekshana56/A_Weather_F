package com.example.weatherforecast

import android.os.Bundle
import com.example.weatherforecast.base.BaseActivity

class WeatherDetailedActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.weather_detailed)
        
        // Set optimized background
        setOptimizedBackground(R.drawable.ic_new_detailed)
        
        // Setup navigation
        setupNavigation()
    }
}


