package com.engine.app.ui.components

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import com.engine.app.R
import com.engine.app.model.EngineSoundProfile
import com.engine.app.model.HUDWidget
import kotlin.math.min

/**
 * Custom ViewGroup that implements a cyberpunk-themed dashboard panel
 * with draggable widgets, neon grid background, and customizable appearance.
 */
class CyberPanel @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : ViewGroup(context, attrs, defStyleAttr) {

    // Panel properties
    private var neonColor: Int = Color.parseColor("#39FF14") // Default neon green
    private var widgetsDraggable: Boolean = true
    private var engineSoundProfile: String = "TeslaCybertruck"
    private var gridDensity: Float = 20f
    private var gridOpacity: Float = 0.5f
    
    // Paint objects
    private val gridPaint = Paint().apply {
        style = Paint.Style.STROKE
        strokeWidth = 2f
        color = neonColor
    }
    
    private val borderPaint = Paint().apply {
        style = Paint.Style.STROKE
        strokeWidth = 4f
        color = neonColor
    }
    
    // Widgets contained in this panel
    private val widgets = mutableListOf<HUDWidget>()
    
    // Dragging state
    private var isDragging = false
    private var draggedChildIndex = -1
    private var lastTouchX = 0f
    private var lastTouchY = 0f
    
    init {
        // Extract custom attributes
        val typedArray = context.obtainStyledAttributes(attrs, R.styleable.CyberPanel)
        try {
            neonColor = typedArray.getColor(
                R.styleable.CyberPanel_neonColor,
                ContextCompat.getColor(context, R.color.neon_green)
            )
            widgetsDraggable = typedArray.getBoolean(R.styleable.CyberPanel_widgetsDraggable, true)
            engineSoundProfile = typedArray.getString(R.styleable.CyberPanel_engineSoundProfile) ?: "TeslaCybertruck"
            gridDensity = typedArray.getFloat(R.styleable.CyberPanel_gridDensity, 20f)
            gridOpacity = typedArray.getFloat(R.styleable.CyberPanel_gridOpacity, 0.5f)
        } finally {
            typedArray.recycle()
        }
        
        // Update paint objects with the attributes
        gridPaint.color = neonColor
        gridPaint.alpha = (gridOpacity * 255).toInt()
        borderPaint.color = neonColor
        
        // Set background to transparent to allow drawing custom background
        setBackgroundColor(Color.TRANSPARENT)
        
        // Enable view group overlay for visual effects
        setWillNotDraw(false)
    }
    
    /**
     * Measures the panel and its children
     */
    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val width = MeasureSpec.getSize(widthMeasureSpec)
        val height = MeasureSpec.getSize(heightMeasureSpec)
        
        // Measure each child
        for (i in 0 until childCount) {
            val child = getChildAt(i)
            if (child.visibility != GONE) {
                // Measure child with its own measure specs
                measureChild(child, widthMeasureSpec, heightMeasureSpec)
            }
        }
        
