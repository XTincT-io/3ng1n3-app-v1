package com.engine.app.viewmodels

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.engine.app.data.CNFTMetadata
import com.engine.app.data.SolanaRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Random

/**
 * ViewModel for handling cNFT minting operations and preview generation
 */
class CNFTViewModel : ViewModel() {

    // Repository for Solana blockchain interactions
    private val solanaRepository = SolanaRepository()
    
    // Minting status states
    enum class MintingStatus {
        IDLE, MINTING, SUCCESS, ERROR
    }
    
    // LiveData for UI state
    private val _previewGenerated = MutableLiveData<Boolean>(false)
    val previewGenerated: LiveData<Boolean> = _previewGenerated
    
    private val _previewImage = MutableLiveData<Bitmap?>(null)
    val previewImage: LiveData<Bitmap?> = _previewImage
    
    private val _mintingStatus = MutableLiveData<MintingStatus>(MintingStatus.IDLE)
    val mintingStatus: LiveData<MintingStatus> = _mintingStatus
    
    private val _selectedFrame = MutableLiveData<String>("holographic")
    val selectedFrame: LiveData<String> = _selectedFrame
    
    private val _mintedNFTAddress = MutableLiveData<String?>(null)
    val mintedNFTAddress: LiveData<String?> = _mintedNFTAddress
    
    // Random for generating glitch effects
    private val random = Random()
    
    /**
     * Sets the preview generation status
     */
    fun setPreviewGenerated(generated: Boolean) {
        _previewGenerated.value = generated
    }
    
    /**
     * Sets the minting status
     */
    fun setMintingStatus(status: MintingStatus) {
        _mintingStatus.value = status
    }
    
    /**
     * Sets the selected frame type
     */
    fun setSelectedFrame(frameType: String) {
        _selectedFrame.value = frameType
    }
    
    /**
     * Generates a preview image for the cNFT based on message content
     * Uses AI-inspired algorithms to create cyberpunk-style art
     */
    fun generatePreview(messageContent: String) {
        viewModelScope.launch {
            try {
                // Simulate AI processing time
                delay(1500)
                
                // Generate the preview image
                val previewBitmap = withContext(Dispatchers.Default) {
                    generateCyberpunkArt(messageContent)
                }
                
                // Update LiveData
                _previewImage.postValue(previewBitmap)
                _previewGenerated.postValue(true)
                
            } catch (e: Exception) {
                Log.e("CNFTViewModel", "Error generating preview: ${e.message}")
                _previewGenerated.postValue(false)
            }
        }
    }
    
    /**
     * Mints a cNFT on the Solana blockchain
     */
    fun mintCNFT(messageContent: String, frameType: String) {
        viewModelScope.launch {
            try {
                // Update status to minting
                _mintingStatus.postValue(MintingStatus.MINTING)
                
                // Create metadata for the NFT
                val metadata = CNFTMetadata(
                    name = "3ng1n3 Message #${System.currentTimeMillis() % 10000}",
                    description = "Cyberpunk message from 3ng1n3 app: $messageContent",
                    image = _previewImage.value,
                    attributes = mapOf(
                        "frame" to frameType,
                        "timestamp" to System.currentTimeMillis().toString(),
                        "messageLength" to messageContent.length.toString()
                    )
                )
                
                // Simulate blockchain interaction
                delay(3000) // Simulating network latency and transaction time
                
                // Mint the NFT using the repository
                val result = solanaRepository.mintCNFT(metadata)
                
                if (result.isSuccess) {
                    // Update with success
                    _mintedNFTAddress.postValue(result.getOrNull())
                    _mintingStatus.postValue(MintingStatus.SUCCESS)
                } else {
                    // Update with error
                    _mintingStatus.postValue(MintingStatus.ERROR)
                    Log.e("CNFTViewModel", "Minting error: ${result.exceptionOrNull()?.message}")
                }
                
            } catch (e: Exception) {
                Log.e("CNFTViewModel", "Error minting cNFT: ${e.message}")
                _mintingStatus.postValue(MintingStatus.ERROR)
            }
        }
    }
    
    /**
     * Generates cyberpunk-style art based on the message content
     * This is a placeholder implementation that creates glitch art
     */
    private fun generateCyberpunkArt(messageContent: String): Bitmap {
        // Create a bitmap for the artwork
        val width = 800
        val height = 800
        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        
        // Fill background with dark color
        val backgroundPaint = Paint().apply {
            color = Color.parseColor("#0D0E1B")
        }
        canvas.drawRect(0f, 0f, width.toFloat(), height.toFloat(), backgroundPaint)
        
        // Generate a color palette based on the message
        val colors = generateColorPalette(messageContent)
        
        // Draw grid lines
        drawCyberpunkGrid(canvas, width, height, colors[0])
        
        // Draw glitch blocks
        drawGlitchBlocks(canvas, width, height, colors)
        
        // Draw binary representation of the message
        drawBinaryData(canvas, width, height, messageContent, colors[1])
        
        // Add some noise/static effect
        addNoiseEffect(canvas, width, height)
        
        return bitmap
    }
    
