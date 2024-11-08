package com.example.assignment_01

import android.app.Activity
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Toast
import com.example.assignment_01.databinding.ActivityConfigurationBinding

class ConfigurationActivity : Activity() {
    private lateinit var binding: ActivityConfigurationBinding
    private lateinit var sharedPrefs: SharedPreferences

    // Keys for saving instance state
    companion object {
        private const val KEY_TIMER_A_ENABLED = "key_timer_a_enabled"
        private const val KEY_TIMER_B_ENABLED = "key_timer_b_enabled"
        private const val KEY_TIME_LIMIT_SECONDS = "key_time_limit_seconds"
        private const val KEY_BUTTON_TYPE_IMAGE = "key_button_type_image"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityConfigurationBinding.inflate(layoutInflater)
        setContentView(binding.root)

        sharedPrefs = getSharedPreferences("AppSettings", MODE_PRIVATE)
        loadConfigurations()

        // Restore UI state from savedInstanceState if available
        if (savedInstanceState != null) {
            binding.timerASwitch.isChecked =
                savedInstanceState.getBoolean(KEY_TIMER_A_ENABLED, binding.timerASwitch.isChecked)
            binding.timerBSwitch.isChecked =
                savedInstanceState.getBoolean(KEY_TIMER_B_ENABLED, binding.timerBSwitch.isChecked)
            binding.timeLimitInput.setText(
                savedInstanceState.getInt(KEY_TIME_LIMIT_SECONDS, 0).let {
                    if (it > 0) it.toString() else ""
                }
            )
            val isButtonTypeImage = savedInstanceState.getBoolean(KEY_BUTTON_TYPE_IMAGE, false)
            if (isButtonTypeImage) {
                binding.imageButtonRadio.isChecked = true
            } else {
                binding.defaultButtonRadio.isChecked = true
            }
        }

        binding.saveSettingsButton.setOnClickListener {
            saveConfigurations()
        }

        binding.backButton.setOnClickListener {
            setResult(RESULT_CANCELED)
            finish()
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        // Save the current UI state
        outState.putBoolean(KEY_TIMER_A_ENABLED, binding.timerASwitch.isChecked)
        outState.putBoolean(KEY_TIMER_B_ENABLED, binding.timerBSwitch.isChecked)
        val timeLimitInput = binding.timeLimitInput.text.toString().trim()
        val timeLimitSeconds = if (timeLimitInput.isNotEmpty()) {
            try {
                timeLimitInput.toInt()
            } catch (e: NumberFormatException) {
                0
            }
        } else {
            0
        }
        outState.putInt(KEY_TIME_LIMIT_SECONDS, timeLimitSeconds)
        outState.putBoolean(KEY_BUTTON_TYPE_IMAGE, binding.imageButtonRadio.isChecked)
    }

    private fun loadConfigurations() {
        val timerAEnabled = sharedPrefs.getBoolean("timerAEnabled", true)
        val timerBEnabled = sharedPrefs.getBoolean("timerBEnabled", true)
        val timeLimitSeconds = sharedPrefs.getInt("timeLimitSeconds", 30) // Default to 30 seconds
        val buttonTypeImage = sharedPrefs.getBoolean("buttonTypeImage", false)

        binding.timerASwitch.isChecked = timerAEnabled
        binding.timerBSwitch.isChecked = timerBEnabled
        binding.timeLimitInput.setText(if (timeLimitSeconds > 0) timeLimitSeconds.toString() else "")

        if (buttonTypeImage) {
            binding.imageButtonRadio.isChecked = true
        } else {
            binding.defaultButtonRadio.isChecked = true
        }
    }

    private fun saveConfigurations() {
        val timerAEnabled = binding.timerASwitch.isChecked
        val timerBEnabled = binding.timerBSwitch.isChecked

        val timeLimitInput = binding.timeLimitInput.text.toString().trim()
        val timeLimitSeconds = if (timeLimitInput.isNotEmpty()) {
            try {
                timeLimitInput.toInt()
            } catch (e: NumberFormatException) {
                0
            }
        } else {
            0
        }

        val buttonTypeImage = binding.imageButtonRadio.isChecked

        with(sharedPrefs.edit()) {
            putBoolean("timerAEnabled", timerAEnabled)
            putBoolean("timerBEnabled", timerBEnabled)
            putInt("timeLimitSeconds", timeLimitSeconds)
            putBoolean("buttonTypeImage", buttonTypeImage)
            apply()
        }

        Toast.makeText(this, "Settings saved successfully.", Toast.LENGTH_SHORT).show()
        setResult(RESULT_OK)
        finish()
    }
}