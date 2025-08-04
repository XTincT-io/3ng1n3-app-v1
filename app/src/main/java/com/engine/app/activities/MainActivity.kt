package com.engine.app.activities

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.view.animation.AlphaAnimation
import android.view.animation.Animation
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.engine.app.R
import com.engine.app.views.GlitchAnimatedDrawable

class MainActivity : AppCompatActivity() {

    private lateinit var logoImage: ImageView
    private lateinit var titleText: TextView
    private lateinit var subtitleText: TextView
    private lateinit var enterChatButton: Button
    private lateinit var customizeDashboardButton: Button
    private lateinit var mintCNFTButton: Button
    private lateinit var glitchDrawable: GlitchAnimatedDrawable
    private val handler = Handler(Looper.getMainLooper())
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        
        // Initialize views
        logoImage = findViewById(R.id.logo_image)
        titleText = findViewById(R.id.title_text)
        subtitleText = findViewById(R.id.subtitle_text)
        enterChatButton = findViewById(R.id.enter_chat_button)
        customizeDashboardButton = findViewById(R.id.customize_dashboard_button)
        mintCNFTButton = findViewById(R.id.mint_cnft_button)
        
        // Set up glitch animation for the logo
        setupGlitchAnimation()
        
        // Set up button click listeners
        setupButtonListeners()
        
        // Start intro animation sequence
        startIntroAnimation()
    }
    
    private fun setupGlitchAnimation() {
        // Create glitch drawable for the logo
        glitchDrawable = GlitchAnimatedDrawable(this).apply {
            setGlitchIntensity(0.7f)
            setGlitchFrequency(0.3f)
            setScanLines(true)
            setBaseColor(ContextCompat.getColor(this@MainActivity, R.color.neon_green))
        }
        
        // Apply the glitch drawable to the logo
        logoImage.setImageDrawable(glitchDrawable)
        
        // Start the glitch animation
        glitchDrawable.start()
    }
    
    private fun setupButtonListeners() {
        // Enter Chat button
        enterChatButton.setOnClickListener {
            provideButtonFeedback(it)
            startActivity(Intent(this, CyberChatActivity::class.java))
        }
        
        // Customize Dashboard button
        customizeDashboardButton.setOnClickListener {
            provideButtonFeedback(it)
            startActivity(Intent(this, DashboardCustomizerActivity::class.java))
        }
        
        // Mint cNFT button
        mintCNFTButton.setOnClickListener {
            provideButtonFeedback(it)
            startActivity(Intent(this, CNFTMinterActivity::class.java))
        }
    }
    
    private fun startIntroAnimation() {
        // Initially hide all elements
        logoImage.alpha = 0f
        titleText.alpha = 0f
        subtitleText.alpha = 0f
        enterChatButton.alpha = 0f
        customizeDashboardButton.alpha = 0f
        mintCNFTButton.alpha = 0f
        
        // Animate logo first
        logoImage.animate()
            .alpha(1f)
            .setDuration(1000)
            .withEndAction {
                // Then animate title
                titleText.animate()
                    .alpha(1f)
                    .setDuration(800)
                    .withEndAction {
                        // Then animate subtitle with a typing effect
                        animateTypingText(subtitleText, getString(R.string.app_subtitle))
                        
                        // Delay showing buttons
                        handler.postDelayed({
                            // Animate buttons one by one
                            enterChatButton.animate()
                                .alpha(1f)
                                .setDuration(400)
                                .withEndAction {
                                    customizeDashboardButton.animate()
                                        .alpha(1f)
                                        .setDuration(400)
                                        .withEndAction {
                                            mintCNFTButton.animate()
                                                .alpha(1f)
                                                .setDuration(400)
                                                .start()
                                        }
                                        .start()
                                }
                                .start()
                        }, 1500)
                    }
                    .start()
            }
            .start()
    }
    
    private fun animateTypingText(textView: TextView, finalText: String) {
        val duration = 1500L // Total duration for typing animation
        val charCount = finalText.length
        val delayPerChar = duration / charCount
        
        textView.text = ""
        textView.alpha = 1f
        
        for (i in finalText.indices) {
            handler.postDelayed({
                textView.text = finalText.substring(0, i + 1)
                
                // Add a blinking cursor effect at the end
                if (i < charCount - 1) {
                    val blinkAnim = AlphaAnimation(0.0f, 1.0f).apply {
                        duration = 50
                        repeatMode = Animation.REVERSE
                        repeatCount = 1
                    }
                    textView.startAnimation(blinkAnim)
                }
            }, i * delayPerChar)
        }
    }
    
    private fun provideButtonFeedback(view: View) {
        // Visual feedback - quick scale animation
        view.animate()
            .scaleX(0.95f)
            .scaleY(0.95f)
            .setDuration(50)
            .withEndAction {
                view.animate()
                    .scaleX(1f)
                    .scaleY(1f)
                    .setDuration(50)
                    .start()
            }
            .start()
        
        // Sound feedback could be added here
    }
    
    override fun onDestroy() {
        super.onDestroy()
        // Stop the glitch animation to prevent memory leaks
        glitchDrawable.stop()
        // Remove any pending callbacks
        handler.removeCallbacksAndMessages(null)
    }
}