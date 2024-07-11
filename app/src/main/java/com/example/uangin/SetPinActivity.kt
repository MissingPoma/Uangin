package com.example.uangin // Sesuaikan dengan package Anda

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.uangin.R

class SetPinActivity : AppCompatActivity() {

    private lateinit var pinEditText: EditText
    private lateinit var saveButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_set_pin) // Ganti dengan layout Anda

        pinEditText = findViewById(R.id.pinEditText)
        saveButton = findViewById(R.id.saveButton)

        saveButton.setOnClickListener {
            val pin = pinEditText.text.toString()
            if (isValidPin(pin)) {
                savePin(pin)
                startActivity(Intent(this, ConfirmPinActivity::class.java))
                finish()
            } else {
                Toast.makeText(this, "PIN tidak valid. Minimal 4 digit.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun isValidPin(pin: String): Boolean {
        return pin.length >= 4 // Minimal 4 digit
    }

    private fun savePin(pin: String) {
        val sharedPreferences = getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
        sharedPreferences.edit().putString("app_pin", pin).apply()
    }
}
