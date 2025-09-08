package com.example.weatherforecast

import android.app.Application
import android.content.ComponentCallbacks2
import android.content.Context
import androidx.lifecycle.ProcessLifecycleOwner
import com.example.weatherforecast.cache.ImageCacheManager
import com.example.weatherforecast.utils.PerformanceMonitor
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

class WeatherForecastApplication : Application() {
    
    private val applicationScope = CoroutineScope(SupervisorJob() + Dispatchers.Default)
    
    override fun onCreate() {
        super.onCreate()
        
        // Initialize performance monitoring
        applicationScope.launch {
            PerformanceMonitor.monitorMemory(this@WeatherForecastApplication)
        }
        
        // Set up memory monitoring
        ProcessLifecycleOwner.get().lifecycle.addObserver(object : androidx.lifecycle.DefaultLifecycleObserver {
            override fun onStart(owner: androidx.lifecycle.LifecycleOwner) {
                super.onStart(owner)
                // App is in foreground
                PerformanceMonitor.logPerformanceMetrics(this@WeatherForecastApplication)
            }
            
            override fun onStop(owner: androidx.lifecycle.LifecycleOwner) {
                super.onStop(owner)
                // App is in background - clear cache to free memory
                ImageCacheManager.clearCache()
            }
        })
    }
    
    override fun onLowMemory() {
        super.onLowMemory()
        // Clear image cache when memory is low
        ImageCacheManager.clearCache()
    }
    
    @Suppress("DEPRECATION")
    override fun onTrimMemory(level: Int) {
        super.onTrimMemory(level)
        when (level) {
            ComponentCallbacks2.TRIM_MEMORY_RUNNING_CRITICAL,
            ComponentCallbacks2.TRIM_MEMORY_RUNNING_LOW,
            ComponentCallbacks2.TRIM_MEMORY_RUNNING_MODERATE -> {
                // Clear cache when memory is getting low
                ImageCacheManager.clearCache()
            }
        }
    }
}
