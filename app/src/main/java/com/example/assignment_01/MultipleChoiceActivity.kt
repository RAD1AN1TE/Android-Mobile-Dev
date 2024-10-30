package com.example.assignment_01

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.example.assignment_01.databinding.ActivityMultipleChoiceBinding
import java.util.concurrent.TimeUnit


class MultipleChoiceActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMultipleChoiceBinding

    private val correctAnswer = "Irvine"

    private var foregroundStartTime: Long = 0
    private var timeInForeground: Long = 0

    private var activityStartTime: Long = 0
    private val handler = Handler(Looper.getMainLooper())
    private lateinit var timerRunnable: Runnable

    private var attemptsLeft: Int = 0


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMultipleChoiceBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val options = resources.getStringArray(R.array.locations)
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, options)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spOptions.adapter = adapter

        attemptsLeft = options.size - 2
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
        val selectedAnswer = binding.spOptions.selectedItem.toString()

        if (selectedAnswer == correctAnswer) {
            val intent = Intent(this, FillInTheBlankActivity::class.java)
            startActivity(intent)
            finish()
        } else {
            attemptsLeft--
            if (attemptsLeft > 0) {
                Toast.makeText(this, "Incorrect! Attempts left: $attemptsLeft", Toast.LENGTH_SHORT).show()
            } else {
                // User failed
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