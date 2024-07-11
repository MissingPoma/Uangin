package com.example.uangin

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.uangin.databinding.ActivitySplashBinding

class SplashActivity : AppCompatActivity() {

    private val SPLASH_TIME_OUT: Long = 1000 // Durasi splash screen dalam milidetik (1 detik)
    private lateinit var binding: ActivitySplashBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Cek apakah PIN diaktifkan
        if (isPinEnabled()) {
            // Tampilkan dialog PIN
            showPinDialog()
        } else {
            // Lanjutkan ke MainActivity setelah splash time out
            Handler().postDelayed({
                startActivity(Intent(this, MainActivity::class.java))
                finish()
            }, SPLASH_TIME_OUT)
        }
    }

    private fun isPinEnabled(): Boolean {
        val sharedPreferences = getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
        return sharedPreferences.contains("app_pin")
    }

    private fun showPinDialog() {
        val inflater = LayoutInflater.from(this)
        val dialogView = inflater.inflate(R.layout.dialog_pin, null)
        val pinEditText = dialogView.findViewById<EditText>(R.id.pinEditText)

        val dialog = AlertDialog.Builder(this)
            .setTitle("Masukkan PIN")
            .setView(dialogView)
            .setCancelable(false)
            .setPositiveButton("OK") { _, _ ->
                val inputPin = pinEditText.text.toString()
                if (verifyPin(inputPin)) {
                    // Lanjutkan ke MainActivity setelah PIN benar
                    startActivity(Intent(this, MainActivity::class.java))
                    finish()
                } else {
                    // Jika PIN salah, tampilkan kembali dialog PIN
                    showPinDialog()
                }
            }
            .setNegativeButton("Batal") { _, _ ->
                finish()
            }
            .create()

        dialog.show()
    }

    private fun verifyPin(inputPin: String): Boolean {
        val sharedPreferences = getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
        val savedPin = sharedPreferences.getString("app_pin", "")
        return inputPin == savedPin
    }
}
