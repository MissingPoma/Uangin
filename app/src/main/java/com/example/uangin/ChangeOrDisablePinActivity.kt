package com.example.uangin // Sesuaikan dengan package Anda

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.uangin.R

class ChangeOrDisablePinActivity : AppCompatActivity() {

    private lateinit var oldPinEditText: EditText
    private lateinit var newPinEditText: EditText
    private lateinit var confirmPinEditText: EditText
    private lateinit var saveButton: Button
    private lateinit var disableButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_change_or_disable_pin)

        oldPinEditText = findViewById(R.id.oldPinEditText)
        newPinEditText = findViewById(R.id.newPinEditText)
        confirmPinEditText = findViewById(R.id.confirmPinEditText)
        saveButton = findViewById(R.id.saveButton)
        disableButton = findViewById(R.id.disableButton)

        saveButton.setOnClickListener {
            changePin()
        }

        disableButton.setOnClickListener {
            disablePin()
        }
    }

    private fun changePin() {
        val oldPin = oldPinEditText.text.toString()
        val newPin = newPinEditText.text.toString()
        val confirmPin = confirmPinEditText.text.toString()

        if (isPinCorrect(oldPin)) {
            if (isValidPin(newPin)) {
                if (newPin == confirmPin) {
                    savePin(newPin)
                    Toast.makeText(this, "PIN berhasil diubah", Toast.LENGTH_SHORT).show()
                    finish()
                } else {
                    Toast.makeText(this, "PIN baru dan konfirmasi PIN tidak cocok", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(this, "PIN baru tidak valid. Minimal 4 digit.", Toast.LENGTH_SHORT).show()
            }
        } else {
            Toast.makeText(this, "PIN lama salah", Toast.LENGTH_SHORT).show()
        }
    }

    private fun disablePin() {
        val sharedPreferences = getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
        sharedPreferences.edit().remove("app_pin").apply()
        Toast.makeText(this, "PIN dinonaktifkan", Toast.LENGTH_SHORT).show()
        finish()
    }

    private fun savePin(pin: String) {
        val sharedPreferences = getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
        sharedPreferences.edit().putString("app_pin", pin).apply()
    }

    private fun isPinCorrect(enteredPin: String): Boolean {
        val sharedPreferences = getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
        val savedPin = sharedPreferences.getString("app_pin", null)
        return enteredPin == savedPin
    }

    private fun isValidPin(pin: String): Boolean {
        return pin.length >= 4 && pin.all { it.isDigit() } // Minimal 4 digit dan semua karakter adalah angka
    }
}