        setMeasuredDimension(width, height)
    }
    
    /**
     * Positions the panel's children
     */
    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        val width = r - l
        val height = b - t
        
        // Position each child based on its relative coordinates
        for (i in 0 until childCount) {
            val child = getChildAt(i)
            if (child.visibility != GONE) {
                val layoutParams = child.layoutParams as LayoutParams
                
                // Convert relative coordinates to absolute positions
                val childLeft = (layoutParams.x * width).toInt()
                val childTop = (layoutParams.y * height).toInt()
                val childRight = childLeft + child.measuredWidth
                val childBottom = childTop + child.measuredHeight
                
                // Layout the child
                child.layout(childLeft, childTop, childRight, childBottom)
            }
        }
    }
    
    /**
     * Draws the cyberpunk grid background and border
     */
    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        
        val width = width.toFloat()
        val height = height.toFloat()
        
        // Draw grid lines
        val cellSize = min(width, height) / gridDensity
        
        // Draw horizontal grid lines
        for (i in 0..height.toInt() step cellSize.toInt()) {
            canvas.drawLine(0f, i.toFloat(), width, i.toFloat(), gridPaint)
        }
        
        // Draw vertical grid lines
        for (i in 0..width.toInt() step cellSize.toInt()) {
            canvas.drawLine(i.toFloat(), 0f, i.toFloat(), height, gridPaint)
        }
        
        // Draw border with neon effect
        val borderPath = Path().apply {
            moveTo(0f, 0f)
            lineTo(width, 0f)
            lineTo(width, height)
            lineTo(0f, height)
            close()
        }
        canvas.drawPath(borderPath, borderPaint)
    }
    
    /**
     * Handles touch events for dragging widgets
     */
    override fun onTouchEvent(event: MotionEvent): Boolean {
        if (!widgetsDraggable) {
            return super.onTouchEvent(event)
        }
        
        val x = event.x
        val y = event.y
        
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                // Find which child was touched
                draggedChildIndex = findChildUnder(x, y)
                if (draggedChildIndex >= 0) {
                    isDragging = true
                    lastTouchX = x
                    lastTouchY = y
                    return true
                }
            }
            
            MotionEvent.ACTION_MOVE -> {
                if (isDragging && draggedChildIndex >= 0) {
                    val child = getChildAt(draggedChildIndex)
                    val layoutParams = child.layoutParams as LayoutParams
                    
                    // Calculate new position
                    val deltaX = x - lastTouchX
                    val deltaY = y - lastTouchY
                    
                    // Update relative coordinates
                    layoutParams.x = (layoutParams.x + deltaX / width).coerceIn(0f, 1f)
                    layoutParams.y = (layoutParams.y + deltaY / height).coerceIn(0f, 1f)
                    
                    // Request layout to update child position
                    requestLayout()
                    
                    // Update last touch position
                    lastTouchX = x
                    lastTouchY = y
                    return true
                }
            }
            
            MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                isDragging = false
                draggedChildIndex = -1
            }
        }
        
        return super.onTouchEvent(event)
    }
    
    /**
     * Finds the child view under the given coordinates
     */
    private fun findChildUnder(x: Float, y: Float): Int {
        for (i in childCount - 1 downTo 0) {
            val child = getChildAt(i)
            if (child.visibility == VISIBLE) {
                val left = child.left
                val right = child.right
                val top = child.top
                val bottom = child.bottom
                
                if (x >= left && x <= right && y >= top && y <= bottom) {
                    return i
                }
            }
        }
        return -1
    }
    
    /**
     * Updates the neon color of the panel
     */
    fun setNeonColor(color: Int) {
        neonColor = color
        gridPaint.color = color
        borderPaint.color = color
        invalidate()
    }
    
    /**
     * Updates the engine sound profile
     */
    fun setEngineSoundProfile(profile: String) {
        engineSoundProfile = profile
        // Trigger sound profile change event
    }
    
    /**
     * Adds a widget to the panel
     */
    fun addWidget(widget: HUDWidget) {
        widgets.add(widget)
        // Create and add the corresponding view
        // This would be implemented based on widget type
    }
    
    /**
     * Custom LayoutParams for CyberPanel children
     */
    class LayoutParams : ViewGroup.LayoutParams {
        // Relative coordinates (0.0 to 1.0)
        var x: Float = 0.5f
        var y: Float = 0.5f
        
        constructor(width: Int, height: Int) : super(width, height)
        
        constructor(width: Int, height: Int, x: Float, y: Float) : super(width, height) {
            this.x = x.coerceIn(0f, 1f)
            this.y = y.coerceIn(0f, 1f)
        }
        
        constructor(source: ViewGroup.LayoutParams) : super(source)
    }
    
    /**
     * Generate layout parameters for a child view
     */
    override fun generateLayoutParams(attrs: AttributeSet): ViewGroup.LayoutParams {
        return LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT)
    }
    
    override fun generateDefaultLayoutParams(): ViewGroup.LayoutParams {
        return LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT)
    }
    
    override fun generateLayoutParams(p: ViewGroup.LayoutParams): ViewGroup.LayoutParams {
        return LayoutParams(p)
    }
    
    override fun checkLayoutParams(p: ViewGroup.LayoutParams): Boolean {
        return p is LayoutParams
    }
}