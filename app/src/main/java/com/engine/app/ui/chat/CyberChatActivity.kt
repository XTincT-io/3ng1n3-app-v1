package com.engine.app.ui.chat

import android.content.Intent
import android.graphics.Shader
import android.graphics.SweepGradient
import android.graphics.Typeface
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import android.widget.ToggleButton
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import com.engine.app.R
import com.engine.app.model.HUDSettings
import com.engine.app.views.BinaryDisplayView
import com.engine.app.ui.components.EncryptionGauge
import com.engine.app.ui.components.GlitchAnimatedDrawable
import com.engine.app.ui.components.NetworkSpeedometer
import com.engine.app.ui.components.RadialMenu
import com.engine.app.ui.CNFTMinterActivity
import com.engine.app.ui.viewmodel.ChatViewModel

/**
 * CyberChatActivity implements the cyberpunk-themed chat interface
 * with holographic effects, glitch animations, and neon styling.
 */
class CyberChatActivity : AppCompatActivity(), RadialMenu.OnMenuItemSelectedListener {

    private lateinit var viewModel: ChatViewModel
    private lateinit var messageBubble: View
    private lateinit var binaryDisplayPanel: View
    private lateinit var binaryDisplayView: BinaryDisplayView
    private lateinit var binaryStatus: TextView
    private lateinit var binaryToggle: ToggleButton
    private lateinit var messageInput: EditText
    private lateinit var sendButton: ImageButton
    private lateinit var encryptionGauge: EncryptionGauge
    private lateinit var networkSpeedometer: NetworkSpeedometer
    private lateinit var radialMenu: RadialMenu
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cyber_chat)
        
        // Initialize ViewModel
        viewModel = ViewModelProvider(this)[ChatViewModel::class.java]
        
        // Initialize UI components
        messageBubble = findViewById(R.id.message_bubble)
        binaryDisplayPanel = findViewById(R.id.binary_display_panel)
        binaryDisplayView = binaryDisplayPanel.findViewById(R.id.binary_display)
        binaryStatus = binaryDisplayPanel.findViewById(R.id.binary_status)
        binaryToggle = findViewById(R.id.binary_toggle)
        messageInput = findViewById(R.id.message_input)
        sendButton = findViewById(R.id.send_button)
        encryptionGauge = findViewById(R.id.encryption_gauge)
        networkSpeedometer = findViewById(R.id.network_speedometer)
        radialMenu = findViewById(R.id.radial_menu)
        
        // Initialize RadialMenu
        setupRadialMenu()
        
        // Create cyberpunk HUD elements
        createHUD()
        
        // Set up observers for chat messages
        setupObservers()
        
        // Set up input handling
        setupInputHandling()
    }
    
    /**
     * Creates the cyberpunk HUD interface with neon effects and animations
     * as specified in the requirements.
     */
    fun createHUD() {
        // Create hologram effect with radial gradient shader
        val hologramColors = intArrayOf(
            ContextCompat.getColor(this, R.color.neon_green),
            ContextCompat.getColor(this, R.color.neon_green_light),
            ContextCompat.getColor(this, R.color.transparent)
        )
        
        val hologramEffect = SweepGradient(
            messageBubble.width / 2f,
            messageBubble.height / 2f,
            hologramColors,
            floatArrayOf(0f, 0.7f, 1f)
        )
        
        // Apply CRT distortion effect to message bubbles
        messageBubble.background = GlitchAnimatedDrawable(R.drawable.grid_texture)
        
        // Initialize RPM gauges for encryption status
        initializeEncryptionGauges()
        
        // Initialize digital speedometer for network latency
        initializeNetworkLatencyMeter()
    }
    
    /**
     * Initializes the animated RPM gauges showing message encryption status
     */
    private fun initializeEncryptionGauges() {
        // Set initial encryption level
        encryptionGauge.setEncryptionLevel(0.5f)
    }
    
    /**
     * Initializes the digital speedometer displaying network latency
     */
    private fun initializeNetworkLatencyMeter() {
        // Set initial network latency
        networkSpeedometer.setLatency(50)
    }
    
    /**
     * Sets up observers for chat messages and UI updates
     */
    private fun setupObservers() {
        viewModel.messages.observe(this) { messages ->
            // Update chat messages with cyberpunk styling
            // TODO: Implement message display with cyberpunk effects
        }
        
        viewModel.encryptionStatus.observe(this) { status ->
            // Update encryption status gauge
            encryptionGauge.setEncryptionLevel(status.level.toFloat() / 100f)
        }
        
        viewModel.networkLatency.observe(this) { latency ->
            // Update network latency speedometer
            networkSpeedometer.setLatency(latency)
        }
        
        viewModel.binaryModeActive.observe(this) { active ->
            // Update binary display visibility
            binaryDisplayPanel.visibility = if (active) View.VISIBLE else View.GONE
            binaryToggle.isChecked = active
            
            // Update binary status text
            binaryStatus.text = if (active) getString(R.string.binary_active) else getString(R.string.binary_inactive)
        }
    }
    
    /**
     * Sets up input handling for the chat interface
     */
    private fun setupInputHandling() {
        // Set up send button
        sendButton.setOnClickListener {
            val message = messageInput.text.toString().trim()
            if (message.isNotEmpty()) {
                viewModel.sendMessage(message)
                messageInput.text.clear()
                
                // Update binary display with the sent message
                if (viewModel.binaryModeActive.value == true) {
                    updateBinaryDisplay(message)
                }
            }
        }
        
        // Set up binary mode toggle
        binaryToggle.setOnClickListener {
            viewModel.toggleBinaryMode(binaryToggle.isChecked)
            if (binaryToggle.isChecked) {
                // Start binary display animation
                updateBinaryDisplay(messageInput.text.toString())
            }
        }
    }
    
    /**
     * Updates the binary display with the given message
     */
    private fun updateBinaryDisplay(message: String) {
        // Reset the binary display
        binaryDisplayView.reset()
        
        // Convert message to binary and display it
        val binaryMessage = message.toByteArray().joinToString("") { 
            String.format("%8s", Integer.toBinaryString(it.toInt() and 0xFF)).replace(' ', '0') 
        }
        
        // Start the binary display animation
        binaryDisplayView.startAnimation()
        
        // Update binary status with "DECODING..." for a few seconds
        binaryStatus.text = getString(R.string.decoding)
        Handler(Looper.getMainLooper()).postDelayed({
            if (viewModel.binaryModeActive.value == true) {
                binaryStatus.text = getString(R.string.binary_active)
            }
        }, 3000)
    }
    
    /**
     * Updates the HUD appearance based on user settings
     */
    fun updateHUDAppearance(settings: HUDSettings) {
        // TODO: Apply user's HUD customization settings
    }
    
    /**
     * Sets up the RadialMenu with cyberpunk-themed options
     */
    private fun setupRadialMenu() {
        // Set the menu item selected listener
        radialMenu.setOnMenuItemSelectedListener(this)
        
        // Set up long press listener on message input to show radial menu
        messageBubble.setOnLongClickListener {
            // Get the current message text
            val messageText = messageInput.text.toString().trim()
            
            // Store the message text in the ViewModel for potential NFT minting
            if (messageText.isNotEmpty()) {
                viewModel.setCurrentMessage(messageText)
            }
            
            // Show the radial menu
            radialMenu.toggle()
            true
        }
    }
    
    /**
     * Handles RadialMenu item selection
     */
    override fun onMenuItemSelected(actionId: String) {
        when (actionId) {
            "nitro_mode" -> {
                // Activate Nitro Mode
                viewModel.activateNitroMode()
            }
            "binary_toggle" -> {
                // Toggle Binary Mode
                viewModel.toggleBinaryMode(!viewModel.binaryModeActive.value!!)
            }
            "cnft_mint" -> {
                // Launch NFT Minter with current message
                launchNFTMinter()
            }
            // Handle other menu options...
        }
        
        // Close the radial menu after selection
        radialMenu.close()
    }
    
    /**
     * Launches the CNFTMinterActivity with the current message
     */
    private fun launchNFTMinter() {
        val currentMessage = viewModel.getCurrentMessage()
        if (currentMessage.isNotEmpty()) {
            val intent = Intent(this, CNFTMinterActivity::class.java).apply {
                putExtra(CNFTMinterActivity.EXTRA_MESSAGE_CONTENT, currentMessage)
            }
            startActivity(intent)
        }
    }
}