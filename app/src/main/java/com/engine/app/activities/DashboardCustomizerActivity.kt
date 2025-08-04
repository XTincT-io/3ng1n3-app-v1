package com.engine.app.activities

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.os.VibrationEffect
import android.os.Vibrator
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.SeekBar
import android.widget.Spinner
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.engine.app.Engine3Application
import com.engine.app.R
import com.engine.app.models.ChatMessage.EngineSoundProfile
import com.engine.app.models.HUDSettings
import com.engine.app.models.HUDWidget
import com.engine.app.models.WidgetType
import com.engine.app.views.CyberPanel

class DashboardCustomizerActivity : AppCompatActivity() {
    
    private lateinit var cyberPanel: CyberPanel
    private lateinit var colorSpinner: Spinner
    private lateinit var soundProfileSpinner: Spinner
    private lateinit var opacitySeekBar: SeekBar
    private lateinit var opacityValueText: TextView
    private lateinit var gridDensitySeekBar: SeekBar
    private lateinit var gridDensityValueText: TextView
    private lateinit var vibrator: Vibrator
    private lateinit var hudSettings: HUDSettings
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dashboard_customizer)
        
        // Get the application instance to access HUD settings
        val app = application as Engine3Application
        hudSettings = app.hudSettings
        
        // Initialize vibrator for haptic feedback
        vibrator = getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        
        // Initialize views
        cyberPanel = findViewById(R.id.cyber_panel)
        colorSpinner = findViewById(R.id.color_scheme_spinner)
        soundProfileSpinner = findViewById(R.id.sound_profile_spinner)
        opacitySeekBar = findViewById(R.id.opacity_seekbar)
        opacityValueText = findViewById(R.id.opacity_value)
        gridDensitySeekBar = findViewById(R.id.grid_density_seekbar)
        gridDensityValueText = findViewById(R.id.grid_density_value)
        
        // Setup color scheme spinner
        setupColorSchemeSpinner()
        
        // Setup sound profile spinner
        setupSoundProfileSpinner()
        
        // Setup opacity seekbar
        setupOpacitySeekBar()
        
        // Setup grid density seekbar
        setupGridDensitySeekBar()
        
        // Load current settings
        loadCurrentSettings()
        
        // Setup sample widgets
        setupSampleWidgets()
    }
    
    private fun setupColorSchemeSpinner() {
        val colorSchemes = resources.getStringArray(R.array.color_schemes)
        val adapter = ArrayAdapter(this, R.layout.spinner_item, colorSchemes)
        adapter.setDropDownViewResource(R.layout.spinner_dropdown_item)
        colorSpinner.adapter = adapter
        
        colorSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                val selectedScheme = colorSchemes[position]
                val color = when (selectedScheme) {
                    "Neon Green" -> ContextCompat.getColor(this@DashboardCustomizerActivity, R.color.neon_green)
                    "Cyber Blue" -> ContextCompat.getColor(this@DashboardCustomizerActivity, R.color.neon_blue)
                    "Hacker Purple" -> ContextCompat.getColor(this@DashboardCustomizerActivity, R.color.neon_purple)
                    "Warning Orange" -> ContextCompat.getColor(this@DashboardCustomizerActivity, R.color.neon_orange)
                    "Critical Red" -> ContextCompat.getColor(this@DashboardCustomizerActivity, R.color.neon_red)
                    else -> ContextCompat.getColor(this@DashboardCustomizerActivity, R.color.neon_green)
                }
                
                // Update CyberPanel color
                cyberPanel.setNeonColor(color)
                
                // Update HUD settings
                hudSettings.neonColor = color
                
                // Provide haptic feedback
                provideHapticFeedback()
            }
            
            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
    }
    
    private fun setupSoundProfileSpinner() {
        val soundProfiles = resources.getStringArray(R.array.sound_profiles)
        val adapter = ArrayAdapter(this, R.layout.spinner_item, soundProfiles)
        adapter.setDropDownViewResource(R.layout.spinner_dropdown_item)
        soundProfileSpinner.adapter = adapter
        
        soundProfileSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                val selectedProfile = soundProfiles[position]
                val profile = when (selectedProfile) {
                    "Tesla Cybertruck" -> EngineSoundProfile.TESLA_CYBERTRUCK
                    "Cyber Charger" -> EngineSoundProfile.CYBER_CHARGER
                    "Quantum Drive" -> EngineSoundProfile.QUANTUM_DRIVE
                    "Neural Network" -> EngineSoundProfile.NEURAL_NETWORK
                    "Stealth Mode" -> EngineSoundProfile.STEALTH_MODE
                    else -> EngineSoundProfile.TESLA_CYBERTRUCK
                }
                
                // Update CyberPanel sound profile
                cyberPanel.setEngineSoundProfile(profile)
                
                // Update HUD settings
                hudSettings.soundProfile = profile
                
                // Provide haptic feedback
                provideHapticFeedback()
            }
            
            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
    }
    
    private fun setupOpacitySeekBar() {
        opacitySeekBar.max = 100
        
        opacitySeekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                val opacity = progress / 100f
                opacityValueText.text = "${progress}%"
                
                // Update CyberPanel opacity
                cyberPanel.alpha = opacity
                
                // Update HUD settings
                hudSettings.opacity = opacity
            }
            
            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            
            override fun onStopTrackingTouch(seekBar: SeekBar?) {
                // Provide haptic feedback when user stops adjusting
                provideHapticFeedback()
            }
        })
    }
    
    private fun setupGridDensitySeekBar() {
        gridDensitySeekBar.max = 100
        
        gridDensitySeekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                val density = progress / 100f
                gridDensityValueText.text = "${progress}%"
                
                // Update CyberPanel grid density
                cyberPanel.setGridDensity(density)
                
                // Update HUD settings
                hudSettings.gridDensity = density
            }
            
            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            
            override fun onStopTrackingTouch(seekBar: SeekBar?) {
                // Provide haptic feedback when user stops adjusting
                provideHapticFeedback()
            }
        })
    }
    
    private fun loadCurrentSettings() {
        // Set color scheme spinner selection
        val colorPosition = when (hudSettings.neonColor) {
            ContextCompat.getColor(this, R.color.neon_green) -> 0
            ContextCompat.getColor(this, R.color.neon_blue) -> 1
            ContextCompat.getColor(this, R.color.neon_purple) -> 2
            ContextCompat.getColor(this, R.color.neon_orange) -> 3
            ContextCompat.getColor(this, R.color.neon_red) -> 4
            else -> 0
        }
        colorSpinner.setSelection(colorPosition)
        
        // Set sound profile spinner selection
        val soundPosition = when (hudSettings.soundProfile) {
            EngineSoundProfile.TESLA_CYBERTRUCK -> 0
            EngineSoundProfile.CYBER_CHARGER -> 1
            EngineSoundProfile.QUANTUM_DRIVE -> 2
            EngineSoundProfile.NEURAL_NETWORK -> 3
            EngineSoundProfile.STEALTH_MODE -> 4
            else -> 0
        }
        soundProfileSpinner.setSelection(soundPosition)
        
        // Set opacity seekbar
        val opacityProgress = (hudSettings.opacity * 100).toInt()
        opacitySeekBar.progress = opacityProgress
        opacityValueText.text = "${opacityProgress}%"
        
        // Set grid density seekbar
        val densityProgress = (hudSettings.gridDensity * 100).toInt()
        gridDensitySeekBar.progress = densityProgress
        gridDensityValueText.text = "${densityProgress}%"
        
        // Apply settings to CyberPanel
        cyberPanel.setNeonColor(hudSettings.neonColor)
        cyberPanel.setEngineSoundProfile(hudSettings.soundProfile)
        cyberPanel.alpha = hudSettings.opacity
        cyberPanel.setGridDensity(hudSettings.gridDensity)
    }
    
    private fun setupSampleWidgets() {
        // Add sample widgets to demonstrate customization
        val widgets = listOf(
            HUDWidget("Encryption Status", WidgetType.ENCRYPTION_GAUGE, 0.2f, 0.1f),
            HUDWidget("Network Speed", WidgetType.NETWORK_SPEEDOMETER, 0.7f, 0.1f),
            HUDWidget("Binary Display", WidgetType.BINARY_DISPLAY, 0.5f, 0.5f),
            HUDWidget("Message Input", WidgetType.MESSAGE_INPUT, 0.5f, 0.9f)
        )
        
        cyberPanel.setWidgets(widgets)
    }
    
    private fun provideHapticFeedback() {
        if (vibrator.hasVibrator()) {
            // Create a vibration effect for haptic feedback
            val effect = VibrationEffect.createOneShot(
                50, // Vibration duration in milliseconds
                VibrationEffect.DEFAULT_AMPLITUDE
            )
            vibrator.vibrate(effect)
        }
    }
    
    override fun onPause() {
        super.onPause()
        // Save settings when activity is paused
        val app = application as Engine3Application
        app.saveHUDSettings(hudSettings)
    }
}