package com.example.weatherforecast.base

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.weatherforecast.drawables.ScaledBitmapDrawable
import com.example.weatherforecast.utils.LayoutOptimizer
import com.example.weatherforecast.utils.PerformanceMonitor
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * Base Activity with performance optimizations and memory management
 */
abstract class BaseActivity : AppCompatActivity() {
    
    private var backgroundDrawable: ScaledBitmapDrawable? = null
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Enable hardware acceleration for better performance
        window.setFlags(
            android.view.WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED,
            android.view.WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED
        )
        
        // Monitor performance
        lifecycleScope.launch {
            PerformanceMonitor.monitorMemory(this@BaseActivity)
        }
    }
    
    override fun onResume() {
        super.onResume()
        // Optimize layout when activity resumes
        findViewById<View>(android.R.id.content)?.let { rootView ->
            LayoutOptimizer.optimizeViewHierarchy(rootView)
        }
    }
    
    /**
     * Set background with memory management
     */
    protected fun setOptimizedBackground(resourceId: Int) {
        lifecycleScope.launch(Dispatchers.IO) {
            try {
                val drawable = ScaledBitmapDrawable(this@BaseActivity, resourceId)
                withContext(Dispatchers.Main) {
                    val rootView = findViewById<View>(android.R.id.content)
                    rootView?.background = drawable
                    backgroundDrawable = drawable
                }
            } catch (e: Exception) {
                // Fallback to transparent background
                withContext(Dispatchers.Main) {
                    val rootView = findViewById<View>(android.R.id.content)
                    rootView?.background = null
                }
            }
        }
    }
    
    /**
     * Optimized click listener setup with debouncing
     */
    protected fun setupClickListener(viewId: Int, action: () -> Unit) {
        findViewById<View>(viewId)?.setOnClickListener {
            // Simple debouncing to prevent rapid clicks
            it.isEnabled = false
            it.postDelayed({ it.isEnabled = true }, 300)
            action()
        }
    }
    
    /**
     * Setup navigation with proper activity management
     */
    protected fun setupNavigation() {
        setupClickListener(com.example.weatherforecast.R.id.nav_home) {
            navigateToActivity(com.example.weatherforecast.HomeActivity::class.java)
        }
        setupClickListener(com.example.weatherforecast.R.id.nav_search) {
            navigateToActivity(com.example.weatherforecast.SearchActivity::class.java)
        }
        setupClickListener(com.example.weatherforecast.R.id.nav_map) {
            navigateToActivity(com.example.weatherforecast.WeatherRadarActivity::class.java)
        }
        setupClickListener(com.example.weatherforecast.R.id.nav_alerts) {
            navigateToActivity(com.example.weatherforecast.AlertsActivity::class.java)
        }
        setupClickListener(com.example.weatherforecast.R.id.nav_settings) {
            navigateToActivity(com.example.weatherforecast.SettingsActivity::class.java)
        }
        setupClickListener(com.example.weatherforecast.R.id.btn_back) {
            onBackPressedDispatcher.onBackPressed()
        }
    }
    
    private fun navigateToActivity(activityClass: Class<*>) {
        if (this::class.java != activityClass) {
            startActivity(android.content.Intent(this, activityClass))
            // Add slide animation for better UX
            @Suppress("DEPRECATION")
            overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right)
        }
    }
    
    override fun onDestroy() {
        super.onDestroy()
        // Clean up background drawable to prevent memory leaks
        backgroundDrawable = null
    }
    
    override fun onLowMemory() {
        super.onLowMemory()
        // Clear background when memory is low
        backgroundDrawable = null
        val rootView = findViewById<View>(android.R.id.content)
        rootView?.background = null
    }
}
