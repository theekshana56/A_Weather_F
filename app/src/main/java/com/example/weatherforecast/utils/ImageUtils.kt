package com.example.weatherforecast.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.util.Log
import android.widget.ImageView
import androidx.core.content.ContextCompat
import java.io.InputStream

object ImageUtils {
    
    private const val TAG = "ImageUtils"
    private const val MAX_IMAGE_SIZE = 2048 // Maximum width or height in pixels
    
    /**
     * Loads a large image resource and scales it down to prevent memory issues
     */
    fun loadScaledImage(context: Context, resourceId: Int, imageView: ImageView) {
        try {
            val options = BitmapFactory.Options().apply {
                inJustDecodeBounds = true
            }
            
            // Get image dimensions without loading it into memory
            BitmapFactory.decodeResource(context.resources, resourceId, options)
            
            val imageHeight = options.outHeight
            val imageWidth = options.outWidth
            
            // Calculate sample size to scale down the image
            val sampleSize = calculateInSampleSize(imageWidth, imageHeight, MAX_IMAGE_SIZE, MAX_IMAGE_SIZE)
            
            val scaledOptions = BitmapFactory.Options().apply {
                inSampleSize = sampleSize
                inJustDecodeBounds = false
                inPreferredConfig = Bitmap.Config.RGB_565 // Use less memory
            }
            
            val scaledBitmap = BitmapFactory.decodeResource(context.resources, resourceId, scaledOptions)
            
            if (scaledBitmap != null) {
                imageView.setImageBitmap(scaledBitmap)
                Log.d(TAG, "Successfully loaded and scaled image: $resourceId")
            } else {
                Log.e(TAG, "Failed to load image: $resourceId")
                // Fallback to original method if scaling fails
                imageView.setImageResource(resourceId)
            }
            
        } catch (e: Exception) {
            Log.e(TAG, "Error loading image: $resourceId", e)
            // Fallback to original method
            imageView.setImageResource(resourceId)
        }
    }
    
    /**
     * Creates a scaled drawable from a large image resource
     */
    fun createScaledDrawable(context: Context, resourceId: Int): Drawable? {
        return try {
            val options = BitmapFactory.Options().apply {
                inJustDecodeBounds = true
            }
            
            BitmapFactory.decodeResource(context.resources, resourceId, options)
            
            val imageHeight = options.outHeight
            val imageWidth = options.outWidth
            
            val sampleSize = calculateInSampleSize(imageWidth, imageHeight, MAX_IMAGE_SIZE, MAX_IMAGE_SIZE)
            
            val scaledOptions = BitmapFactory.Options().apply {
                inSampleSize = sampleSize
                inJustDecodeBounds = false
                inPreferredConfig = Bitmap.Config.RGB_565
            }
            
            val scaledBitmap = BitmapFactory.decodeResource(context.resources, resourceId, scaledOptions)
            BitmapDrawable(context.resources, scaledBitmap)
            
        } catch (e: Exception) {
            Log.e(TAG, "Error creating scaled drawable: $resourceId", e)
            ContextCompat.getDrawable(context, resourceId)
        }
    }
    
    /**
     * Calculates the sample size for scaling down an image
     */
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
     * Rotates a bitmap by the specified angle
     */
    fun rotateBitmap(bitmap: Bitmap, degrees: Float): Bitmap {
        val matrix = Matrix()
        matrix.postRotate(degrees)
        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
    }
}
