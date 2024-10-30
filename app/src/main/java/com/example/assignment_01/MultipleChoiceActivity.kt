package com.example.assignment_01

import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity


class MultipleChoiceActivity : AppCompatActivity() {

    private lateinit var spOptions: Spinner
    private lateinit var btnSubmit: Button
    private var attemptsLeft: Int = 0
    private val correctAnswer = "Irvine"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_multiple_choice)

        spOptions = findViewById(R.id.spOptions)
        btnSubmit = findViewById(R.id.btnSubmit)

        val numOptions = resources.getStringArray(R.array.locations).size
        attemptsLeft = numOptions - 2

        btnSubmit.setOnClickListener {
            checkAnswer()
        }
    }

    private fun checkAnswer() {
        val selectedAnswer = spOptions.selectedItem.toString()

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
}