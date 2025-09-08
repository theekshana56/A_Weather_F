package com.example.weatherforecast

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity

class OnboardingTwoActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.onboarding_screen_two)

        // btn_next -> onboarding_screen_third
        findViewById<View>(R.id.btn_finish)?.setOnClickListener {
            startActivity(Intent(this, OnboardingThreeActivity::class.java))
        }
    }
}


