package com.engine.app.ui.components

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.*
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.view.animation.OvershootInterpolator
import androidx.core.content.ContextCompat
import androidx.core.content.withStyledAttributes
import com.engine.app.R
import kotlin.math.*

/**
 * A cyberpunk-styled radial menu inspired by a steering wheel design.
 * Features customizable menu items, haptic feedback, and animated transitions.
 */
class RadialMenu @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    // Menu properties
    private var itemCount: Int = 6
    private var radius: Float = 0f
    private var innerRadius: Float = 0f
    private var centerX: Float = 0f
    private var centerY: Float = 0f
    private var selectedItem: Int = -1
    private var enableHapticFeedback: Boolean = true
    private var showLabels: Boolean = true
    
    // Animation properties
    private var currentScale: Float = 0f
    private var isOpen: Boolean = false
    
    // Menu items
    private val menuItems = mutableListOf<MenuItem>()
    
    // Colors
    private var backgroundColor = Color.parseColor("#1A1A1A")
    private var ringColor = Color.parseColor("#39FF14") // Neon green
    private var selectedColor = Color.parseColor("#FF3366") // Neon red
    private var textColor = Color.WHITE
    
    // Paint objects
    private val backgroundPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
        color = backgroundColor
    }
    
    private val ringPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.STROKE
        color = ringColor
        strokeWidth = 3f
    }
    
    private val itemPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
    }
    
    private val textPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
        color = textColor
        textAlign = Paint.Align.CENTER
    }
    
    private val iconPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    
    // Animation
    private val scaleAnimator = ValueAnimator.ofFloat(0f, 1f).apply {
        duration = 300
        interpolator = OvershootInterpolator(1.5f)
        addUpdateListener { animation ->
            currentScale = animation.animatedValue as Float
            invalidate()
        }
    }
    
    // Vibrator service for haptic feedback
    private val vibrator: Vibrator? by lazy {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val vibratorManager = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager
            vibratorManager.defaultVibrator
        } else {
            @Suppress("DEPRECATION")
            context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        }
    }
    
    init {
        // Load attributes from XML
        context.withStyledAttributes(attrs, R.styleable.RadialMenu) {
            itemCount = getInteger(R.styleable.RadialMenu_itemCount, 6)
            enableHapticFeedback = getBoolean(R.styleable.RadialMenu_hapticFeedback, true)
            showLabels = getBoolean(R.styleable.RadialMenu_showLabels, true)
        }
        
        // Initialize with default menu items
        initDefaultMenuItems()
    }
    
    /**
     * Initialize with default menu items
     */
    private fun initDefaultMenuItems() {
        val defaultIcons = listOf(
            R.drawable.ic_nitro_mode,
            R.drawable.ic_binary_toggle,
            R.drawable.ic_cnft_mint,
            R.drawable.ic_encryption,
            R.drawable.ic_sound_profile,
            R.drawable.ic_settings
        )
        
        val defaultLabels = listOf(
            context.getString(R.string.radial_nitro),
            context.getString(R.string.binary_mode),
            context.getString(R.string.mint_cnft),
            context.getString(R.string.radial_encrypt),
            context.getString(R.string.sound_custom),
            context.getString(R.string.nav_settings)
        )
        
        val defaultActions = listOf(
            { performAction("nitro_mode") },
            { performAction("binary_toggle") },
            { performAction("cnft_mint") },
            { performAction("encryption_level") },
            { performAction("sound_profile") },
            { performAction("settings") }
        )
        
        // Create menu items (limit to itemCount)
        for (i in 0 until min(itemCount, defaultIcons.size)) {
            val icon = ContextCompat.getDrawable(context, defaultIcons[i])
            menuItems.add(MenuItem(icon, defaultLabels[i], defaultActions[i]))
        }
    }
    
    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        
        // Calculate dimensions based on the view size
        centerX = w / 2f
        centerY = h / 2f
        radius = min(w, h) * 0.4f
        innerRadius = radius * 0.3f
        
        // Adjust text size based on view dimensions
        textPaint.textSize = radius * 0.15f
    }
    
    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        
        if (isOpen) {
            // Apply scale animation
            canvas.save()
            canvas.scale(currentScale, currentScale, centerX, centerY)
            
            // Draw the menu background
            drawMenuBackground(canvas)
            
            // Draw the menu items
            drawMenuItems(canvas)
            
            canvas.restore()
        }
    }
    
    /**
     * Draws the radial menu background with cyberpunk styling
     */
    private fun drawMenuBackground(canvas: Canvas) {
        // Draw semi-transparent background
        backgroundPaint.alpha = 200
        canvas.drawCircle(centerX, centerY, radius, backgroundPaint)
        
        // Draw outer ring with glow effect
        val glowPaint = Paint(ringPaint)
        glowPaint.maskFilter = BlurMaskFilter(10f, BlurMaskFilter.Blur.NORMAL)
        canvas.drawCircle(centerX, centerY, radius, glowPaint)
        canvas.drawCircle(centerX, centerY, radius, ringPaint)
        
        // Draw inner ring
        canvas.drawCircle(centerX, centerY, innerRadius, glowPaint)
        canvas.drawCircle(centerX, centerY, innerRadius, ringPaint)
        
        // Draw dividing lines between menu items
        for (i in 0 until itemCount) {
            val angle = 2 * Math.PI * i / itemCount
            val startX = centerX + innerRadius * cos(angle).toFloat()
            val startY = centerY + innerRadius * sin(angle).toFloat()
            val endX = centerX + radius * cos(angle).toFloat()
            val endY = centerY + radius * sin(angle).toFloat()
            
            canvas.drawLine(startX, startY, endX, endY, ringPaint)
        }
    }
    
    /**
     * Draws the menu items with icons and labels
     */
    private fun drawMenuItems(canvas: Canvas) {
        for (i in 0 until min(itemCount, menuItems.size)) {
            val angle = 2 * Math.PI * i / itemCount
            val itemCenterX = centerX + (innerRadius + radius) / 2 * cos(angle).toFloat()
            val itemCenterY = centerY + (innerRadius + radius) / 2 * sin(angle).toFloat()
            
            // Draw item background if selected
            if (i == selectedItem) {
                itemPaint.color = selectedColor
                val itemRadius = (radius - innerRadius) * 0.4f
                
                // Draw with glow effect
                val glowPaint = Paint(itemPaint)
                glowPaint.maskFilter = BlurMaskFilter(10f, BlurMaskFilter.Blur.NORMAL)
                canvas.drawCircle(itemCenterX, itemCenterY, itemRadius, glowPaint)
                canvas.drawCircle(itemCenterX, itemCenterY, itemRadius, itemPaint)
            }
            
            // Draw item icon
            val icon = menuItems[i].icon
            if (icon != null) {
                val iconSize = (radius - innerRadius) * 0.5f
                val left = itemCenterX - iconSize / 2
                val top = itemCenterY - iconSize / 2
                val right = itemCenterX + iconSize / 2
                val bottom = itemCenterY + iconSize / 2
                
                icon.setBounds(left.toInt(), top.toInt(), right.toInt(), bottom.toInt())
                icon.draw(canvas)
            }
            
            // Draw item label if enabled
            if (showLabels) {
                val label = menuItems[i].label
                val labelRadius = radius + textPaint.textSize
                val labelX = centerX + labelRadius * cos(angle).toFloat()
                val labelY = centerY + labelRadius * sin(angle).toFloat() + textPaint.textSize / 3
                
                // Draw with glow effect
                val glowPaint = Paint(textPaint)
                glowPaint.maskFilter = BlurMaskFilter(3f, BlurMaskFilter.Blur.NORMAL)
                canvas.drawText(label, labelX, labelY, glowPaint)
                canvas.drawText(label, labelX, labelY, textPaint)
            }
        }
    }
    
    override fun onTouchEvent(event: MotionEvent): Boolean {
        if (!isOpen) return false
        
        val x = event.x
        val y = event.y
        
        // Calculate distance from center
        val distance = sqrt((x - centerX).pow(2) + (y - centerY).pow(2))
        
        when (event.action) {
            MotionEvent.ACTION_DOWN, MotionEvent.ACTION_MOVE -> {
                // Check if touch is within the menu ring
                if (distance >= innerRadius && distance <= radius) {
                    // Calculate angle and determine selected item
                    val angle = atan2(y - centerY, x - centerX)
                    val normalizedAngle = if (angle < 0) angle + 2 * Math.PI else angle
                    val newSelectedItem = ((normalizedAngle / (2 * Math.PI)) * itemCount).toInt() % itemCount
                    
                    if (newSelectedItem != selectedItem) {
                        selectedItem = newSelectedItem
                        if (enableHapticFeedback) {
                            performHapticFeedback()
                        }
                        invalidate()
                    }
                    return true
                } else {
                    // Deselect if touch is outside the ring
                    if (selectedItem != -1) {
                        selectedItem = -1
                        invalidate()
                    }
                }
            }
            MotionEvent.ACTION_UP -> {
                // Execute action for selected item
                if (selectedItem != -1 && distance >= innerRadius && distance <= radius) {
                    menuItems[selectedItem].action.invoke()
                    closeMenu()
                    return true
                } else if (distance > radius) {
                    // Close menu if touch is outside the menu
                    closeMenu()
                    return true
                }
            }
        }
        
        return super.onTouchEvent(event)
    }
    
    /**
     * Performs haptic feedback when selecting menu items
     */
    private fun performHapticFeedback() {
        vibrator?.let {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                it.vibrate(VibrationEffect.createOneShot(20, VibrationEffect.DEFAULT_AMPLITUDE))
            } else {
                @Suppress("DEPRECATION")
                it.vibrate(20)
            }
        }
    }
    
    /**
     * Opens the radial menu with animation
     */
    fun openMenu() {
        if (!isOpen) {
            isOpen = true
            selectedItem = -1
            scaleAnimator.cancel()
            scaleAnimator.setFloatValues(0f, 1f)
            scaleAnimator.start()
        }
    }
    
    /**
     * Closes the radial menu with animation
     */
    fun closeMenu() {
        if (isOpen) {
            scaleAnimator.cancel()
            scaleAnimator.setFloatValues(currentScale, 0f)
            scaleAnimator.start()
            scaleAnimator.addUpdateListener { animation ->
                currentScale = animation.animatedValue as Float
                if (currentScale <= 0f) {
                    isOpen = false
                }
                invalidate()
            }
        }
    }
    
    /**
     * Toggles the menu open/closed state
     */
    fun toggleMenu() {
        if (isOpen) {
            closeMenu()
        } else {
            openMenu()
        }
    }
    
    /**
     * Sets custom menu items
     */
    fun setMenuItems(items: List<MenuItem>) {
        menuItems.clear()
        menuItems.addAll(items)
        itemCount = items.size
        invalidate()
    }
    
    /**
     * Enables or disables haptic feedback
     */
    fun setHapticFeedback(enable: Boolean) {
        this.enableHapticFeedback = enable
    }
    
    /**
     * Enables or disables label display
     */
    fun setShowLabels(show: Boolean) {
        if (this.showLabels != show) {
            this.showLabels = show
            invalidate()
        }
    }
    
    /**
     * Performs the action associated with the given action ID
     */
    private fun performAction(actionId: String) {
        // This method would be implemented to handle different actions
        // For now, we'll just call the listener if set
        actionListener?.onMenuItemSelected(actionId)
    }
    
    // Interface for menu item selection callback
    interface OnMenuItemSelectedListener {
        fun onMenuItemSelected(actionId: String)
    }
    
    // Listener for menu item selection
    private var actionListener: OnMenuItemSelectedListener? = null
    
    /**
     * Sets the listener for menu item selection
     */
    fun setOnMenuItemSelectedListener(listener: OnMenuItemSelectedListener) {
        this.actionListener = listener
    }
    
    /**
     * Data class representing a menu item
     */
    data class MenuItem(
        val icon: Drawable?,
        val label: String,
        val action: () -> Unit
    )
}