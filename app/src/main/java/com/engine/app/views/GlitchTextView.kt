package com.engine.app.views

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.graphics.Typeface
import android.os.Handler
import android.os.Looper
import android.util.AttributeSet
import android.view.View
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import com.engine.app.R
import java.util.Random
import kotlin.math.min

/**
 * A custom TextView that displays text with cyberpunk-style glitch effects.
 * Features include:
 * - Random character glitching
 * - Scan lines overlay
 * - Color shifting
 * - Binary mode display
 */
class GlitchTextView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : AppCompatTextView(context, attrs, defStyleAttr) {

    // Glitch effect properties
    private var glitchIntensity = 0.3f // 0.0f to 1.0f
    private var glitchFrequency = 0.2f // 0.0f to 1.0f
    private var showScanLines = true
    private var binaryMode = false
    
    // Paint objects
    private val scanLinePaint = Paint().apply {
        color = Color.argb(40, 0, 255, 255) // Semi-transparent cyan
        style = Paint.Style.STROKE
        strokeWidth = 1f
    }
    
    private val glitchPaint = Paint().apply {
        color = Color.argb(180, 255, 0, 128) // Magenta for glitch effect
        textSize = this@GlitchTextView.textSize
    }
    
    // For random glitch generation
    private val random = Random()
    private val handler = Handler(Looper.getMainLooper())
    private var glitchRunnable: Runnable? = null
    private var isGlitching = false
    
    // Original text and binary representation
    private var originalText = ""
    private var binaryText = ""
    
    // Temporary variables for glitch effect
    private val glitchedChars = mutableListOf<Int>()
    private val tempRect = Rect()
    
    init {
        // Get custom attributes if provided
        if (attrs != null) {
            val typedArray = context.obtainStyledAttributes(
                attrs,
                R.styleable.GlitchTextView,
                defStyleAttr,
                0
            )
            
            try {
                glitchIntensity = typedArray.getFloat(
                    R.styleable.GlitchTextView_glitchIntensity,
                    glitchIntensity
                )
                glitchFrequency = typedArray.getFloat(
                    R.styleable.GlitchTextView_glitchFrequency,
                    glitchFrequency
                )
                showScanLines = typedArray.getBoolean(
                    R.styleable.GlitchTextView_scanLines,
                    showScanLines
                )
                binaryMode = typedArray.getBoolean(
                    R.styleable.GlitchTextView_binaryDisplay,
                    binaryMode
                )
            } finally {
                typedArray.recycle()
            }
        }
        
        // Set the cyber font if available
        try {
            val customTypeface = ResourcesCompat.getFont(context, R.font.cyber_font)
            typeface = customTypeface
        } catch (e: Exception) {
            // Fallback to default if font not available
        }
        
        // Initialize glitch effect
        startGlitchEffect()
    }
    
    override fun setText(text: CharSequence?, type: BufferType?) {
        originalText = text?.toString() ?: ""
        binaryText = textToBinary(originalText)
        super.setText(if (binaryMode) binaryText else originalText, type)
    }
    
    override fun onDraw(canvas: Canvas) {
        // Draw the base text
        super.onDraw(canvas)
        
        // Draw scan lines if enabled
        if (showScanLines) {
            drawScanLines(canvas)
        }
        
        // Draw glitch effects if active
        if (isGlitching && glitchedChars.isNotEmpty()) {
            drawGlitchEffects(canvas)
        }
    }
    
    private fun drawScanLines(canvas: Canvas) {
        val lineSpacing = height / 15f // Adjust for density of scan lines
        
        for (i in 0 until height step lineSpacing.toInt()) {
            canvas.drawLine(0f, i.toFloat(), width.toFloat(), i.toFloat(), scanLinePaint)
        }
    }
    
    private fun drawGlitchEffects(canvas: Canvas) {
        val text = text.toString()
        if (text.isEmpty()) return
        
        // Get text bounds for positioning
        paint.getTextBounds(text, 0, text.length, tempRect)
        
        // Calculate text position (similar to TextView's internal logic)
        val textX = when (textAlignment) {
            View.TEXT_ALIGNMENT_CENTER -> (width - tempRect.width()) / 2f
            View.TEXT_ALIGNMENT_VIEW_END -> width - tempRect.width() - paddingEnd.toFloat()
            else -> paddingStart.toFloat()
        }
        
        // Draw glitched characters
        for (charIndex in glitchedChars) {
            if (charIndex >= text.length) continue
            
            // Calculate position of this character
            val charWidth = paint.measureText(text, 0, charIndex)
            val xPos = textX + charWidth
            
            // Random offset for glitch effect
            val offsetX = (random.nextFloat() * 6 - 3) * glitchIntensity
            val offsetY = (random.nextFloat() * 6 - 3) * glitchIntensity
            
            // Draw the glitched character with offset and different color
            val char = text[charIndex].toString()
            canvas.drawText(
                char,
                xPos + offsetX,
                height / 2f + tempRect.height() / 2f + offsetY,
                glitchPaint
            )
        }
    }
    
    private fun startGlitchEffect() {
        glitchRunnable = object : Runnable {
            override fun run() {
                // Determine if we should glitch based on frequency
                isGlitching = random.nextFloat() < glitchFrequency
                
                if (isGlitching) {
                    // Clear previous glitched characters
                    glitchedChars.clear()
                    
                    // Select random characters to glitch
                    val text = text.toString()
                    val charCount = text.length
                    val glitchCount = (charCount * glitchIntensity * 0.3f).toInt() + 1
                    
                    for (i in 0 until min(glitchCount, charCount)) {
                        glitchedChars.add(random.nextInt(charCount))
                    }
                    
                    // Randomly shift glitch color
                    val r = random.nextInt(256)
                    val g = random.nextInt(256)
                    val b = random.nextInt(256)
                    glitchPaint.color = Color.argb(180, r, g, b)
                }
                
                // Force redraw
                invalidate()
                
                // Schedule next glitch effect
                val delay = if (isGlitching) {
                    // Shorter delay during active glitch
                    (50 + random.nextInt(100)).toLong()
                } else {
                    // Longer delay between glitch effects
                    (500 + random.nextInt(1500)).toLong()
                }
                
                handler.postDelayed(this, delay)
            }
        }
        
        // Start the glitch effect loop
        handler.post(glitchRunnable!!)
    }
    
    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        // Clean up to prevent memory leaks
        glitchRunnable?.let { handler.removeCallbacks(it) }
    }
    
    // Convert text to binary representation
    private fun textToBinary(text: String): String {
        val binaryBuilder = StringBuilder()
        for (char in text) {
            // Convert each character to its binary representation
            val binary = Integer.toBinaryString(char.code)
            // Pad to 8 bits
            val paddedBinary = binary.padStart(8, '0')
            binaryBuilder.append(paddedBinary).append(' ')
        }
        return binaryBuilder.toString()
    }
    
    // Public methods to control the glitch effect
    
    fun setGlitchIntensity(intensity: Float) {
        glitchIntensity = intensity.coerceIn(0f, 1f)
    }
    
    fun setGlitchFrequency(frequency: Float) {
        glitchFrequency = frequency.coerceIn(0f, 1f)
    }
    
    fun setScanLines(show: Boolean) {
        showScanLines = show
        invalidate()
    }
    
    fun setBinaryMode(enabled: Boolean) {
        if (binaryMode != enabled) {
            binaryMode = enabled
            super.setText(if (binaryMode) binaryText else originalText, BufferType.NORMAL)
        }
    }
    
    fun toggleBinaryMode() {
        setBinaryMode(!binaryMode)
    }
}