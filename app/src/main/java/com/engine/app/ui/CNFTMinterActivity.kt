package com.engine.app.ui

import android.animation.ValueAnimator
import android.graphics.Color
import android.graphics.LinearGradient
import android.graphics.Shader
import android.os.Bundle
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import com.engine.app.R
import com.engine.app.databinding.ActivityCnftMinterBinding
import com.engine.app.viewmodels.CNFTViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.*

/**
 * Activity for minting Cyberpunk-themed cNFTs from chat messages.
 * Features holographic transaction signatures, glitch art generation,
 * and integration with Solana blockchain.
 */
class CNFTMinterActivity : AppCompatActivity() {

    companion object {
        const val EXTRA_MESSAGE_CONTENT = "message_content"
    }

    private lateinit var binding: ActivityCnftMinterBinding
    private lateinit var viewModel: CNFTViewModel
    
    // Animation properties
    private var hologramAnimator: ValueAnimator? = null
    private var signatureAnimator: ValueAnimator? = null
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Inflate layout using view binding
        binding = ActivityCnftMinterBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        // Initialize ViewModel
        viewModel = ViewModelProvider(this)[CNFTViewModel::class.java]
        
        // Get message data from intent if available
        intent.getStringExtra(EXTRA_MESSAGE_CONTENT)?.let { content ->
            binding.messageContentInput.setText(content)
            // Generate preview automatically if message is provided
            generatePreview(content)
        }
        
        // Setup UI elements
        setupUI()
        
