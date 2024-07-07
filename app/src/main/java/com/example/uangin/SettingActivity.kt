package com.example.uangin

import android.app.ActivityOptions
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import android.content.Intent
import android.widget.ImageButton
import com.google.android.material.floatingactionbutton.FloatingActionButton
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.bottomnavigation.BottomNavigationView


class SettingActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        WindowCompat.setDecorFitsSystemWindows(window, false)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_setting)

        val bottomNavigationView: BottomNavigationView = findViewById(R.id.bottomNavigationView)
        bottomNavigationView.selectedItemId = R.id.settingIcon

        bottomNavigationView.setOnItemSelectedListener { item ->
            val intent = when (item.itemId) {
                R.id.homeIcon -> Intent(this, MainActivity::class.java)
                R.id.searchIcon -> Intent(this, SearchActivity::class.java)
                R.id.chartIcon -> Intent(this, ChartActivity::class.java)
                else -> return@setOnItemSelectedListener true // Already on SettingsActivity
            }

            startActivity(intent)
            finish()
            true
        }
    }
}