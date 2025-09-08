package com.example.weatherforecast.cache

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.LruCache
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.lang.ref.WeakReference

/**
 * Memory-efficient image cache manager
 */
object ImageCacheManager {
    
    private const val MAX_CACHE_SIZE = 50 * 1024 * 1024 // 50MB
    private const val MAX_IMAGE_SIZE = 1024 // Max width/height for cached images
    
    private val memoryCache: LruCache<String, Bitmap> by lazy {
        object : LruCache<String, Bitmap>(MAX_CACHE_SIZE) {
            override fun sizeOf(key: String, bitmap: Bitmap): Int {
                return bitmap.byteCount
            }
        }
    }
    
    private val contextRefs = mutableMapOf<String, WeakReference<Context>>()
    
    /**
     * Get cached bitmap or load and cache it
     */
    suspend fun getBitmap(context: Context?, resourceId: Int): Bitmap? {
        if (context == null) return null
        
        val key = "res_$resourceId"
        
        // Check memory cache first
        memoryCache.get(key)?.let { return it }
        
        // Load and cache the bitmap
        return withContext(Dispatchers.IO) {
            try {
                val bitmap = loadScaledBitmap(context, resourceId)
                bitmap?.let { memoryCache.put(key, it) }
                bitmap
            } catch (e: Exception) {
                null
            }
        }
    }
    
    /**
     * Load and scale bitmap efficiently
     */
    private fun loadScaledBitmap(context: Context, resourceId: Int): Bitmap? {
        val options = BitmapFactory.Options().apply {
            inJustDecodeBounds = true
        }
        
        BitmapFactory.decodeResource(context.resources, resourceId, options)
        
        val imageHeight = options.outHeight
        val imageWidth = options.outWidth
        
        if (imageWidth <= MAX_IMAGE_SIZE && imageHeight <= MAX_IMAGE_SIZE) {
            // Image is already small enough
            val normalOptions = BitmapFactory.Options().apply {
                inJustDecodeBounds = false
                inPreferredConfig = Bitmap.Config.RGB_565
            }
            return BitmapFactory.decodeResource(context.resources, resourceId, normalOptions)
        }
        
        // Calculate sample size
        val sampleSize = calculateInSampleSize(imageWidth, imageHeight, MAX_IMAGE_SIZE, MAX_IMAGE_SIZE)
        
        val scaledOptions = BitmapFactory.Options().apply {
            inSampleSize = sampleSize
            inJustDecodeBounds = false
            inPreferredConfig = Bitmap.Config.RGB_565
        }
        
        return BitmapFactory.decodeResource(context.resources, resourceId, scaledOptions)
    }
    
    private fun calculateInSampleSize(
        imageWidth: Int,
        imageHeight: Int,
        reqWidth: Int,
        reqHeight: Int
    ): Int {
        var inSampleSize = 1
        
        if (imageHeight > reqHeight || imageWidth > reqWidth) {
            val halfHeight = imageHeight / 2
            val halfWidth = imageWidth / 2
            
            while (halfHeight / inSampleSize >= reqHeight && halfWidth / inSampleSize >= reqWidth) {
                inSampleSize *= 2
            }
        }
        
        return inSampleSize
    }
    
    /**
     * Clear cache when memory is low
     */
    fun clearCache() {
        memoryCache.evictAll()
    }
    
    /**
     * Get cache size in bytes
     */
    fun getCacheSize(): Int = memoryCache.size()
    
    /**
     * Get cache hit rate
     */
    fun getCacheHitRate(): Float {
        val hits = memoryCache.hitCount()
        val misses = memoryCache.missCount()
        return if (hits + misses > 0) hits.toFloat() / (hits + misses) else 0f
    }
}
