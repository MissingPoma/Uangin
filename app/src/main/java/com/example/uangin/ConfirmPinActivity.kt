package com.example.uangin // Sesuaikan dengan package Anda

import android.content.Context
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.uangin.R

class ConfirmPinActivity : AppCompatActivity() {

    private lateinit var pinEditText: EditText
    private lateinit var confirmButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_confirm_pin) // Ganti dengan layout Anda

        pinEditText = findViewById(R.id.pinEditText)
        confirmButton = findViewById(R.id.confirmButton)

        confirmButton.setOnClickListener {
            val enteredPin = pinEditText.text.toString()
            if (isPinCorrect(enteredPin)) {
                Toast.makeText(this, "PIN berhasil dikonfirmasi", Toast.LENGTH_SHORT).show()
                finish() // Kembali ke SettingFragment
            } else {
                Toast.makeText(this, "PIN salah", Toast.LENGTH_SHORT).show()
                pinEditText.text.clear() // Hapus input PIN jika salah
            }
        }
    }

    private fun isPinCorrect(enteredPin: String): Boolean {
        val sharedPreferences = getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
        val savedPin = sharedPreferences.getString("app_pin", null)
        return enteredPin == savedPin
    }
}
