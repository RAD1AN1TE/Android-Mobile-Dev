package com.example.assignment_01

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class ResultActivity : AppCompatActivity() {

    // Keys for SharedPreferences
    companion object {
        private const val PREFS_NAME = "QuizResults"
        private const val PASS_COUNT_KEY = "passCount"
        private const val FAIL_COUNT_KEY = "failCount"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_result)

        val resultMessageTextView = findViewById<TextView>(R.id.tvResultMessage)
        val resultCountsTextView = findViewById<TextView>(R.id.tvResultCounts)
        val shareButton = findViewById<Button>(R.id.btnShareResult)

        val result = intent.getStringExtra("result")

        val sharedPref = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

        if (result == "pass") {
            val passCount = sharedPref.getInt(PASS_COUNT_KEY, 0) + 1
            with(sharedPref.edit()) {
                putInt(PASS_COUNT_KEY, passCount)
                apply()
            }
//            Log.d("QuizResults", "Pass count updated to $passCount")
            resultMessageTextView.text = "Passed"
        } else if (result == "fail") {
            val failCount = sharedPref.getInt(FAIL_COUNT_KEY, 0) + 1
            with(sharedPref.edit()) {
                putInt(FAIL_COUNT_KEY, failCount)
                apply()
            }
            resultMessageTextView.text = "Failed"
        } else {
            resultMessageTextView.text = "Results not available"
        }

        val passCount = sharedPref.getInt(PASS_COUNT_KEY, 0)
        val failCount = sharedPref.getInt(FAIL_COUNT_KEY, 0)
        resultCountsTextView.text = "Passed: $passCount times\nFailed: $failCount times"

        shareButton.setOnClickListener {
            shareQuizResults(passCount, failCount)
        }
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
        val chooser = Intent.createChooser(sendIntent, "Share your quiz results via")

        if (sendIntent.resolveActivity(packageManager) != null) {
            startActivity(chooser)
        } else {
            Toast.makeText(this, "No app available to share the results.", Toast.LENGTH_SHORT).show()
        }
    }
}