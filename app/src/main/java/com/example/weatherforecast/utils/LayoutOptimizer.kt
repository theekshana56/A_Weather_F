package com.example.weatherforecast.utils

import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import com.example.weatherforecast.cache.ImageCacheManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * Layout optimization utilities
 */
object LayoutOptimizer {
    
    /**
     * Optimize view hierarchy for better performance
     */
    fun optimizeViewHierarchy(view: View) {
        when (view) {
            is ViewGroup -> {
                // Skip view hierarchy modifications to prevent crashes
                // The optimization was causing "child already has parent" errors

                // Recursively optimize children (safely)
                for (i in 0 until view.childCount) {
                    optimizeViewHierarchy(view.getChildAt(i))
                }
            }
            is ImageView -> {
                // Optimize ImageView settings
                view.setLayerType(View.LAYER_TYPE_HARDWARE, null)
                view.setScaleType(ImageView.ScaleType.CENTER_CROP)
            }
        }
    }
    
    /**
     * Preload images in background
     */
    fun preloadImages(scope: CoroutineScope, context: Context?, imageResources: List<Int>) {
        if (context == null) return
        
        scope.launch(Dispatchers.IO) {
            imageResources.forEach { resourceId ->
                try {
                    // This will cache the image
                    ImageCacheManager.getBitmap(context, resourceId)
                } catch (e: Exception) {
                    // Ignore errors during preloading
                }
            }
        }
    }
    
    /**
     * Enable hardware acceleration for views
     */
    fun enableHardwareAcceleration(view: View) {
        view.setLayerType(View.LAYER_TYPE_HARDWARE, null)
    }
    
    /**
     * Disable hardware acceleration for views that don't need it
     */
    fun disableHardwareAcceleration(view: View) {
        view.setLayerType(View.LAYER_TYPE_SOFTWARE, null)
    }
    
    /**
     * Optimize scroll performance
     */
    fun optimizeScrollPerformance(scrollView: View) {
        scrollView.isVerticalScrollBarEnabled = false
        scrollView.isHorizontalScrollBarEnabled = false
        scrollView.overScrollMode = View.OVER_SCROLL_NEVER
    }
}
