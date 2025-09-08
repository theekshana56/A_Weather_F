package com.example.weatherforecast

import android.content.Intent
import android.os.Bundle
import com.example.weatherforecast.base.BaseActivity

class SearchActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.search_screen)

        // Setup navigation
        setupNavigation()

        // city card -> weather detailed
        setupClickListener(R.id.colombo_crd) {
            startActivity(Intent(this, WeatherDetailedActivity::class.java))
        }
    }
}


