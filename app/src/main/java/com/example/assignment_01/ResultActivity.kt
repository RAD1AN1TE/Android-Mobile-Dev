package com.example.assignment_01

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class ResultActivity : AppCompatActivity() {

    companion object {
        private const val PREFS_NAME = "QuizResults"
        private const val PASS_COUNT_KEY = "passCount"
        private const val FAIL_COUNT_KEY = "failCount"

        // Key for saving instance state
        private const val KEY_RESULT_PROCESSED = "resultProcessed"
    }

    private var isResultProcessed: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_result)

        val resultMessageTextView = findViewById<TextView>(R.id.tvResultMessage)
        val resultCountsTextView = findViewById<TextView>(R.id.tvResultCounts)
        val shareButton = findViewById<Button>(R.id.btnShareResult)
        val restartButton = findViewById<Button>(R.id.btnRestart)

        val result = intent.getStringExtra("result")

        val sharedPref = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

        // Restore the isResultProcessed flag
        if (savedInstanceState != null) {
            isResultProcessed = savedInstanceState.getBoolean(KEY_RESULT_PROCESSED, false)
        }

        // Process the result only if it hasn't been processed yet
        if (!isResultProcessed && result != null) {
            when (result) {
                "pass" -> {
                    val passCount = sharedPref.getInt(PASS_COUNT_KEY, 0) + 1
                    with(sharedPref.edit()) {
                        putInt(PASS_COUNT_KEY, passCount)
                        apply()
                    }
                    resultMessageTextView.text = "Passed"
                }
                "fail" -> {
                    val failCount = sharedPref.getInt(FAIL_COUNT_KEY, 0) + 1
                    with(sharedPref.edit()) {
                        putInt(FAIL_COUNT_KEY, failCount)
                        apply()
                    }
                    resultMessageTextView.text = "Failed"
                }
                else -> {
                    resultMessageTextView.text = "Results not available"
                }
            }
            // Mark the result as processed
            isResultProcessed = true
        } else if (result == null) {
            resultMessageTextView.text = "Results not available"
        }

        // Fetch the updated counts
        val passCount = sharedPref.getInt(PASS_COUNT_KEY, 0)
        val failCount = sharedPref.getInt(FAIL_COUNT_KEY, 0)
        resultCountsTextView.text = "Passed: $passCount times\nFailed: $failCount times"

        shareButton.setOnClickListener {
            shareQuizResults(passCount, failCount)
        }

        restartButton.setOnClickListener {
            restartQuiz()
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        // Save the isResultProcessed flag
        outState.putBoolean(KEY_RESULT_PROCESSED, isResultProcessed)
    }

    private fun restartQuiz() {
        val intent = Intent(this, MultipleChoiceActivity::class.java)
        // Clear the activity stack to prevent back navigation
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent)
        finish()
    }

    private fun shareQuizResults(passCount: Int, failCount: Int) {
        val message = "Quiz Results:\nPassed: $passCount times\nFailed: $failCount times"

        // Create ACTION_SEND intent
        val sendIntent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TEXT, message)
            type = "text/plain"
        }

        // Create ACTION_CHOOSER intent
        val chooserIntent = Intent.createChooser(sendIntent, "Share your quiz results via")

        if (sendIntent.resolveActivity(packageManager) != null) {
            startActivity(chooserIntent)
        } else {
            Toast.makeText(this, "No app available to share the results.", Toast.LENGTH_SHORT).show()
        }
    }
}