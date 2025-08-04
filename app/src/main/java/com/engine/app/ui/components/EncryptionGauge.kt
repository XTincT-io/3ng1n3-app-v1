package com.engine.app.ui.components

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import androidx.core.content.withStyledAttributes
import com.engine.app.R
import com.engine.app.data.EncryptionLevel
import kotlin.math.min

/**
 * Custom view that displays encryption status as a cyberpunk-styled RPM gauge.
 * Features include animated needle movement, color transitions based on encryption level,
 * and customizable appearance.
 */
class EncryptionGauge @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    // Properties for the gauge
    private var level: Int = 0
    private var maxLevel: Int = 100
    private var lowThreshold: Int = 30
    private var mediumThreshold: Int = 70
    
    // Current animated value of the gauge
    private var currentValue: Float = 0f
    
    // Colors
    private var backgroundColor = Color.parseColor("#1A1A1A") // Dark background
    private var lowColor = Color.parseColor("#FF3366") // Neon red
    private var mediumColor = Color.parseColor("#FFCC00") // Neon yellow
    private var highColor = Color.parseColor("#39FF14") // Neon green
    private var textColor = Color.parseColor("#39FF14") // Neon green
    
    // Dimensions
    private var gaugeWidth = 0f
    private var gaugeHeight = 0f
    private var padding = 0f
    
    // Paint objects
    private val backgroundPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
        color = backgroundColor
    }
    
    private val gaugePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
    }
    
    private val borderPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.STROKE
        color = textColor
        strokeWidth = 3f
    }
    
    private val textPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
        color = textColor
        textAlign = Paint.Align.CENTER
    }
    
    // Animation
    private val valueAnimator = ValueAnimator.ofFloat(0f, 0f).apply {
        duration = 500
        interpolator = AccelerateDecelerateInterpolator()
        addUpdateListener { animation ->
            currentValue = animation.animatedValue as Float
            invalidate()
        }
    }
    
    init {
        // Load attributes from XML
        context.withStyledAttributes(attrs, R.styleable.EncryptionGauge) {
            level = getInteger(R.styleable.EncryptionGauge_level, 0)
            maxLevel = getInteger(R.styleable.EncryptionGauge_maxLevel, 100)
            lowThreshold = getInteger(R.styleable.EncryptionGauge_lowThreshold, 30)
            mediumThreshold = getInteger(R.styleable.EncryptionGauge_mediumThreshold, 70)
        }
        
        // Initialize the gauge position
        currentValue = level.toFloat()
    }
    
    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        
        // Calculate dimensions based on the view size
        padding = min(w, h) * 0.1f
        gaugeWidth = w - (padding * 2)
        gaugeHeight = h - (padding * 2)
        
        // Adjust text size based on view dimensions
        textPaint.textSize = min(gaugeWidth, gaugeHeight) * 0.15f
    }
    
    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        
        // Draw the gauge background
        drawGaugeBackground(canvas)
        
        // Draw the gauge fill based on current value
        drawGaugeFill(canvas)
        
        // Draw the gauge border
        drawGaugeBorder(canvas)
        
        // Draw the text readout
        drawTextReadout(canvas)
    }
    
    /**
     * Draws the gauge background with a cyberpunk style
     */
    private fun drawGaugeBackground(canvas: Canvas) {
        // Create a background with a subtle gradient
        val gradient = LinearGradient(
            padding, padding,
            padding, padding + gaugeHeight,
            Color.parseColor("#2A2A2A"),
            Color.parseColor("#101010"),
            Shader.TileMode.CLAMP
        )
        
        val bgPaint = Paint(backgroundPaint)
        bgPaint.shader = gradient
        
        // Draw rounded rectangle for the background
        val rect = RectF(padding, padding, padding + gaugeWidth, padding + gaugeHeight)
        canvas.drawRoundRect(rect, 10f, 10f, bgPaint)
        
        // Draw grid lines for cyberpunk effect
        val gridPaint = Paint(borderPaint)
        gridPaint.alpha = 80
        gridPaint.strokeWidth = 1f
        
        // Horizontal grid lines
        val horizontalSpacing = gaugeHeight / 10
        for (i in 1..9) {
            val y = padding + i * horizontalSpacing
            canvas.drawLine(padding, y, padding + gaugeWidth, y, gridPaint)
        }
        
        // Vertical grid lines
        val verticalSpacing = gaugeWidth / 20
        for (i in 1..19) {
            val x = padding + i * verticalSpacing
            canvas.drawLine(x, padding, x, padding + gaugeHeight, gridPaint)
        }
    }
    
    /**
     * Draws the gauge fill based on the current value
     */
    private fun drawGaugeFill(canvas: Canvas) {
        // Calculate the fill width based on current value
        val fillRatio = currentValue / maxLevel
        val fillWidth = gaugeWidth * fillRatio
        
        // Determine the color based on thresholds
        val color = when {
            currentValue < lowThreshold -> lowColor
            currentValue < mediumThreshold -> mediumColor
            else -> highColor
        }
        
        // Set the gauge paint color
        gaugePaint.color = color
        
        // Add a glow effect
        val glowPaint = Paint(gaugePaint)
        glowPaint.maskFilter = BlurMaskFilter(10f, BlurMaskFilter.Blur.NORMAL)
        
        // Draw the fill with rounded corners
        val rect = RectF(padding, padding, padding + fillWidth, padding + gaugeHeight)
        canvas.drawRoundRect(rect, 10f, 10f, glowPaint)
        canvas.drawRoundRect(rect, 10f, 10f, gaugePaint)
        
        // Draw level indicators
        drawLevelIndicators(canvas, fillWidth)
    }
    
    /**
     * Draws level indicators on the gauge
     */
    private fun drawLevelIndicators(canvas: Canvas, fillWidth: Float) {
        val indicatorPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            style = Paint.Style.STROKE
            color = Color.WHITE
            strokeWidth = 2f
        }
        
        // Draw threshold indicators
        val lowX = padding + (gaugeWidth * lowThreshold / maxLevel)
        val mediumX = padding + (gaugeWidth * mediumThreshold / maxLevel)
        
        // Low threshold indicator
        indicatorPaint.color = lowColor
        canvas.drawLine(lowX, padding, lowX, padding + gaugeHeight, indicatorPaint)
        
        // Medium threshold indicator
        indicatorPaint.color = mediumColor
        canvas.drawLine(mediumX, padding, mediumX, padding + gaugeHeight, indicatorPaint)
        
        // Draw tick marks
        val tickCount = 10
        val tickSpacing = gaugeWidth / tickCount
        
        for (i in 0..tickCount) {
            val x = padding + i * tickSpacing
            val tickHeight = if (i % 5 == 0) gaugeHeight * 0.2f else gaugeHeight * 0.1f
            
            // Determine color based on position relative to fill
            indicatorPaint.color = if (x <= padding + fillWidth) Color.WHITE else Color.GRAY
            
            canvas.drawLine(x, padding + gaugeHeight - tickHeight, x, padding + gaugeHeight, indicatorPaint)
        }
    }
    
    /**
     * Draws the gauge border with a cyberpunk glow effect
     */
    private fun drawGaugeBorder(canvas: Canvas) {
        // Create a glowing border effect
        val glowPaint = Paint(borderPaint)
        glowPaint.maskFilter = BlurMaskFilter(5f, BlurMaskFilter.Blur.NORMAL)
        
        // Draw the border with glow
        val rect = RectF(padding, padding, padding + gaugeWidth, padding + gaugeHeight)
        canvas.drawRoundRect(rect, 10f, 10f, glowPaint)
        canvas.drawRoundRect(rect, 10f, 10f, borderPaint)
    }
    
    /**
     * Draws the text readout showing the encryption level
     */
    private fun drawTextReadout(canvas: Canvas) {
        // Determine the encryption level text
        val levelText = when {
            currentValue < lowThreshold -> "LOW SEC"
            currentValue < mediumThreshold -> "MID SEC"
            else -> "HIGH SEC"
        }
        
        // Draw the encryption level text
        val centerX = padding + gaugeWidth / 2
        val centerY = padding + gaugeHeight / 2 + textPaint.textSize / 3
        
        // Draw with glow effect
        val glowPaint = Paint(textPaint)
        glowPaint.maskFilter = BlurMaskFilter(5f, BlurMaskFilter.Blur.NORMAL)
        
        canvas.drawText(levelText, centerX, centerY, glowPaint)
        canvas.drawText(levelText, centerX, centerY, textPaint)
        
        // Draw the numeric value below
        val valueText = "${currentValue.toInt()}%"
        val valueY = centerY + textPaint.textSize * 1.2f
        
        val valuePaint = Paint(textPaint)
        valuePaint.textSize = textPaint.textSize * 0.8f
        
        val valueGlowPaint = Paint(valuePaint)
        valueGlowPaint.maskFilter = BlurMaskFilter(3f, BlurMaskFilter.Blur.NORMAL)
        
        canvas.drawText(valueText, centerX, valueY, valueGlowPaint)
        canvas.drawText(valueText, centerX, valueY, valuePaint)
    }
    
    /**
     * Sets the current encryption level and animates the gauge
     */
    fun setLevel(newLevel: Int) {
        val boundedValue = newLevel.coerceIn(0, maxLevel)
        
        if (this.level != boundedValue) {
            this.level = boundedValue
            
            // Animate to the new value
            valueAnimator.cancel()
            valueAnimator.setFloatValues(currentValue, boundedValue.toFloat())
            valueAnimator.start()
        }
    }
    
    /**
     * Sets the encryption level directly from an EncryptionLevel enum
     */
    fun setEncryptionLevel(encryptionLevel: EncryptionLevel) {
        val levelValue = when (encryptionLevel) {
            EncryptionLevel.NONE -> 0
            EncryptionLevel.LOW -> 20
            EncryptionLevel.MEDIUM -> 50
            EncryptionLevel.HIGH -> 80
            EncryptionLevel.MAXIMUM -> 100
        }
        
        setLevel(levelValue)
    }
    
    /**
     * Sets the maximum level value and updates the display
     */
    fun setMaxLevel(value: Int) {
        if (value > 0 && this.maxLevel != value) {
            this.maxLevel = value
            invalidate()
        }
    }
    
    /**
     * Sets the threshold for low encryption level
     */
    fun setLowThreshold(value: Int) {
        if (value >= 0 && value < mediumThreshold && this.lowThreshold != value) {
            this.lowThreshold = value
            invalidate()
        }
    }
    
    /**
     * Sets the threshold for medium encryption level
     */
    fun setMediumThreshold(value: Int) {
        if (value > lowThreshold && value < maxLevel && this.mediumThreshold != value) {
            this.mediumThreshold = value
            invalidate()
        }
    }
}