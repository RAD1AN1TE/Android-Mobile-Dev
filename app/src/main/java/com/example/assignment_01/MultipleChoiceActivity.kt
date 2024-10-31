package com.example.assignment_01

import android.app.Activity
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.os.CountDownTimer
import android.os.Handler
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.Toast
import com.example.assignment_01.databinding.ActivityMultipleChoiceBinding

class MultipleChoiceActivity : Activity() {

    private lateinit var binding: ActivityMultipleChoiceBinding
    private var attempts1 = 0
    private var attempts2 = 0
    private val maxAttempts1 = 2
    private val maxAttempts2 = 2
    private val correctAnswer1 = "Irvine"
    private val correctAnswer2 = "New York"

    private var activityStartTime: Long = 0
    private var foregroundStartTime: Long = 0
    private var timeInForeground: Long = 0
    private lateinit var timerRunnable: Runnable
    private val handler = Handler()

    private var countDownTimer: CountDownTimer? = null
    private var timeLeftInMillis: Long = 0

    // Config Settings
    private lateinit var sharedPrefs: SharedPreferences
    private var timerAEnabled = true
    private var timerBEnabled = true
    private var timeLimitSeconds: Int = 30

    private var cameFromSettings = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMultipleChoiceBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Init settings
        sharedPrefs = getSharedPreferences("AppSettings", MODE_PRIVATE)
        loadConfigurations()

        updateTimerVisibility()

        // Restore saved state if available
        if (savedInstanceState != null) {
            timeLeftInMillis = savedInstanceState.getLong("timeLeftInMillis")
            activityStartTime = savedInstanceState.getLong("activityStartTime")
            timeInForeground = savedInstanceState.getLong("timeInForeground")
            attempts1 = savedInstanceState.getInt("attempts1")
            attempts2 = savedInstanceState.getInt("attempts2")
            cameFromSettings = savedInstanceState.getBoolean("cameFromSettings")
        } else {
            // Initialize timers if no saved state
            timeLeftInMillis = timeLimitSeconds * 1000L
            activityStartTime = System.currentTimeMillis()
        }

        // Settings Button
        binding.settingsButton.setOnClickListener {
            val intent = Intent(this, ConfigurationActivity::class.java)
            startActivity(intent)
            cameFromSettings = true
        }

        // Initialize spinners with options from strings.xml
        val options = resources.getStringArray(R.array.locations)
        val adapter1 = ArrayAdapter(this, android.R.layout.simple_spinner_item, options)
        adapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spOptions1.adapter = adapter1

        val adapter2 = ArrayAdapter(this, android.R.layout.simple_spinner_item, options)
        adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spOptions2.adapter = adapter2

        timerRunnable = object : Runnable {
            override fun run() {
                // Update Foreground Timer
                if (timerAEnabled) {
                    val currentTime = System.currentTimeMillis()
                    val elapsedForeground = timeInForeground + (currentTime - foregroundStartTime)
                    binding.timerA.text = formatTime(elapsedForeground)
                }

                // Update Total Timer
                if (timerBEnabled) {
                    val currentTime = System.currentTimeMillis()
                    val elapsedTotal = currentTime - activityStartTime
                    binding.timerB.text = formatTime(elapsedTotal)
                }

                // next update after a second
                handler.postDelayed(this, 1000)
            }
        }

        // Start the timer
        startTimer()

