package com.example.assignment_01

import android.app.Activity
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Toast
import com.example.assignment_01.databinding.ActivityConfigurationBinding

class ConfigurationActivity : Activity() {
    private lateinit var binding: ActivityConfigurationBinding
    private lateinit var sharedPrefs: SharedPreferences


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityConfigurationBinding.inflate(layoutInflater)
        setContentView(binding.root)

        sharedPrefs = getSharedPreferences("AppSettings", MODE_PRIVATE)
        loadConfigurations()

        binding.saveSettingsButton.setOnClickListener {
            saveConfigurations()
        }

    }

    private fun loadConfigurations() {
        val timerAEnabled = sharedPrefs.getBoolean("timerAEnabled", true)
        val timerBEnabled = sharedPrefs.getBoolean("timerBEnabled", true)
        val timeLimitSeconds = sharedPrefs.getInt("timeLimitSeconds", 0)
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
        finish()
    }

}