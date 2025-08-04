package com.engine.app.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.engine.app.model.ChatMessage
import com.engine.app.model.EncryptionLevel
import com.engine.app.model.NetworkStatus
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.*
import kotlin.random.Random

/**
 * ViewModel for the CyberChat screen that handles message processing,
 * encryption status, and network latency simulation.
 */
class ChatViewModel : ViewModel() {

    // Chat messages
    private val _messages = MutableLiveData<List<ChatMessage>>(emptyList())
    val messages: LiveData<List<ChatMessage>> = _messages
    
    // Encryption status (displayed as RPM gauge)
    private val _encryptionStatus = MutableLiveData<EncryptionLevel>(EncryptionLevel.MEDIUM)
    val encryptionStatus: LiveData<EncryptionLevel> = _encryptionStatus
    
    // Network latency (displayed as speedometer)
    private val _networkLatency = MutableLiveData<Int>(50) // ms
    val networkLatency: LiveData<Int> = _networkLatency
    
    // Binary translation mode
    private val _binaryMode = MutableLiveData<Boolean>(false)
    val binaryMode: LiveData<Boolean> = _binaryMode
    
    // Binary mode active status for UI display
    private val _binaryModeActive = MutableLiveData<Boolean>(false)
    val binaryModeActive: LiveData<Boolean> = _binaryModeActive
    
    // Current message for NFT minting
    private var currentMessage: String = ""
    
    init {
        // Simulate network latency fluctuations
        simulateNetworkActivity()
        
        // Add some initial messages for testing
        addSampleMessages()
    }
    
    /**
     * Sends a new chat message
     */
    fun sendMessage(text: String) {
        if (text.isBlank()) return
        
        viewModelScope.launch {
            // Simulate encryption process
            _encryptionStatus.value = EncryptionLevel.HIGH
            delay(300) // Encryption animation time
            
            val currentMessages = _messages.value?.toMutableList() ?: mutableListOf()
            
            // Create new message
            val newMessage = ChatMessage(
                id = UUID.randomUUID().toString(),
                text = text,
                timestamp = System.currentTimeMillis(),
                isSent = true,
                isEncrypted = true,
                binaryRepresentation = if (_binaryMode.value == true) textToBinary(text) else null
            )
            
            // Add to messages
            currentMessages.add(newMessage)
            _messages.value = currentMessages
            
            // Reset encryption status after sending
            delay(500)
            _encryptionStatus.value = EncryptionLevel.MEDIUM
        }
    }
    
    /**
     * Toggles binary translation mode
     */
    fun toggleBinaryMode(active: Boolean) {
        _binaryMode.value = active
        _binaryModeActive.value = active
    }
    
    /**
     * Toggles binary translation mode (no parameter version)
     */
    fun toggleBinaryMode() {
        val newValue = _binaryMode.value != true
        _binaryMode.value = newValue
        _binaryModeActive.value = newValue
    }
    
    /**
     * Activates "Nitro Mode" for AI message sorting
     */
    fun activateNitroMode() {
        viewModelScope.launch {
            // Simulate nitro boost effect
            for (i in 1..5) {
                _networkLatency.value = 10 // Super fast
                delay(100)
                _networkLatency.value = 5
                delay(100)
            }
            
            // Sort messages by relevance (simulated)
            val currentMessages = _messages.value?.toMutableList() ?: return@launch
            currentMessages.sortByDescending { it.timestamp }
            _messages.value = currentMessages
            
            // Return to normal after boost
            delay(1000)
            simulateNetworkActivity()
        }
    }
    
    /**
     * Simulates network activity with random latency fluctuations
     */
    private fun simulateNetworkActivity() {
        viewModelScope.launch {
            while (true) {
                // Random latency between 30-120ms
                val newLatency = Random.nextInt(30, 120)
                _networkLatency.value = newLatency
                
                // Update encryption level based on latency
                _encryptionStatus.value = when {
                    newLatency < 50 -> EncryptionLevel.HIGH
                    newLatency < 80 -> EncryptionLevel.MEDIUM
                    else -> EncryptionLevel.LOW
                }
                
                delay(2000) // Update every 2 seconds
            }
        }
    }
    
    /**
     * Converts text to binary representation
     */
    private fun textToBinary(text: String): String {
        return text.map { char ->
            char.code.toString(2).padStart(8, '0')
        }.joinToString(" ")
    }
    
    /**
     * Adds sample messages for testing
     */
    private fun addSampleMessages() {
        val sampleMessages = listOf(
            ChatMessage(
                id = UUID.randomUUID().toString(),
                text = "Welcome to 3ng1n3 Cyberpunk Chat",
                timestamp = System.currentTimeMillis() - 60000,
                isSent = false,
                isEncrypted = true,
                binaryRepresentation = "01010111 01100101 01101100 01100011 01101111 01101101 01100101"
            ),
            ChatMessage(
                id = UUID.randomUUID().toString(),
                text = "Encryption active. Network secure.",
                timestamp = System.currentTimeMillis() - 30000,
                isSent = false,
                isEncrypted = true,
                binaryRepresentation = null
            )
        )
        
        _messages.value = sampleMessages
    }
    
    /**
     * Stores the current message for potential NFT minting
     */
    fun setCurrentMessage(message: String) {
        currentMessage = message
    }
    
    /**
     * Retrieves the current message for NFT minting
     */
    fun getCurrentMessage(): String {
        return currentMessage
    }
}