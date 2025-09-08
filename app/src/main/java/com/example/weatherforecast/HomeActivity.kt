package com.example.weatherforecast

import android.content.Intent
import android.os.Bundle
import android.view.View
import com.example.weatherforecast.base.BaseActivity

class HomeActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.home_screen)
        
        // Set optimized background
        setOptimizedBackground(R.drawable.ic_new_home)
        
        // Setup navigation
        setupNavigation()

        // Cards / Buttons
        setupClickListener(R.id.monday) {
            startActivity(Intent(this, WeatherDetailedActivity::class.java))
        }
        setupClickListener(R.id.btn_search_locations) {
            startActivity(Intent(this, SearchActivity::class.java))
        }
        setupClickListener(R.id.btn_weather_radar) {
            startActivity(Intent(this, WeatherRadarActivity::class.java))
        }
    }
}


