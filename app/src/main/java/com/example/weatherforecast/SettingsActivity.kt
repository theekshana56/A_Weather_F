package com.example.weatherforecast

import android.os.Bundle
import com.example.weatherforecast.base.BaseActivity

class SettingsActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.setting_screen)
        
        // Set optimized background
        setOptimizedBackground(R.drawable.bg_sky_gradient)
        
        // Setup navigation
        setupNavigation()
    }
}



