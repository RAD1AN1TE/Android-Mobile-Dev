
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

        // Keys for savedInstanceState
        private const val KEY_RESULT_MESSAGE = "key_result_message"
        private const val KEY_PASS_COUNT_DISPLAY = "key_pass_count_display"
        private const val KEY_FAIL_COUNT_DISPLAY = "key_fail_count_display"
        private const val KEY_RESULT_TYPE = "key_result_type"
    }

    private var resultType: String? = null
    private var passCountDisplay: Int = 0
    private var failCountDisplay: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_result)

        val resultMessageTextView = findViewById<TextView>(R.id.tvResultMessage)
        val resultCountsTextView = findViewById<TextView>(R.id.tvResultCounts)
        val shareButton = findViewById<Button>(R.id.btnShareResult)

        val sharedPref = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

        if (savedInstanceState != null) {
            // Restore transient states
            val restoredResultMessage = savedInstanceState.getString(KEY_RESULT_MESSAGE, "Results not available")
            passCountDisplay = savedInstanceState.getInt(KEY_PASS_COUNT_DISPLAY, sharedPref.getInt(PASS_COUNT_KEY, 0))
            failCountDisplay = savedInstanceState.getInt(KEY_FAIL_COUNT_DISPLAY, sharedPref.getInt(FAIL_COUNT_KEY, 0))
            resultType = savedInstanceState.getString(KEY_RESULT_TYPE, null)

            resultMessageTextView.text = restoredResultMessage
            resultCountsTextView.text = "Passed: $passCountDisplay times\nFailed: $failCountDisplay times"
        } else {
            // Handle intent result and update UI accordingly
            val result = intent.getStringExtra("result")
            resultType = result

            if (result == "pass") {
                val passCount = sharedPref.getInt(PASS_COUNT_KEY, 0) + 1
                with(sharedPref.edit()) {
                    putInt(PASS_COUNT_KEY, passCount)
                    apply()
                }
                resultMessageTextView.text = "Passed"
                passCountDisplay = passCount
            } else if (result == "fail") {
                val failCount = sharedPref.getInt(FAIL_COUNT_KEY, 0) + 1
                with(sharedPref.edit()) {
                    putInt(FAIL_COUNT_KEY, failCount)
                    apply()
                }
                resultMessageTextView.text = "Failed"
                failCountDisplay = failCount
            } else {
                resultMessageTextView.text = "Results not available"
                passCountDisplay = sharedPref.getInt(PASS_COUNT_KEY, 0)
                failCountDisplay = sharedPref.getInt(FAIL_COUNT_KEY, 0)
            }

            val passCount = sharedPref.getInt(PASS_COUNT_KEY, 0)
            val failCount = sharedPref.getInt(FAIL_COUNT_KEY, 0)
            resultCountsTextView.text = "Passed: $passCount times\nFailed: $failCount times"
        }

        shareButton.setOnClickListener {
            shareQuizResults(passCountDisplay, failCountDisplay)
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        // Save transient UI states
        val resultMessage = findViewById<TextView>(R.id.tvResultMessage).text.toString()
        val resultCounts = findViewById<TextView>(R.id.tvResultCounts).text.toString()

        outState.putString(KEY_RESULT_MESSAGE, resultMessage)
        outState.putInt(KEY_PASS_COUNT_DISPLAY, passCountDisplay)
        outState.putInt(KEY_FAIL_COUNT_DISPLAY, failCountDisplay)
        outState.putString(KEY_RESULT_TYPE, resultType)
    }

    private fun shareQuizResults(passCount: Int, failCount: Int) {
        val message = "Quiz Results:\nPassed: $passCount times\nFailed: $failCount times"

        // Create ACTION_SEND intent
        val sendIntent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TEXT, message)
            type = "text/plain"
        }

        // Create chooser
        val chooser = Intent.createChooser(sendIntent, "Share your quiz results via")

        // Verify that there are apps to handle the intent
        if (sendIntent.resolveActivity(packageManager) != null) {
            startActivity(chooser)
        } else {
            Toast.makeText(this, "No app available to share the results.", Toast.LENGTH_SHORT).show()
        }
    }
}