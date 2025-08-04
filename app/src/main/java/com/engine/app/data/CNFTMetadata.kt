package com.engine.app.data

import android.graphics.Bitmap

/**
 * Data class representing metadata for a compressed NFT (cNFT)
 * This follows the Metaplex NFT standard structure with additional fields
 * for cyberpunk-themed attributes
 */
data class CNFTMetadata(
    // Basic NFT metadata
    val name: String,
    val description: String,
    val image: Bitmap?,
    
    // Additional attributes as key-value pairs
    val attributes: Map<String, String> = emptyMap(),
    
    // Optional fields
    val externalUrl: String? = null,
    val animationUrl: String? = null,
    
    // Cyberpunk-specific metadata
    val frameType: String = "holographic",
    val glitchIntensity: Float = 0.5f,
    val neonColor: String = "#39FF14", // Default neon green
    
    // Blockchain-related fields (would be populated after minting)
    val mintAddress: String? = null,
    val ownerAddress: String? = null,
    val createdAt: Long = System.currentTimeMillis()
) {
    /**
     * Converts the metadata to a JSON-compatible Map
     * This would be used when uploading metadata to IPFS/Arweave
     */
    fun toJsonMap(): Map<String, Any> {
        val map = mutableMapOf<String, Any>(
            "name" to name,
            "description" to description,
            "attributes" to attributes.map { (key, value) ->
                mapOf("trait_type" to key, "value" to value)
            }
        )
        
        // Add optional fields if they exist
        externalUrl?.let { map["external_url"] = it }
        animationUrl?.let { map["animation_url"] = it }
        
        // Add cyberpunk-specific metadata
        map["properties"] = mapOf(
            "frame_type" to frameType,
            "glitch_intensity" to glitchIntensity,
            "neon_color" to neonColor,
            "created_at" to createdAt
        )
        
        return map
    }
    
    companion object {
        /**
         * Creates a sample cNFT metadata for testing purposes
         */
        fun createSample(): CNFTMetadata {
            return CNFTMetadata(
                name = "3ng1n3 Test Message #1337",
                description = "A test message from the 3ng1n3 app",
                image = null,
                attributes = mapOf(
                    "frame" to "holographic",
                    "timestamp" to System.currentTimeMillis().toString(),
                    "messageLength" to "42"
                ),
                frameType = "holographic",
                glitchIntensity = 0.7f,
                neonColor = "#00FFFF" // Cyan
            )
        }
    }
}