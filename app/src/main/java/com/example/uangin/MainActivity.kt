package com.example.uangin

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import android.content.Intent
import android.app.ActivityOptions
import androidx.core.view.WindowCompat
import com.google.android.material.bottomnavigation.BottomNavigationView
import android.widget.ImageButton
import com.google.android.material.floatingactionbutton.FloatingActionButton
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat


class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        WindowCompat.setDecorFitsSystemWindows(window, false)
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        val fab: FloatingActionButton = findViewById(R.id.fab)
        fab.setOnClickListener {
            val intent = Intent(this@MainActivity, AddActivity::class.java)
            startActivity(intent)
        }
        val bottomNavigationView: BottomNavigationView = findViewById(R.id.bottomNavigationView)
        bottomNavigationView.selectedItemId = R.id.homeIcon
        bottomNavigationView.setOnItemSelectedListener { item ->
            val intent = when (item.itemId) {
                R.id.settingIcon -> Intent(this, SettingActivity::class.java)
                R.id.searchIcon -> Intent(this, SearchActivity::class.java)
                R.id.chartIcon -> Intent(this, ChartActivity::class.java)
                else -> return@setOnItemSelectedListener true // Already on HomeActivity
            }

            startActivity(intent)
            finish()
            true
        }
    }
}