    /**
     * Generates a color palette based on the message content
     */
    private fun generateColorPalette(message: String): List<Int> {
        val palette = mutableListOf<Int>()
        
        // Use the message hash to seed color generation
        val seed = message.hashCode()
        val seededRandom = Random(seed.toLong())
        
        // Generate primary color - always a neon shade
        val hue = seededRandom.nextFloat() * 360
        val primaryColor = Color.HSVToColor(floatArrayOf(hue, 0.9f, 0.9f))
        palette.add(primaryColor)
        
        // Generate secondary color - complementary to primary
        val secondaryHue = (hue + 180) % 360
        val secondaryColor = Color.HSVToColor(floatArrayOf(secondaryHue, 0.8f, 0.8f))
        palette.add(secondaryColor)
        
        // Generate accent color - offset from primary
        val accentHue = (hue + 60) % 360
        val accentColor = Color.HSVToColor(floatArrayOf(accentHue, 0.7f, 1.0f))
        palette.add(accentColor)
        
        // Add a dark color for contrast
        palette.add(Color.parseColor("#121212"))
        
        return palette
    }
    
    /**
     * Draws a cyberpunk-style grid on the canvas
     */
    private fun drawCyberpunkGrid(canvas: Canvas, width: Int, height: Int, gridColor: Int) {
        val paint = Paint().apply {
            color = gridColor
            alpha = 80
            strokeWidth = 1f
            style = Paint.Style.STROKE
        }
        
        // Draw horizontal grid lines
        val horizontalSpacing = height / 20
        for (i in 0..20) {
            val y = i * horizontalSpacing.toFloat()
            canvas.drawLine(0f, y, width.toFloat(), y, paint)
        }
        
        // Draw vertical grid lines
        val verticalSpacing = width / 20
        for (i in 0..20) {
            val x = i * verticalSpacing.toFloat()
            canvas.drawLine(x, 0f, x, height.toFloat(), paint)
        }
    }
    
    /**
     * Draws glitch blocks on the canvas
     */
    private fun drawGlitchBlocks(canvas: Canvas, width: Int, height: Int, colors: List<Int>) {
        val paint = Paint()
        
        // Number of glitch blocks to draw
        val numBlocks = random.nextInt(10) + 5
        
        for (i in 0 until numBlocks) {
            // Randomize block position and size
            val blockWidth = random.nextInt(width / 2)
            val blockHeight = random.nextInt(height / 10) + 10
            val x = random.nextInt(width - blockWidth)
            val y = random.nextInt(height - blockHeight)
            
            // Randomize color and transparency
            paint.color = colors[random.nextInt(colors.size)]
            paint.alpha = random.nextInt(200) + 55
            
            // Draw the block
            canvas.drawRect(x.toFloat(), y.toFloat(), 
                           (x + blockWidth).toFloat(), 
                           (y + blockHeight).toFloat(), paint)
        }
    }
    
    /**
     * Draws binary representation of the message on the canvas
     */
    private fun drawBinaryData(canvas: Canvas, width: Int, height: Int, message: String, textColor: Int) {
        val paint = Paint().apply {
            color = textColor
            alpha = 180
            textSize = 16f
            isAntiAlias = true
        }
        
        // Convert message to binary
        val binaryString = message.map { char ->
            char.code.toString(2).padStart(8, '0')
        }.joinToString(" ")
        
        // Split into lines to fit canvas width
        val textWidth = paint.measureText("00000000 ") * 5 // Approximate width of 5 bytes
        val charsPerLine = (width / (paint.measureText("0") * 1.2)).toInt()
        
        // Draw binary data in rows
        var y = height / 3f
        var index = 0
        while (index < binaryString.length && y < height) {
            val endIndex = minOf(index + charsPerLine, binaryString.length)
            val line = binaryString.substring(index, endIndex)
            canvas.drawText(line, 20f, y, paint)
            y += paint.textSize * 1.5f
            index = endIndex
        }
    }
    
    /**
     * Adds a noise/static effect to the canvas
     */
    private fun addNoiseEffect(canvas: Canvas, width: Int, height: Int) {
        val paint = Paint()
        val pixelDensity = 10 // Lower means more noise
        
        for (x in 0 until width step pixelDensity) {
            for (y in 0 until height step pixelDensity) {
                if (random.nextFloat() < 0.1f) { // 10% chance of noise pixel
                    val noiseColor = if (random.nextBoolean()) {
                        Color.WHITE
                    } else {
                        Color.parseColor("#00FFFF") // Cyan
                    }
                    
                    paint.color = noiseColor
                    paint.alpha = random.nextInt(100) + 50
                    
                    val noiseSize = random.nextInt(pixelDensity / 2) + 1
                    canvas.drawRect(
                        x.toFloat(),
                        y.toFloat(),
                        (x + noiseSize).toFloat(),
                        (y + noiseSize).toFloat(),
                        paint
                    )
                }
            }
        }
    }
}