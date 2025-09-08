package com.example.weatherforecast.drawables

import android.content.Context
import android.graphics.*
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.util.Log

class ScaledBitmapDrawable(
    private val context: Context,
    private val resourceId: Int,
    private val maxWidth: Int = 2048,
    private val maxHeight: Int = 2048
) : Drawable() {
    
    private var scaledBitmap: Bitmap? = null
    private val paint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val matrix = Matrix()
    private var bounds = Rect()
    
    companion object {
        private const val TAG = "ScaledBitmapDrawable"
    }
    
    init {
        loadScaledBitmap()
    }
    
    private fun loadScaledBitmap() {
        try {
            val options = BitmapFactory.Options().apply {
                inJustDecodeBounds = true
            }
            
            // Get image dimensions
            BitmapFactory.decodeResource(context.resources, resourceId, options)
            
            val imageHeight = options.outHeight
            val imageWidth = options.outWidth
            
            if (imageWidth <= maxWidth && imageHeight <= maxHeight) {
                // Image is already small enough, load normally
                val normalOptions = BitmapFactory.Options().apply {
                    inJustDecodeBounds = false
                    inPreferredConfig = Bitmap.Config.RGB_565
                }
                scaledBitmap = BitmapFactory.decodeResource(context.resources, resourceId, normalOptions)
            } else {
                // Calculate sample size to scale down
                val sampleSize = calculateInSampleSize(imageWidth, imageHeight, maxWidth, maxHeight)
                
                val scaledOptions = BitmapFactory.Options().apply {
                    inSampleSize = sampleSize
                    inJustDecodeBounds = false
                    inPreferredConfig = Bitmap.Config.RGB_565
                }
                
                scaledBitmap = BitmapFactory.decodeResource(context.resources, resourceId, scaledOptions)
            }
            
            Log.d(TAG, "Scaled image from ${imageWidth}x${imageHeight} to ${scaledBitmap?.width}x${scaledBitmap?.height}")
            
        } catch (e: Exception) {
            Log.e(TAG, "Error loading scaled bitmap: $resourceId", e)
        }
    }
    
    override fun draw(canvas: Canvas) {
        val scaledBitmap = this.scaledBitmap ?: return
        
        val viewWidth = bounds.width().toFloat()
        val viewHeight = bounds.height().toFloat()
        val bitmapWidth = scaledBitmap.width.toFloat()
        val bitmapHeight = scaledBitmap.height.toFloat()
        
        if (viewWidth <= 0 || viewHeight <= 0 || bitmapWidth <= 0 || bitmapHeight <= 0) {
            return
        }
        
        // Calculate scaling to fit the bounds while maintaining aspect ratio
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
        matrix.postTranslate(left + bounds.left, top + bounds.top)
        
        // Draw the scaled bitmap
        canvas.drawBitmap(scaledBitmap, matrix, paint)
    }
    
    override fun setAlpha(alpha: Int) {
        paint.alpha = alpha
    }
    
    override fun setColorFilter(colorFilter: ColorFilter?) {
        paint.colorFilter = colorFilter
    }
    
    @Deprecated("Deprecated in Java")
    override fun getOpacity(): Int {
        return PixelFormat.TRANSLUCENT
    }
    
    override fun setBounds(left: Int, top: Int, right: Int, bottom: Int) {
        super.setBounds(left, top, right, bottom)
        bounds.set(left, top, right, bottom)
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
    
    override fun getIntrinsicWidth(): Int {
        return scaledBitmap?.width ?: super.getIntrinsicWidth()
    }
    
    override fun getIntrinsicHeight(): Int {
        return scaledBitmap?.height ?: super.getIntrinsicHeight()
    }
}