        // Setup Submit Button
        setupSubmitButton()
    }

    override fun onResume() {
        super.onResume()
        foregroundStartTime = System.currentTimeMillis()

        handler.post(timerRunnable)

        loadConfigurations()
        updateTimerVisibility()

        if (cameFromSettings) {
            resetTimer()
            cameFromSettings = false
        } else {
            startTimer()
        }
    }

    override fun onPause() {
        super.onPause()
        Log.d("MultipleChoiceActivity", "onPause called")
        val currentTime = System.currentTimeMillis()
        timeInForeground += (currentTime - foregroundStartTime)

        handler.removeCallbacks(timerRunnable)

        countDownTimer?.cancel()

    }

    override fun onDestroy() {
        super.onDestroy()

        handler.removeCallbacks(timerRunnable)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putLong("timeLeftInMillis", timeLeftInMillis)
        outState.putLong("activityStartTime", activityStartTime)
        outState.putLong("timeInForeground", timeInForeground)
        outState.putInt("attempts1", attempts1)
        outState.putInt("attempts2", attempts2)
        outState.putBoolean("cameFromSettings", cameFromSettings)
    }

    private fun loadConfigurations() {
        timerAEnabled = sharedPrefs.getBoolean("timerAEnabled", true)
        timerBEnabled = sharedPrefs.getBoolean("timerBEnabled", true)
        timeLimitSeconds = sharedPrefs.getInt("timeLimitSeconds", 30)
    }

    private fun updateTimerVisibility() {
        if (timerAEnabled) {
            binding.timerALabel.visibility = android.view.View.VISIBLE
            binding.timerA.visibility = android.view.View.VISIBLE
        } else {
            binding.timerALabel.visibility = android.view.View.GONE
            binding.timerA.visibility = android.view.View.GONE
        }

        if (timerBEnabled) {
            binding.timerBLabel.visibility = android.view.View.VISIBLE
            binding.timerB.visibility = android.view.View.VISIBLE
        } else {
            binding.timerBLabel.visibility = android.view.View.GONE
            binding.timerB.visibility = android.view.View.GONE
        }
    }

    private fun startTimer(reset: Boolean = false) {
        countDownTimer?.cancel()
        if (reset) {
            timeLeftInMillis = timeLimitSeconds * 1000L
        }
        binding.timerRemaining.text = formatTime(timeLeftInMillis)

        countDownTimer = object : CountDownTimer(timeLeftInMillis, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                timeLeftInMillis = millisUntilFinished
                binding.timerRemaining.text = formatTime(timeLeftInMillis)
            }

            override fun onFinish() {
                binding.timerRemaining.text = "00:00"
                Toast.makeText(this@MultipleChoiceActivity, "Time's up!", Toast.LENGTH_SHORT).show()
                navigateToResult(false)
            }
        }.start()
    }

    private fun resetTimer() {
        countDownTimer?.cancel()
        startTimer(reset = true)
    }

    private fun setupSubmitButton() {
        binding.btnSubmit.setOnClickListener {

            val selected1 = binding.spOptions1.selectedItem.toString()
            val selected2 = binding.spOptions2.selectedItem.toString()

            var allCorrect = true

            if (selected1 != correctAnswer1) {
                attempts1++
                allCorrect = false
                if (attempts1 >= maxAttempts1) {
                    navigateToResult(false)
                    return@setOnClickListener
                } else {
                    Toast.makeText(this, "Question 1 Incorrect. Try again.", Toast.LENGTH_SHORT).show()
                }
            }

            if (selected2 != correctAnswer2) {
                attempts2++
                allCorrect = false
                if (attempts2 >= maxAttempts2) {
                    navigateToResult(false)
                    return@setOnClickListener
                } else {
                    Toast.makeText(this, "Question 2 Incorrect. Try again.", Toast.LENGTH_SHORT).show()
                }
            }

            if (allCorrect) {
                navigateToFillInBlank()
            }
            // Timer continues without resetting
        }
    }

    private fun navigateToFillInBlank() {
        countDownTimer?.cancel()
        val intent = Intent(this, FillInTheBlankActivity::class.java)
        startActivity(intent)
        finish()
    }

    private fun navigateToResult(passed: Boolean) {
        countDownTimer?.cancel()
        val intent = Intent(this, ResultActivity::class.java).apply {
            putExtra("result", if (passed) "pass" else "fail")
        }
        startActivity(intent)
        finish()
    }

    private fun formatTime(millis: Long): String {
        val minutes = (millis / 1000) / 60
        val seconds = (millis / 1000) % 60
        return String.format("%02d:%02d", minutes, seconds)
    }
}