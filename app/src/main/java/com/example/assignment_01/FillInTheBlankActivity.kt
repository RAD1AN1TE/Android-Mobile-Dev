package com.example.assignment_01

import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity

class FillInTheBlankActivity : AppCompatActivity() {

    private var attemptsLeft = 2
    private val correctAnswer = "50"
    private lateinit var etAnswerFIB: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_fib)

        etAnswerFIB = findViewById(R.id.textAnswer)
        val btnSubmit = findViewById<Button>(R.id.btnSubmit)

        btnSubmit.setOnClickListener {
            checkAnswer()
        }
    }

    private fun checkAnswer() {
        val userAnswer = etAnswerFIB.text.toString().trim()

        if (userAnswer.equals(correctAnswer, ignoreCase = true)) {
            val intent = Intent(this, ResultActivity::class.java)
            intent.putExtra("result", "pass")
            startActivity(intent)
            finish()
        } else
            attemptsLeft--
            if (attemptsLeft > 0) {
                Toast.makeText(this, "Incorrect Answer! $attemptsLeft Attempts left", Toast.LENGTH_SHORT).show()
            } else {
                val intent = Intent(this, ResultActivity::class.java)
                intent.putExtra("result", "fail")
                startActivity(intent)
                finish()
            }
        }
    }
