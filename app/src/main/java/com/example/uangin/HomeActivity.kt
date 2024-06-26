package com.example.uangin

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import android.content.Intent
import com.google.android.material.bottomnavigation.BottomNavigationView
import android.widget.ImageButton
import com.google.android.material.floatingactionbutton.FloatingActionButton
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat


class HomeActivity : AppCompatActivity() {

    private lateinit var bottomNavigationView: BottomNavigationView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_home)

        val fab: FloatingActionButton = findViewById(R.id.fab)
        fab.setOnClickListener {
            val intent = Intent(this@HomeActivity, AddActivity::class.java)
            startActivity(intent)
        }
        bottomNavigationView = findViewById(R.id.bottomNavigationView)
        bottomNavigationView.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.homeIcon -> {
                    // Handle click on Home icon
                    true
                }
                R.id.searchIcon -> {
                    // Handle click on Search icon
                    true
                }
                R.id.chartIcon -> {
                    // Handle click on Chart icon
                    true
                }
                R.id.settingIcon -> {
                    // Handle click on Setting icon
                    startActivity(Intent(this@HomeActivity, SettingActivity::class.java))
                    true
                }
                else -> false
            }
        }
    }
}