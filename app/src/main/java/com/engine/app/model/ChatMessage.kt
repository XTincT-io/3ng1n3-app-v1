package com.engine.app.model

/**
 * Data class representing a chat message in the 3ng1n3 app.
 * Includes cyberpunk-themed properties like encryption status and binary representation.
 */
data class ChatMessage(
    val id: String,
    val text: String,
    val timestamp: Long,
    val isSent: Boolean,
    val isEncrypted: Boolean = true,
    val binaryRepresentation: String? = null,
    val encryptionLevel: EncryptionLevel = EncryptionLevel.MEDIUM,
    val glitchIntensity: Float = 0f
)

/**
 * Enum representing the encryption level of messages,
 * which is visualized as an RPM gauge in the UI.
 */
enum class EncryptionLevel(val value: Int, val label: String) {
    LOW(1, "BASIC"),
    MEDIUM(2, "STANDARD"),
    HIGH(3, "MAXIMUM")
}

/**
 * Enum representing the network status,
 * which is visualized as a speedometer in the UI.
 */
enum class NetworkStatus(val value: Int, val label: String) {
    OFFLINE(0, "OFFLINE"),
    UNSTABLE(1, "UNSTABLE"),
    STABLE(2, "STABLE"),
    OPTIMAL(3, "OPTIMAL")
}

/**
 * Data class for engine sound translation settings.
 * Maps text patterns to engine sound effects.
 */
data class EngineSoundProfile(
    val name: String,
    val baseFrequency: Float,
    val characterMappings: Map<Char, SoundEffect> = defaultCharacterMappings()
) {
    companion object {
        fun defaultCharacterMappings(): Map<Char, SoundEffect> {
            return mapOf(
                'a' to SoundEffect.LOW_RUMBLE,
                'e' to SoundEffect.MID_TONE,
                'i' to SoundEffect.HIGH_PITCH,
                'o' to SoundEffect.DEEP_BASS,
                'u' to SoundEffect.WHINE,
                '0' to SoundEffect.CLICK,
                '1' to SoundEffect.BEEP
                // Additional mappings can be added
            )
        }
        
        // Predefined sound profiles
        val TESLA_CYBERTRUCK = EngineSoundProfile(
            name = "TeslaCybertruck",
            baseFrequency = 220f
        )
        
        val CYBER_CHARGER = EngineSoundProfile(
            name = "CyberCharger",
            baseFrequency = 440f
        )
        
        val DIESEL_PUNK = EngineSoundProfile(
            name = "DieselPunk",
            baseFrequency = 110f
        )
    }
}

/**
 * Enum representing different sound effects for the engine sound translation.
 */
enum class SoundEffect(val frequencyMultiplier: Float, val duration: Int) {
    LOW_RUMBLE(0.5f, 300),
    MID_TONE(1.0f, 200),
    HIGH_PITCH(2.0f, 100),
    DEEP_BASS(0.25f, 400),
    WHINE(3.0f, 150),
    CLICK(1.5f, 50),
    BEEP(2.5f, 75)
}