        // Observe ViewModel data
        observeViewModel()
    }
    
    private fun setupUI() {
        // Setup toolbar
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = getString(R.string.cnft_minter_title)
        
        // Apply cyberpunk styling to the preview area
        setupHolographicEffect()
        
        // Setup buttons
        binding.generateButton.setOnClickListener {
            val content = binding.messageContentInput.text.toString()
            if (content.isNotEmpty()) {
                generatePreview(content)
            } else {
                Toast.makeText(this, R.string.error_empty_message, Toast.LENGTH_SHORT).show()
            }
        }
        
        binding.mintButton.setOnClickListener {
            if (viewModel.previewGenerated.value == true) {
                startMintingProcess()
            } else {
                Toast.makeText(this, R.string.error_generate_preview_first, Toast.LENGTH_SHORT).show()
            }
        }
        
        binding.customizeButton.setOnClickListener {
            showCustomizationOptions()
        }
        
        // Setup NFT frame selector
        setupFrameSelector()
    }
    
    /**
     * Sets up the holographic effect for the preview area
     */
    private fun setupHolographicEffect() {
        // Create a holographic gradient shader
        val colors = intArrayOf(
            Color.parseColor("#80FF00FF"), // Transparent purple
            Color.parseColor("#8000FFFF"), // Transparent cyan
            Color.parseColor("#8000FF00")  // Transparent green
        )
        
        val positions = floatArrayOf(0f, 0.5f, 1f)
        
        val hologramShader = LinearGradient(
            0f, 0f,
            binding.previewContainer.width.toFloat(), binding.previewContainer.height.toFloat(),
            colors, positions, Shader.TileMode.MIRROR
        )
        
        // Animate the holographic effect
        hologramAnimator = ValueAnimator.ofFloat(0f, 1f).apply {
            duration = 3000
            repeatCount = ValueAnimator.INFINITE
            repeatMode = ValueAnimator.REVERSE
            interpolator = AccelerateDecelerateInterpolator()
            
            addUpdateListener { animator ->
                val value = animator.animatedValue as Float
                val updatedShader = LinearGradient(
                    0f, value * binding.previewContainer.height,
                    binding.previewContainer.width.toFloat(), (value + 0.5f) * binding.previewContainer.height,
                    colors, positions, Shader.TileMode.MIRROR
                )
                binding.holographicOverlay.background.shader = updatedShader
                binding.holographicOverlay.invalidate()
            }
            
            start()
        }
    }
    
    /**
     * Sets up the NFT frame selector with different cyberpunk styles
     */
    private fun setupFrameSelector() {
        // Setup frame options
        binding.frameHolographic.setOnClickListener {
            viewModel.setSelectedFrame("holographic")
            updateSelectedFrame("holographic")
        }
        
        binding.frameGlitch.setOnClickListener {
            viewModel.setSelectedFrame("glitch")
            updateSelectedFrame("glitch")
        }
        
        binding.frameDatastream.setOnClickListener {
            viewModel.setSelectedFrame("datastream")
            updateSelectedFrame("datastream")
        }
        
        // Set default frame
        updateSelectedFrame("holographic")
    }
    
    /**
     * Updates the selected frame visual indicator
     */
    private fun updateSelectedFrame(frameType: String) {
        // Reset all frame selections
        binding.frameHolographic.setBackgroundResource(R.drawable.frame_unselected)
        binding.frameGlitch.setBackgroundResource(R.drawable.frame_unselected)
        binding.frameDatastream.setBackgroundResource(R.drawable.frame_unselected)
        
        // Highlight the selected frame
        when (frameType) {
            "holographic" -> binding.frameHolographic.setBackgroundResource(R.drawable.frame_selected)
            "glitch" -> binding.frameGlitch.setBackgroundResource(R.drawable.frame_selected)
            "datastream" -> binding.frameDatastream.setBackgroundResource(R.drawable.frame_selected)
        }
        
        // Apply the frame effect to the preview
        applyFrameEffect(frameType)
    }
    
    /**
     * Applies the selected frame effect to the preview
     */
    private fun applyFrameEffect(frameType: String) {
        when (frameType) {
            "holographic" -> {
                binding.frameOverlay.setBackgroundResource(R.drawable.frame_holographic)
                binding.holographicOverlay.visibility = View.VISIBLE
            }
            "glitch" -> {
                binding.frameOverlay.setBackgroundResource(R.drawable.frame_broken_monitor)
                binding.holographicOverlay.visibility = View.GONE
            }
            "datastream" -> {
                binding.frameOverlay.setBackgroundResource(R.drawable.frame_datastream)
                binding.holographicOverlay.visibility = View.VISIBLE
            }
        }
    }
    
    /**
     * Observes ViewModel data changes
     */
    private fun observeViewModel() {
        // Observe preview generation status
        viewModel.previewGenerated.observe(this) { generated ->
            binding.mintButton.isEnabled = generated
            binding.customizeButton.isEnabled = generated
            binding.previewProgressBar.visibility = if (generated) View.GONE else View.VISIBLE
        }
        
        // Observe preview image
        viewModel.previewImage.observe(this) { bitmap ->
            if (bitmap != null) {
                binding.previewImageView.setImageBitmap(bitmap)
                binding.previewPlaceholder.visibility = View.GONE
            } else {
                binding.previewPlaceholder.visibility = View.VISIBLE
            }
        }
        
        // Observe minting status
        viewModel.mintingStatus.observe(this) { status ->
            when (status) {
                CNFTViewModel.MintingStatus.IDLE -> {
                    binding.mintingStatusContainer.visibility = View.GONE
                    binding.mintButton.isEnabled = viewModel.previewGenerated.value == true
                    binding.generateButton.isEnabled = true
                }
                CNFTViewModel.MintingStatus.MINTING -> {
                    binding.mintingStatusContainer.visibility = View.VISIBLE
                    binding.mintingStatusText.text = getString(R.string.minting_in_progress)
                    binding.mintingProgressBar.isIndeterminate = true
                    binding.mintButton.isEnabled = false
                    binding.generateButton.isEnabled = false
                    animateSignature(true)
                }
                CNFTViewModel.MintingStatus.SUCCESS -> {
                    binding.mintingStatusContainer.visibility = View.VISIBLE
                    binding.mintingStatusText.text = getString(R.string.minting_success)
                    binding.mintingProgressBar.isIndeterminate = false
                    binding.mintingProgressBar.progress = 100
                    binding.mintButton.isEnabled = false
                    binding.generateButton.isEnabled = true
                    animateSignature(false)
                    
                    // Show success message
                    Toast.makeText(this, R.string.cnft_minted_successfully, Toast.LENGTH_LONG).show()
                }
                CNFTViewModel.MintingStatus.ERROR -> {
                    binding.mintingStatusContainer.visibility = View.VISIBLE
                    binding.mintingStatusText.text = getString(R.string.minting_error)
                    binding.mintingProgressBar.isIndeterminate = false
                    binding.mintingProgressBar.progress = 0
                    binding.mintButton.isEnabled = true
                    binding.generateButton.isEnabled = true
                    animateSignature(false)
                    
                    // Show error message
                    Toast.makeText(this, R.string.error_minting_failed, Toast.LENGTH_LONG).show()
                }
            }
        }
    }
    
    /**
     * Generates a preview of the cNFT using AI
     */
    private fun generatePreview(content: String) {
        // Show loading state
        binding.previewProgressBar.visibility = View.VISIBLE
        binding.previewPlaceholder.visibility = View.GONE
        viewModel.setPreviewGenerated(false)
        
        // Generate preview using ViewModel
        viewModel.generatePreview(content)
    }
    
    /**
     * Starts the minting process for the cNFT
     */
    private fun startMintingProcess() {
        // Update UI to show minting in progress
        viewModel.setMintingStatus(CNFTViewModel.MintingStatus.MINTING)
        
        // Get minting parameters
        val content = binding.messageContentInput.text.toString()
        val frameType = viewModel.selectedFrame.value ?: "holographic"
        
        // Start minting process using ViewModel
        viewModel.mintCNFT(content, frameType)
    }
    
    /**
     * Shows customization options for the cNFT
     */
    private fun showCustomizationOptions() {
        // Toggle visibility of customization panel
        val isVisible = binding.customizationPanel.visibility == View.VISIBLE
        binding.customizationPanel.visibility = if (isVisible) View.GONE else View.VISIBLE
        
        // Update button text
        binding.customizeButton.text = getString(
            if (isVisible) R.string.customize_cnft else R.string.hide_customization
        )
    }
    
    /**
     * Animates the holographic signature effect
     */
    private fun animateSignature(active: Boolean) {
        if (active) {
            // Create and start signature animation
            signatureAnimator = ValueAnimator.ofInt(0, 100).apply {
                duration = 2000
                repeatCount = ValueAnimator.INFINITE
                interpolator = AccelerateDecelerateInterpolator()
                
                addUpdateListener { animator ->
                    val value = animator.animatedValue as Int
                    binding.signatureOverlay.alpha = (Math.sin(value * Math.PI / 50) * 0.5 + 0.5).toFloat()
                }
                
                start()
            }
            
            // Show signature overlay
            binding.signatureOverlay.visibility = View.VISIBLE
        } else {
            // Stop signature animation
            signatureAnimator?.cancel()
            signatureAnimator = null
            
            // Hide signature overlay with fade out
            binding.signatureOverlay.animate()
                .alpha(0f)
                .setDuration(500)
                .withEndAction {
                    binding.signatureOverlay.visibility = View.GONE
                    binding.signatureOverlay.alpha = 1f
                }
                .start()
        }
    }
    
    override fun onDestroy() {
        super.onDestroy()
        
        // Clean up animators
        hologramAnimator?.cancel()
        hologramAnimator = null
        
        signatureAnimator?.cancel()
        signatureAnimator = null
    }
    
    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}