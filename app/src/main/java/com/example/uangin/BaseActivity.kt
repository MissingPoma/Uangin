package com.example.uangin // Sesuaikan package

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity

open class BaseActivity : AppCompatActivity() { // Open class agar bisa di-extend

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (isPinEnabled()) { // Cek apakah PIN diaktifkan
            showPinDialog()
        }
    }

    private fun isPinEnabled(): Boolean {
        val sharedPreferences = getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
        return sharedPreferences.contains("app_pin")
    }

    private fun showPinDialog() {
        val builder = AlertDialog.Builder(this)
        val view = layoutInflater.inflate(R.layout.dialog_pin, null) // Buat layout untuk dialog PIN
        val pinEditText = view.findViewById<EditText>(R.id.pinEditText)

        builder.setView(view)
            .setTitle("Masukkan PIN")
            .setPositiveButton("OK") { dialog, _ ->
                val enteredPin = pinEditText.text.toString()
                if (isPinCorrect(enteredPin)) {
                    dialog.dismiss()
                } else {
                    Toast.makeText(this, "PIN salah", Toast.LENGTH_SHORT).show()
                    showPinDialog() // Tampilkan dialog lagi jika PIN salah
                }
            }
            .setCancelable(false) // Tidak bisa ditutup dengan tombol back
            .show()
    }

    private fun isPinCorrect(enteredPin: String): Boolean {
        val sharedPreferences = getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
        val savedPin = sharedPreferences.getString("app_pin", null)
        return enteredPin == savedPin
    }
}
