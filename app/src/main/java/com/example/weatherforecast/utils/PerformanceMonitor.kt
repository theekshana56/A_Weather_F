package com.example.weatherforecast.utils

import android.app.ActivityManager
import android.content.Context
import android.os.Debug
import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Performance monitoring utility
 */
object PerformanceMonitor {
    
    private const val TAG = "PerformanceMonitor"
    private const val MEMORY_WARNING_THRESHOLD = 0.8f // 80% memory usage
    
    /**
     * Monitor memory usage and log warnings
     */
    suspend fun monitorMemory(context: Context) {
        withContext(Dispatchers.IO) {
            val activityManager = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
            val memoryInfo = ActivityManager.MemoryInfo()
            activityManager.getMemoryInfo(memoryInfo)
            
            val usedMemory = memoryInfo.totalMem - memoryInfo.availMem
            val memoryUsage = usedMemory.toFloat() / memoryInfo.totalMem
            
            if (memoryUsage > MEMORY_WARNING_THRESHOLD) {
                Log.w(TAG, "High memory usage: ${(memoryUsage * 100).toInt()}%")
                Log.w(TAG, "Available memory: ${memoryInfo.availMem / 1024 / 1024}MB")
                
                // Trigger garbage collection
                System.gc()
            }
        }
    }
    
    /**
     * Get current memory usage in MB
     */
    fun getMemoryUsage(): Long {
        val runtime = Runtime.getRuntime()
        val usedMemory = runtime.totalMemory() - runtime.freeMemory()
        return usedMemory / 1024 / 1024
    }
    
    /**
     * Get heap size in MB
     */
    fun getHeapSize(): Long {
        val runtime = Runtime.getRuntime()
        return runtime.totalMemory() / 1024 / 1024
    }
    
    /**
     * Check if device has low memory
     */
    fun isLowMemory(context: Context): Boolean {
        val activityManager = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        return activityManager.isLowRamDevice
    }
    
    /**
     * Log performance metrics
     */
    fun logPerformanceMetrics(context: Context) {
        val memoryUsage = getMemoryUsage()
        val heapSize = getHeapSize()
        val isLowMem = isLowMemory(context)
        
        Log.d(TAG, "Memory Usage: ${memoryUsage}MB / ${heapSize}MB")
        Log.d(TAG, "Low Memory Device: $isLowMem")
        Log.d(TAG, "Native Heap: ${Debug.getNativeHeapSize() / 1024 / 1024}MB")
    }
}
