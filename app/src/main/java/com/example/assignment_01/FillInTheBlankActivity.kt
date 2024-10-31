package com.example.assignment_01

import android.app.Activity
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.os.CountDownTimer
import android.os.Handler
import android.os.Looper
import android.widget.*
import com.example.assignment_01.databinding.ActivityFibBinding
import java.util.concurrent.TimeUnit

class FillInTheBlankActivity : Activity() {

    private var attemptsLeft = 2
    private val correctAnswer = "50"
    private lateinit var binding: ActivityFibBinding

    private lateinit var timerRunnable: Runnable
    private val handler = Handler(Looper.getMainLooper())

    private var activityStartTime: Long = 0
    private var foregroundStartTime: Long = 0
    private var timeInForeground: Long = 0

    private lateinit var sharedPrefs: SharedPreferences
    private var timerAEnabled = true
    private var timerBEnabled = true
    private var timeLimitSeconds: Int = 0
    private var buttonTypeImage = false

    private var countDownTimer: CountDownTimer? = null
    private var timeLeftInMillis: Long = 0
    private var cameFromSettings = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFibBinding.inflate(layoutInflater)
        setContentView(binding.root)

        sharedPrefs = getSharedPreferences("AppSettings", MODE_PRIVATE)
        loadConfigurations()

        updateTimerVisibility()

        // Initialize timeLeftInMillis
        timeLeftInMillis = timeLimitSeconds * 1000L
        activityStartTime = System.currentTimeMillis()

        val settingsButton: Button = findViewById(R.id.settings_button)
        settingsButton.setOnClickListener {
            val intent = Intent(this, ConfigurationActivity::class.java)
            startActivity(intent)
            cameFromSettings = true  // Flag to reset timer when returning
        }

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

                // Schedule next update after a second
                handler.postDelayed(this, 1000)
            }
        }

        // Start the timer
        startTimer()

        binding.btnSubmit.setOnClickListener {
            checkAnswer()
        }
    }

    override fun onResume() {
        super.onResume()
        foregroundStartTime = System.currentTimeMillis()

        handler.post(timerRunnable)

        loadConfigurations()
        updateTimerVisibility()

        if (cameFromSettings) {
            // Reset timer because we came from settings
            resetTimer()
            cameFromSettings = false
        } else {
            // Resume timer without resetting
            startTimer()
        }
    }

    override fun onPause() {
        super.onPause()
        val currentTime = System.currentTimeMillis()
        timeInForeground += (currentTime - foregroundStartTime)

        handler.removeCallbacks(timerRunnable)

        countDownTimer?.cancel()  // Pause the timer
    }

    override fun onDestroy() {
        super.onDestroy()

        handler.removeCallbacks(timerRunnable)
    }

    private fun startTimer(reset: Boolean = false) {
        if (timeLimitSeconds <= 0) {
            return  // No timer to run if time limit is zero or negative
        }

        countDownTimer?.cancel()  // Cancel any existing timer

        if (reset) {
            timeLeftInMillis = timeLimitSeconds * 1000L  // Reset timeLeftInMillis
        }

        binding.timerRemaining.text = formatTime(timeLeftInMillis)

        countDownTimer = object : CountDownTimer(timeLeftInMillis, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                timeLeftInMillis = millisUntilFinished
                binding.timerRemaining.text = formatTime(timeLeftInMillis)
            }

            override fun onFinish() {
                binding.timerRemaining.text = "00:00"
                Toast.makeText(this@FillInTheBlankActivity, "Time's up!", Toast.LENGTH_SHORT).show()
                navigateToResult(false)
            }
        }.start()
    }

    private fun resetTimer() {
        countDownTimer?.cancel()
        timeLeftInMillis = timeLimitSeconds * 1000L  // Reset timeLeftInMillis
        startTimer(reset = true)
    }

    private fun checkAnswer() {
        val userAnswer = binding.textAnswer.text.toString().trim()

        if (userAnswer.equals(correctAnswer, ignoreCase = true)) {
            countDownTimer?.cancel()
            navigateToResult(true)
        } else {
            attemptsLeft--
            if (attemptsLeft > 0) {
                Toast.makeText(
                    this,
                    "Incorrect Answer! $attemptsLeft Attempts left",
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                countDownTimer?.cancel()
                navigateToResult(false)
            }
        }
    }

    private fun formatTime(millis: Long): String {
        val minutes = TimeUnit.MILLISECONDS.toMinutes(millis)
        val seconds = TimeUnit.MILLISECONDS.toSeconds(millis) -
                TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millis))
        return String.format("%02d:%02d", minutes, seconds)
    }

    private fun loadConfigurations() {
        timerAEnabled = sharedPrefs.getBoolean("timerAEnabled", true)
        timerBEnabled = sharedPrefs.getBoolean("timerBEnabled", true)
        timeLimitSeconds = sharedPrefs.getInt("timeLimitSeconds", 0)
        buttonTypeImage = sharedPrefs.getBoolean("buttonTypeImage", false)
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

    private fun navigateToResult(passed: Boolean) {
        countDownTimer?.cancel()
        val intent = Intent(this, ResultActivity::class.java).apply {
            putExtra("result", if (passed) "pass" else "fail")
        }
        startActivity(intent)
        finish()
    }
}