package com.example.uangin

import android.os.Bundle
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Handler

class MainActivity : AppCompatActivity() {

    private val SPLASH_TIME_OUT: Long = 1000 // Durasi splash screen dalam milidetik (3 detik)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        Handler().postDelayed({
            startActivity(Intent(this, HomeActivity::class.java))
            finish()
        }, SPLASH_TIME_OUT)
    }


}