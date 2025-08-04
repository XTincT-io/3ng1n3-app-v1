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
import androidx.core.content.ContextCompat
import com.engine.app.R
import java.util.Random
import kotlin.math.min

/**
 * A custom view that displays scrolling binary data in a cyberpunk style.
 * Features include:
 * - Scrolling binary digits (0s and 1s)
 * - Highlighted sections to simulate data processing
 * - Scan line overlay
 * - Customizable colors and speed
 */
class BinaryDisplayView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    // Display properties
    private var scrollSpeed = 2 // Pixels per frame
    private var density = 0.7f // How dense the binary display is (0.0f to 1.0f)
    private var highlightProbability = 0.1f // Probability of highlighting a column
    private var showScanLines = true
    
    // Colors
    private var baseColor = Color.GREEN
    private var highlightColor = Color.CYAN
    private var scanLineColor = Color.argb(40, 0, 255, 255) // Semi-transparent cyan
    
    // Paint objects
    private val binaryPaint = Paint().apply {
        textSize = 14f
        typeface = Typeface.MONOSPACE
        isAntiAlias = true
    }
    
    private val scanLinePaint = Paint().apply {
        style = Paint.Style.STROKE
        strokeWidth = 1f
    }
    
    // Binary data
    private val columns = mutableListOf<BinaryColumn>()
    private val random = Random()
    
    // Animation
    private val handler = Handler(Looper.getMainLooper())
    private var animationRunnable: Runnable? = null
    private var isAnimating = false
    
    // Temporary variables for drawing
    private val tempRect = Rect()
    
    init {
        // Get custom attributes if provided
        if (attrs != null) {
            val typedArray = context.obtainStyledAttributes(
                attrs,
                R.styleable.BinaryDisplayView,
                defStyleAttr,
                0
            )
            
            try {
                scrollSpeed = typedArray.getInt(
                    R.styleable.BinaryDisplayView_scrollSpeed,
                    scrollSpeed
                )
                density = typedArray.getFloat(
                    R.styleable.BinaryDisplayView_density,
                    density
                )
                highlightProbability = typedArray.getFloat(
                    R.styleable.BinaryDisplayView_highlightProbability,
                    highlightProbability
                )
                showScanLines = typedArray.getBoolean(
                    R.styleable.BinaryDisplayView_showScanLines,
                    showScanLines
                )
                baseColor = typedArray.getColor(
                    R.styleable.BinaryDisplayView_baseColor,
                    ContextCompat.getColor(context, R.color.neon_green)
                )
                highlightColor = typedArray.getColor(
                    R.styleable.BinaryDisplayView_highlightColor,
                    ContextCompat.getColor(context, R.color.neon_cyan)
                )
            } finally {
                typedArray.recycle()
            }
        } else {
            // Default colors if attributes not provided
            baseColor = ContextCompat.getColor(context, R.color.neon_green)
            highlightColor = ContextCompat.getColor(context, R.color.neon_cyan)
        }
        
        scanLineColor = Color.argb(40, Color.red(highlightColor), 
                                  Color.green(highlightColor), 
                                  Color.blue(highlightColor))
        scanLinePaint.color = scanLineColor
    }
    
    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        
        // Initialize binary columns based on view size
        initializeBinaryData()
    }
    
    private fun initializeBinaryData() {
        columns.clear()
        
        // Calculate character width for spacing
        binaryPaint.getTextBounds("0", 0, 1, tempRect)
        val charWidth = tempRect.width()
        val charHeight = tempRect.height()
        
        // Calculate number of columns and rows
        val numColumns = width / (charWidth + 4) // Add some spacing
        val numRows = height / (charHeight + 2) + 1 // +1 to ensure we fill the view
        
        // Create columns of binary data
        for (i in 0 until numColumns) {
            // Only create a column based on density
            if (random.nextFloat() <= density) {
                val x = i * (charWidth + 4) + 2 // Add some spacing
                val isHighlighted = random.nextFloat() < highlightProbability
                val speed = scrollSpeed * (0.5f + random.nextFloat()) // Vary speed slightly
                
                columns.add(BinaryColumn(
                    x = x.toFloat(),
                    numRows = numRows,
                    charHeight = charHeight,
                    isHighlighted = isHighlighted,
                    speed = speed.toInt()
                ))
            }
        }
    }
    
    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        
        // Draw binary columns
        for (column in columns) {
            drawBinaryColumn(canvas, column)
        }
        
        // Draw scan lines if enabled
        if (showScanLines) {
            drawScanLines(canvas)
        }
    }
    
    private fun drawBinaryColumn(canvas: Canvas, column: BinaryColumn) {
        // Set color based on highlight status
        binaryPaint.color = if (column.isHighlighted) highlightColor else baseColor
        
        // Draw each character in the column
        for (i in 0 until column.binaryChars.size) {
            val char = column.binaryChars[i]
            val y = column.startY + i * (tempRect.height() + 2)
            
            // Only draw if within view bounds
            if (y > -tempRect.height() && y < height) {
                // Fade out characters at the bottom
                val alpha = if (y > height - tempRect.height() * 3) {
                    ((height - y) / (tempRect.height() * 3) * 255).toInt()
                } else {
                    255
                }
                binaryPaint.alpha = alpha
                
                canvas.drawText(char.toString(), column.x, y, binaryPaint)
            }
        }
    }
    
    private fun drawScanLines(canvas: Canvas) {
        val lineSpacing = height / 20f // Adjust for density of scan lines
        
        for (i in 0 until height step lineSpacing.toInt()) {
            canvas.drawLine(0f, i.toFloat(), width.toFloat(), i.toFloat(), scanLinePaint)
        }
    }
    
    fun startAnimation() {
        if (isAnimating) return
        
        isAnimating = true
        animationRunnable = object : Runnable {
            override fun run() {
                // Update binary columns
                for (column in columns) {
                    column.update()
                    
                    // Randomly change highlight status
                    if (random.nextFloat() < 0.01f) {
                        column.isHighlighted = random.nextFloat() < highlightProbability
                    }
                }
                
                // Force redraw
                invalidate()
                
                // Schedule next frame
                handler.postDelayed(this, 16) // ~60fps
            }
        }
        
        // Start the animation loop
        handler.post(animationRunnable!!)
    }
    
    fun stopAnimation() {
        isAnimating = false
        animationRunnable?.let { handler.removeCallbacks(it) }
    }
    
    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        startAnimation()
    }
    
    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        stopAnimation()
    }
    
    // Inner class to represent a column of binary digits
    private inner class BinaryColumn(
        val x: Float,
        numRows: Int,
        charHeight: Int,
        var isHighlighted: Boolean,
        val speed: Int
    ) {
        val binaryChars = CharArray(numRows)
        var startY = -charHeight * random.nextInt(numRows / 2) // Start at random position
        
        init {
            // Initialize with random binary digits
            for (i in binaryChars.indices) {
                binaryChars[i] = if (random.nextBoolean()) '1' else '0'
            }
        }
        
        fun update() {
            // Move column down
            startY += speed
            
            // If the column has moved down enough, reset it to the top
            // and generate new binary digits
            if (startY > height) {
                startY = -tempRect.height() * binaryChars.size
                
                // Regenerate binary digits
                for (i in binaryChars.indices) {
                    binaryChars[i] = if (random.nextBoolean()) '1' else '0'
                }
            }
            
            // Randomly change some digits for animation effect
            val changeIndex = random.nextInt(binaryChars.size)
            if (random.nextFloat() < 0.1f) {
                binaryChars[changeIndex] = if (random.nextBoolean()) '1' else '0'
            }
        }
    }
    
    // Public methods to control the display
    
    fun setScrollSpeed(speed: Int) {
        scrollSpeed = speed.coerceIn(1, 10)
        // Update column speeds
        for (column in columns) {
            // Vary speed slightly for each column
            val newSpeed = scrollSpeed * (0.5f + random.nextFloat())
        }
    }
    
    fun setDensity(newDensity: Float) {
        density = newDensity.coerceIn(0.1f, 1.0f)
        // Reinitialize with new density
        initializeBinaryData()
    }
    
    fun setHighlightProbability(probability: Float) {
        highlightProbability = probability.coerceIn(0f, 1f)
    }
    
    fun setScanLines(show: Boolean) {
        showScanLines = show
        invalidate()
    }
    
    fun setBaseColor(color: Int) {
        baseColor = color
        invalidate()
    }
    
    fun setHighlightColor(color: Int) {
        highlightColor = color
        scanLineColor = Color.argb(40, Color.red(highlightColor), 
                                  Color.green(highlightColor), 
                                  Color.blue(highlightColor))
        scanLinePaint.color = scanLineColor
        invalidate()
    }
    
    /**
     * Resets the binary display by reinitializing the binary data
     * and restarting the animation if it was previously running
     */
    fun reset() {
        val wasAnimating = isAnimating
        
        // Stop current animation if running
        if (isAnimating) {
            stopAnimation()
        }
        
        // Reinitialize binary data
        initializeBinaryData()
        
        // Restart animation if it was previously running
        if (wasAnimating) {
            startAnimation()
        } else {
            invalidate() // Force redraw without animation
        }
    }
}