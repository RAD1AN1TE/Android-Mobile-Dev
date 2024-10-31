package com.example.assignment_01

import android.app.Activity
import android.content.Intent
import android.os.Bundle

class MainActivity : Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val intent = Intent(this, MultipleChoiceActivity::class.java)
        startActivity(intent)
        finish()
    }
}