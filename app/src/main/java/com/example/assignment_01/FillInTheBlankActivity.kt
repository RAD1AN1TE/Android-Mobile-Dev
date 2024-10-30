package com.example.assignment_01

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.example.assignment_01.databinding.ActivityFibBinding
import java.util.concurrent.TimeUnit

class FillInTheBlankActivity : AppCompatActivity() {

    private var attemptsLeft = 2
    private val correctAnswer = "50"
    private lateinit var binding: ActivityFibBinding

    private lateinit var timerRunnable: Runnable
    private val handler = Handler(Looper.getMainLooper())

    private var activityStartTime: Long = 0
    private var foregroundStartTime: Long = 0
    private var timeInForeground: Long = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFibBinding.inflate(layoutInflater)
        setContentView(binding.root)

        activityStartTime = System.currentTimeMillis()

        timerRunnable = object : Runnable {
            override fun run() {
                // Update Foreground Timer
                val currentTime = System.currentTimeMillis()
                val elapsedForeground = timeInForeground + (currentTime - foregroundStartTime)
                binding.timerA.text = formatTime(elapsedForeground)

                // Update Total Timer
                val elapsedTotal = currentTime - activityStartTime
                binding.timerB.text = formatTime(elapsedTotal)

                // next update after a second
                handler.postDelayed(this, 1000)
            }
        }

        binding.btnSubmit.setOnClickListener {
            checkAnswer()
        }
    }

    override fun onResume() {
        super.onResume()
        foregroundStartTime = System.currentTimeMillis()

        handler.post(timerRunnable)
    }

    override fun onPause() {
        super.onPause()
        val currentTime = System.currentTimeMillis()
        timeInForeground += (currentTime - foregroundStartTime)

        handler.removeCallbacks(timerRunnable)
    }

    override fun onDestroy() {
        super.onDestroy()

        handler.removeCallbacks(timerRunnable)
    }

    private fun checkAnswer() {
        val userAnswer = binding.textAnswer.text.toString().trim()

        if (userAnswer.equals(correctAnswer, ignoreCase = true)) {
            val intent = Intent(this, ResultActivity::class.java)
            intent.putExtra("result", "pass")
            startActivity(intent)
            finish()
        } else {
            attemptsLeft--
            if (attemptsLeft > 0) {
                Toast.makeText(
                    this,
                    "Incorrect Answer! $attemptsLeft Attempts left",
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                val intent = Intent(this, ResultActivity::class.java)
                intent.putExtra("result", "fail")
                startActivity(intent)
                finish()
            }
        }
    }

    private fun formatTime(millis: Long): String {
        val minutes = TimeUnit.MILLISECONDS.toMinutes(millis)
        val seconds = TimeUnit.MILLISECONDS.toSeconds(millis) -
                TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millis))
        return String.format("%02d:%02d", minutes, seconds)
    }
}
