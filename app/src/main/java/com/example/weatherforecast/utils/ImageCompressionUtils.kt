package com.example.weatherforecast.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.util.Log
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream

object ImageCompressionUtils {
    
    private const val TAG = "ImageCompressionUtils"
    private const val MAX_IMAGE_SIZE = 2048
    private const val COMPRESSION_QUALITY = 80
    
    /**
     * Compresses a large image resource and saves it to a temporary file
     * This can be used to create optimized versions of large images
     */
    fun compressImageResource(
        context: Context,
        resourceId: Int,
        maxWidth: Int = MAX_IMAGE_SIZE,
        maxHeight: Int = MAX_IMAGE_SIZE
    ): Bitmap? {
        return try {
            val options = BitmapFactory.Options().apply {
                inJustDecodeBounds = true
            }
            
            // Get image dimensions
            BitmapFactory.decodeResource(context.resources, resourceId, options)
            
            val imageHeight = options.outHeight
            val imageWidth = options.outWidth
            
            if (imageWidth <= maxWidth && imageHeight <= maxHeight) {
                // Image is already small enough
                val normalOptions = BitmapFactory.Options().apply {
                    inJustDecodeBounds = false
                    inPreferredConfig = Bitmap.Config.RGB_565
                }
                return BitmapFactory.decodeResource(context.resources, resourceId, normalOptions)
            }
            
            // Calculate sample size
            val sampleSize = calculateInSampleSize(imageWidth, imageHeight, maxWidth, maxHeight)
            
            val scaledOptions = BitmapFactory.Options().apply {
                inSampleSize = sampleSize
                inJustDecodeBounds = false
                inPreferredConfig = Bitmap.Config.RGB_565
            }
            
            val scaledBitmap = BitmapFactory.decodeResource(context.resources, resourceId, scaledOptions)
            
            Log.d(TAG, "Compressed image from ${imageWidth}x${imageHeight} to ${scaledBitmap?.width}x${scaledBitmap?.height}")
            scaledBitmap
            
        } catch (e: Exception) {
            Log.e(TAG, "Error compressing image: $resourceId", e)
            null
        }
    }
    
    /**
     * Saves a compressed bitmap to a file
     */
    fun saveCompressedBitmap(bitmap: Bitmap, file: File): Boolean {
        return try {
            val outputStream = FileOutputStream(file)
            val compressed = bitmap.compress(Bitmap.CompressFormat.JPEG, COMPRESSION_QUALITY, outputStream)
            outputStream.close()
            compressed
        } catch (e: Exception) {
            Log.e(TAG, "Error saving compressed bitmap", e)
            false
        }
    }
    
    /**
     * Creates a thumbnail version of an image
     */
    fun createThumbnail(
        context: Context,
        resourceId: Int,
        thumbnailSize: Int = 256
    ): Bitmap? {
        return try {
            val options = BitmapFactory.Options().apply {
                inJustDecodeBounds = true
            }
            
            BitmapFactory.decodeResource(context.resources, resourceId, options)
            
            val imageHeight = options.outHeight
            val imageWidth = options.outWidth
            
            val sampleSize = calculateInSampleSize(imageWidth, imageHeight, thumbnailSize, thumbnailSize)
            
            val scaledOptions = BitmapFactory.Options().apply {
                inSampleSize = sampleSize
                inJustDecodeBounds = false
                inPreferredConfig = Bitmap.Config.RGB_565
            }
            
            val thumbnail = BitmapFactory.decodeResource(context.resources, resourceId, scaledOptions)
            
            Log.d(TAG, "Created thumbnail ${thumbnail?.width}x${thumbnail?.height} for image $resourceId")
            thumbnail
            
        } catch (e: Exception) {
            Log.e(TAG, "Error creating thumbnail: $resourceId", e)
            null
        }
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
}
