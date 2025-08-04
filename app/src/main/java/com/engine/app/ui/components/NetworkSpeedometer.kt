package com.engine.app.ui.components

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import androidx.core.content.withStyledAttributes
import com.engine.app.R
import kotlin.math.cos
import kotlin.math.min
import kotlin.math.sin

/**
 * Custom view that displays network latency as a cyberpunk-styled digital speedometer.
 * Features include animated needle movement, digital readout, and customizable appearance.
 */
class NetworkSpeedometer @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    // Properties for the speedometer
    private var latency: Int = 0
    private var maxLatency: Int = 200
    private var showDigital: Boolean = true
    private var animateNeedle: Boolean = true
    
    // Current animated value of the needle position
    private var currentNeedleValue: Float = 0f
    
    // Colors
    private var dialColor = Color.parseColor("#1A1A1A") // Dark background
    private var tickColor = Color.parseColor("#39FF14") // Neon green
    private var needleColor = Color.parseColor("#FF3366") // Neon red
    private var textColor = Color.parseColor("#39FF14") // Neon green
    
    // Dimensions
    private var dialRadius = 0f
    private var centerX = 0f
    private var centerY = 0f
    
    // Angles
    private val startAngle = 180f
    private val sweepAngle = 180f
    
    // Paint objects
    private val dialPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
        color = dialColor
    }
    
    private val tickPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.STROKE
        color = tickColor
        strokeWidth = 2f
    }
    
    private val needlePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
        color = needleColor
    }
    
    private val textPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
        color = textColor
        textAlign = Paint.Align.CENTER
    }
    
    // Animation
    private val needleAnimator = ValueAnimator.ofFloat(0f, 0f).apply {
        duration = 500
        interpolator = AccelerateDecelerateInterpolator()
        addUpdateListener { animation ->
            currentNeedleValue = animation.animatedValue as Float
            invalidate()
        }
    }
    
    init {
        // Load attributes from XML
        context.withStyledAttributes(attrs, R.styleable.NetworkSpeedometer) {
            latency = getInteger(R.styleable.NetworkSpeedometer_latency, 0)
            maxLatency = getInteger(R.styleable.NetworkSpeedometer_maxLatency, 200)
            showDigital = getBoolean(R.styleable.NetworkSpeedometer_showDigital, true)
            animateNeedle = getBoolean(R.styleable.NetworkSpeedometer_animateNeedle, true)
        }
        
        // Initialize the needle position
        currentNeedleValue = latency.toFloat()
    }
    
    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        
        // Calculate dimensions based on the view size
        centerX = w / 2f
        centerY = h * 0.7f // Position the dial in the upper portion
        dialRadius = min(w, h) * 0.4f // Dial takes up 80% of the smaller dimension
        
        // Adjust text size based on view dimensions
        textPaint.textSize = dialRadius * 0.3f
    }
    
    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        
        // Draw the dial background
        drawDial(canvas)
        
        // Draw the tick marks
        drawTicks(canvas)
        
        // Draw the needle
        drawNeedle(canvas)
        
        // Draw the digital readout if enabled
        if (showDigital) {
            drawDigitalReadout(canvas)
        }
    }
    
    /**
     * Draws the speedometer dial background
     */
    private fun drawDial(canvas: Canvas) {
        // Draw the outer rim with a cyberpunk glow effect
        val rimPaint = Paint(dialPaint)
        rimPaint.style = Paint.Style.STROKE
        rimPaint.color = tickColor
        rimPaint.strokeWidth = 4f
        rimPaint.maskFilter = BlurMaskFilter(8f, BlurMaskFilter.Blur.NORMAL)
        
        // Draw the semi-circular dial
        val rectF = RectF(centerX - dialRadius, centerY - dialRadius, centerX + dialRadius, centerY + dialRadius)
        canvas.drawArc(rectF, startAngle, sweepAngle, false, rimPaint)
        
        // Draw the inner dial with a gradient
        val innerDialPaint = Paint(dialPaint)
        innerDialPaint.shader = RadialGradient(
            centerX, centerY, dialRadius * 0.9f,
            intArrayOf(Color.parseColor("#2A2A2A"), Color.parseColor("#101010")),
            floatArrayOf(0f, 1f),
            Shader.TileMode.CLAMP
        )
        
        val innerRectF = RectF(
            centerX - dialRadius * 0.9f,
            centerY - dialRadius * 0.9f,
            centerX + dialRadius * 0.9f,
            centerY + dialRadius * 0.9f
        )
        canvas.drawArc(innerRectF, startAngle, sweepAngle, true, innerDialPaint)
    }
    
    /**
     * Draws the tick marks around the dial
     */
    private fun drawTicks(canvas: Canvas) {
        // Draw major and minor tick marks
        val majorTickLength = dialRadius * 0.15f
        val minorTickLength = dialRadius * 0.08f
        
        // Number of major ticks (including start and end)
        val majorTickCount = 5
        // Number of minor ticks between each major tick
        val minorTicksBetweenMajor = 4
        
        // Calculate angles for ticks
        val majorTickAngleStep = sweepAngle / (majorTickCount - 1)
        val minorTickAngleStep = majorTickAngleStep / (minorTicksBetweenMajor + 1)
        
        // Draw major ticks and labels
        for (i in 0 until majorTickCount) {
            val angle = startAngle + i * majorTickAngleStep
            val radians = Math.toRadians(angle.toDouble())
            
            val innerX = centerX + (dialRadius * 0.8f * cos(radians)).toFloat()
            val innerY = centerY + (dialRadius * 0.8f * sin(radians)).toFloat()
            val outerX = centerX + ((dialRadius * 0.8f + majorTickLength) * cos(radians)).toFloat()
            val outerY = centerY + ((dialRadius * 0.8f + majorTickLength) * sin(radians)).toFloat()
            
            canvas.drawLine(innerX, innerY, outerX, outerY, tickPaint)
            
            // Draw tick label
            val labelValue = (i * maxLatency / (majorTickCount - 1))
            val labelX = centerX + ((dialRadius * 0.8f + majorTickLength + dialRadius * 0.1f) * cos(radians)).toFloat()
            val labelY = centerY + ((dialRadius * 0.8f + majorTickLength + dialRadius * 0.1f) * sin(radians)).toFloat() + textPaint.textSize / 3
            
            // Adjust text alignment based on position
            when {
                angle < 190f -> textPaint.textAlign = Paint.Align.LEFT
                angle > 350f -> textPaint.textAlign = Paint.Align.RIGHT
                else -> textPaint.textAlign = Paint.Align.CENTER
            }
            
            canvas.drawText("$labelValue", labelX, labelY, textPaint)
        }
        
        // Reset text alignment
        textPaint.textAlign = Paint.Align.CENTER
        
        // Draw minor ticks
        for (i in 0 until majorTickCount - 1) {
            for (j in 1..minorTicksBetweenMajor) {
                val angle = startAngle + i * majorTickAngleStep + j * minorTickAngleStep
                val radians = Math.toRadians(angle.toDouble())
                
                val innerX = centerX + (dialRadius * 0.85f * cos(radians)).toFloat()
                val innerY = centerY + (dialRadius * 0.85f * sin(radians)).toFloat()
                val outerX = centerX + ((dialRadius * 0.85f + minorTickLength) * cos(radians)).toFloat()
                val outerY = centerY + ((dialRadius * 0.85f + minorTickLength) * sin(radians)).toFloat()
                
                canvas.drawLine(innerX, innerY, outerX, outerY, tickPaint)
            }
        }
    }
    
    /**
     * Draws the needle pointing to the current latency value
     */
    private fun drawNeedle(canvas: Canvas) {
        // Calculate the angle for the current value
        val valueRatio = currentNeedleValue / maxLatency
        val valueAngle = startAngle + sweepAngle * valueRatio
        val radians = Math.toRadians(valueAngle.toDouble())
        
        // Calculate needle points
        val needleLength = dialRadius * 0.75f
        val needleWidth = dialRadius * 0.05f
        
        val tipX = centerX + (needleLength * cos(radians)).toFloat()
        val tipY = centerY + (needleLength * sin(radians)).toFloat()
        
        val leftX = centerX + (needleWidth * cos(radians + Math.PI / 2)).toFloat()
        val leftY = centerY + (needleWidth * sin(radians + Math.PI / 2)).toFloat()
        
        val rightX = centerX + (needleWidth * cos(radians - Math.PI / 2)).toFloat()
        val rightY = centerY + (needleWidth * sin(radians - Math.PI / 2)).toFloat()
        
        // Create the needle path
        val needlePath = Path()
        needlePath.moveTo(tipX, tipY)
        needlePath.lineTo(leftX, leftY)
        needlePath.lineTo(rightX, rightY)
        needlePath.close()
        
        // Draw the needle with a glow effect
        val glowPaint = Paint(needlePaint)
        glowPaint.maskFilter = BlurMaskFilter(8f, BlurMaskFilter.Blur.NORMAL)
        canvas.drawPath(needlePath, glowPaint)
        canvas.drawPath(needlePath, needlePaint)
        
        // Draw the center pivot
        canvas.drawCircle(centerX, centerY, dialRadius * 0.08f, needlePaint)
        
        // Add a highlight to the pivot for a metallic look
        val highlightPaint = Paint(Paint.ANTI_ALIAS_FLAG)
        highlightPaint.color = Color.WHITE
        highlightPaint.alpha = 180
        canvas.drawCircle(centerX - dialRadius * 0.02f, centerY - dialRadius * 0.02f, dialRadius * 0.03f, highlightPaint)
    }
    
    /**
     * Draws the digital readout of the current latency value
     */
    private fun drawDigitalReadout(canvas: Canvas) {
        val readoutText = "${latency}ms"
        val readoutY = centerY + dialRadius + textPaint.textSize * 1.2f
        
        // Draw text with a cyberpunk glow effect
        val glowPaint = Paint(textPaint)
        glowPaint.maskFilter = BlurMaskFilter(6f, BlurMaskFilter.Blur.NORMAL)
        canvas.drawText(readoutText, centerX, readoutY, glowPaint)
        canvas.drawText(readoutText, centerX, readoutY, textPaint)
    }
    
    /**
     * Sets the current latency value and animates the needle if animation is enabled
     */
    fun setLatency(value: Int) {
        val boundedValue = value.coerceIn(0, maxLatency)
        
        if (this.latency != boundedValue) {
            this.latency = boundedValue
            
            if (animateNeedle) {
                // Animate the needle to the new position
                needleAnimator.cancel()
                needleAnimator.setFloatValues(currentNeedleValue, boundedValue.toFloat())
                needleAnimator.start()
            } else {
                // Update without animation
                currentNeedleValue = boundedValue.toFloat()
                invalidate()
            }
        }
    }
    
    /**
     * Sets the maximum latency value and updates the display
     */
    fun setMaxLatency(value: Int) {
        if (value > 0 && this.maxLatency != value) {
            this.maxLatency = value
            // Recalculate the current needle position based on the new max
            currentNeedleValue = (latency.toFloat() / maxLatency) * maxLatency
            invalidate()
        }
    }
    
    /**
     * Enables or disables the digital readout
     */
    fun setShowDigital(show: Boolean) {
        if (this.showDigital != show) {
            this.showDigital = show
            invalidate()
        }
    }
    
    /**
     * Enables or disables needle animation
     */
    fun setAnimateNeedle(animate: Boolean) {
        this.animateNeedle = animate
    }
}