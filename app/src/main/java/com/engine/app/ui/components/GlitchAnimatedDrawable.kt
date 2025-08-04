package com.engine.app.ui.components

import android.animation.ValueAnimator
import android.graphics.Canvas
import android.graphics.ColorFilter
import android.graphics.Paint
import android.graphics.PixelFormat
import android.graphics.drawable.Drawable
import android.os.SystemClock
import android.view.animation.LinearInterpolator
import androidx.annotation.DrawableRes
import androidx.core.content.ContextCompat
import com.engine.app.R
import kotlin.random.Random

/**
 * Custom drawable that applies a cyberpunk-style glitch effect to any background.
 * Creates CRT distortion and scan lines for a retro-futuristic look.
 */
class GlitchAnimatedDrawable(@DrawableRes private val baseDrawableResId: Int) : Drawable() {

    private val baseDrawable: Drawable? = null // Will be initialized when bounds are set
    private val glitchPaint = Paint().apply {
        style = Paint.Style.STROKE
        strokeWidth = 2f
    }
    
    private var glitchOffset = 0f
    private var glitchIntensity = 0.3f
    private var lastGlitchTime = 0L
    private var glitchDuration = 200L
    private var scanLineOffset = 0f
    
    private val glitchAnimator = ValueAnimator.ofFloat(0f, 1f).apply {
        duration = 1000
        repeatCount = ValueAnimator.INFINITE
        repeatMode = ValueAnimator.RESTART
        interpolator = LinearInterpolator()
        addUpdateListener { animation ->
            scanLineOffset = animation.animatedValue as Float
            invalidateSelf()
        }
    }
    
    init {
        glitchAnimator.start()
    }
    
    override fun onBoundsChange(bounds: androidx.graphics.Rect) {
        super.onBoundsChange(bounds)
        // Initialize the base drawable when bounds are set
        baseDrawable?.bounds = bounds
    }
    
    override fun draw(canvas: Canvas) {
        // Draw the base drawable
        baseDrawable?.draw(canvas)
        
        // Apply scan lines effect
        drawScanLines(canvas)
        
        // Apply random glitch effect
        if (shouldGlitch()) {
            drawGlitchEffect(canvas)
        }
    }
    
    private fun drawScanLines(canvas: Canvas) {
        val height = bounds.height().toFloat()
        val width = bounds.width().toFloat()
        
        // Draw horizontal scan lines
        glitchPaint.alpha = 40 // Semi-transparent
        val scanLineSpacing = 4f
        val totalLines = (height / scanLineSpacing).toInt()
        
        for (i in 0 until totalLines) {
            val y = (i * scanLineSpacing + scanLineOffset * scanLineSpacing) % height
            canvas.drawLine(0f, y, width, y, glitchPaint)
        }
    }
    
    private fun drawGlitchEffect(canvas: Canvas) {
        val height = bounds.height().toFloat()
        val width = bounds.width().toFloat()
        
        // Save the canvas state
        canvas.save()
        
        // Apply random horizontal shifts to create glitch effect
        val numGlitches = (5 * glitchIntensity).toInt() + 1
        for (i in 0 until numGlitches) {
            val glitchHeight = height / (numGlitches * 3)
            val startY = Random.nextFloat() * height
            val endY = startY + glitchHeight
            
            // Create horizontal displacement
            val displacement = (Random.nextFloat() * 2 - 1) * width * 0.03f * glitchIntensity
            
            // Apply the glitch effect by drawing a shifted portion of the canvas
            canvas.save()
            canvas.clipRect(0f, startY, width, endY)
            canvas.translate(displacement, 0f)
            baseDrawable?.draw(canvas)
            canvas.restore()
        }
        
        // Restore the canvas to its original state
        canvas.restore()
        
        // Schedule the next glitch
        lastGlitchTime = SystemClock.elapsedRealtime()
    }
    
    private fun shouldGlitch(): Boolean {
        val currentTime = SystemClock.elapsedRealtime()
        if (currentTime - lastGlitchTime > glitchDuration) {
            // Random chance of glitching
            return Random.nextFloat() < 0.05f // 5% chance per frame
        }
        return false
    }
    
    override fun setAlpha(alpha: Int) {
        glitchPaint.alpha = alpha
    }
    
    override fun setColorFilter(colorFilter: ColorFilter?) {
        glitchPaint.colorFilter = colorFilter
    }
    
    override fun getOpacity(): Int {
        return PixelFormat.TRANSLUCENT
    }
    
    /**
     * Set the intensity of the glitch effect (0.0 to 1.0)
     */
    fun setGlitchIntensity(intensity: Float) {
        glitchIntensity = intensity.coerceIn(0f, 1f)
    }
    
    override fun getIntrinsicWidth(): Int {
        return baseDrawable?.intrinsicWidth ?: -1
    }
    
    override fun getIntrinsicHeight(): Int {
        return baseDrawable?.intrinsicHeight ?: -1
    }
}