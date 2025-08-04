package com.engine.app.model

import androidx.compose.ui.graphics.Color

/**
 * Data class for HUD customization settings as specified in the requirements.
 * Allows users to customize the cyberpunk HUD interface with different colors,
 * opacity levels, widget positions, and sound profiles.
 */
data class HUDSettings(
    val hudColor: String = "#39FF14", // Default neon green
    val opacity: Float = 0.85f,
    val widgetPosition: Map<String, Pair<Float, Float>> = defaultWidgetPositions(),
    val soundProfile: String = "TeslaCybertruck" // Custom engine sound packs
) {
    companion object {
        /**
         * Provides default widget positions for the HUD layout.
         * Coordinates are normalized between 0.0 and 1.0 for relative positioning.
         */
        fun defaultWidgetPositions(): Map<String, Pair<Float, Float>> = mapOf(
            "cNFTMinter" to Pair(0.7f, 0.2f),
            "EncryptionGauge" to Pair(0.1f, 0.8f),
            "NetworkStatus" to Pair(0.9f, 0.8f),
            "MessageCounter" to Pair(0.5f, 0.1f),
            "SpeedometerLatency" to Pair(0.5f, 0.9f),
            "RadialMenu" to Pair(0.5f, 0.5f)
        )
        
        /**
         * Predefined color schemes for quick customization
         */
        val COLOR_SCHEMES = mapOf(
            "ToxicGreen" to "#39FF14",
            "CorpBlue" to "#00FFFF",
            "RiotPurple" to "#FF00FF",
            "HazardOrange" to "#FF9900",
            "BloodRed" to "#FF0000"
        )
        
        /**
         * Available sound profiles for engine sound translation
         */
        val SOUND_PROFILES = listOf(
            "TeslaCybertruck",
            "CyberCharger",
            "DieselPunk",
            "QuantumDrive"
        )
    }
    
    /**
     * Converts the HUD color from hex string to Color object
     */
    fun getHudColorAsColor(): Color {
        return try {
            Color(android.graphics.Color.parseColor(hudColor))
        } catch (e: Exception) {
            // Default to neon green if parsing fails
            Color(android.graphics.Color.parseColor("#39FF14"))
        }
    }
    
    /**
     * Creates a copy with updated widget position
     */
    fun updateWidgetPosition(widgetName: String, position: Pair<Float, Float>): HUDSettings {
        val updatedPositions = widgetPosition.toMutableMap()
        updatedPositions[widgetName] = position
        return copy(widgetPosition = updatedPositions)
    }
}

/**
 * Represents a HUD widget with its properties
 */
data class HUDWidget(
    val name: String,
    val type: WidgetType,
    val position: Pair<Float, Float>,
    val size: Pair<Float, Float> = Pair(0.2f, 0.2f), // Default size as percentage of screen
    val isVisible: Boolean = true
)

/**
 * Enum defining the types of widgets available in the HUD
 */
enum class WidgetType {
    GAUGE,
    STATUS,
    BUTTON,
    METER,
    DISPLAY,
    MENU
}