package com.example.weatherforecast.widgets

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Matrix
import android.graphics.Paint
import android.graphics.RectF
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.util.Log
import android.widget.ImageView
import com.example.weatherforecast.utils.ImageUtils

class ScaledImageView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : ImageView(context, attrs, defStyleAttr) {
    
    private var scaledBitmap: Bitmap? = null
    private val paint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val matrix = Matrix()
    
    companion object {
        private const val TAG = "ScaledImageView"
        private const val MAX_IMAGE_SIZE = 2048
    }
    
    override fun setImageResource(resId: Int) {
        // Load and scale the image before setting it
        loadScaledImage(resId)
    }
    
    private fun loadScaledImage(resId: Int) {
        try {
            val options = BitmapFactory.Options().apply {
                inJustDecodeBounds = true
            }
            
            // Get image dimensions
            BitmapFactory.decodeResource(resources, resId, options)
            
            val imageHeight = options.outHeight
            val imageWidth = options.outWidth
            
            if (imageWidth <= MAX_IMAGE_SIZE && imageHeight <= MAX_IMAGE_SIZE) {
                // Image is already small enough, load normally
                super.setImageResource(resId)
                return
            }
            
            // Calculate sample size to scale down
            val sampleSize = calculateInSampleSize(imageWidth, imageHeight, MAX_IMAGE_SIZE, MAX_IMAGE_SIZE)
            
            val scaledOptions = BitmapFactory.Options().apply {
                inSampleSize = sampleSize
                inJustDecodeBounds = false
                inPreferredConfig = Bitmap.Config.RGB_565
            }
            
            scaledBitmap = BitmapFactory.decodeResource(resources, resId, scaledOptions)
            invalidate()
            
            Log.d(TAG, "Scaled image from ${imageWidth}x${imageHeight} to ${scaledBitmap?.width}x${scaledBitmap?.height}")
            
        } catch (e: Exception) {
            Log.e(TAG, "Error loading scaled image: $resId", e)
            super.setImageResource(resId)
        }
    }
    
    override fun onDraw(canvas: Canvas) {
        val scaledBitmap = this.scaledBitmap
        
        if (scaledBitmap != null) {
            val viewWidth = width.toFloat()
            val viewHeight = height.toFloat()
            val bitmapWidth = scaledBitmap.width.toFloat()
            val bitmapHeight = scaledBitmap.height.toFloat()
            
            // Calculate scaling to fit the view while maintaining aspect ratio
            val scaleX = viewWidth / bitmapWidth
            val scaleY = viewHeight / bitmapHeight
            val scale = minOf(scaleX, scaleY)
            
            // Calculate position to center the image
            val scaledWidth = bitmapWidth * scale
            val scaledHeight = bitmapHeight * scale
            val left = (viewWidth - scaledWidth) / 2
            val top = (viewHeight - scaledHeight) / 2
            
            // Set up the matrix for scaling and positioning
            matrix.reset()
            matrix.setScale(scale, scale)
            matrix.postTranslate(left, top)
            
            // Draw the scaled bitmap
            canvas.drawBitmap(scaledBitmap, matrix, paint)
        } else {
            super.onDraw(canvas)
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
    
    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        scaledBitmap?.recycle()
        scaledBitmap = null
    }
}
