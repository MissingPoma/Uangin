package com.example.uangin

import android.os.Bundle
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Handler

class SplashActivity : AppCompatActivity() {

    private val SPLASH_TIME_OUT: Long = 1000 // Durasi splash screen dalam milidetik (3 detik)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        Handler().postDelayed({
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }, SPLASH_TIME_OUT)
    }


}