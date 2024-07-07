package com.example.uangin

import android.app.DatePickerDialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.content.Intent
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import androidx.room.Room
import com.example.uangin.HomeActivity
import com.example.uangin.R
import com.example.uangin.SettingActivity
import com.example.uangin.database.AppDatabase
import com.example.uangin.database.dao.KategoriDao
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.*

class AddActivity : AppCompatActivity() {

    private lateinit var db: AppDatabase
    private lateinit var categoryDao: KategoriDao
    private lateinit var tanggalTextView: TextView
    private lateinit var autoCompleteTextView: AutoCompleteTextView
    private lateinit var catatanEditText: TextInputEditText
    private lateinit var jumlahEditText: TextInputEditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_adding_data)

        val leftArrowButton = findViewById<ImageButton>(R.id.leftArrow)
        leftArrowButton.setOnClickListener {
            val intent = Intent(this, HomeActivity::class.java)
            startActivity(intent)
        }

        tanggalTextView = findViewById(R.id.tanggalTextView)
        val calendarButton: ImageButton = findViewById(R.id.calendarButton)

        calendarButton.setOnClickListener {
            showDatePickerDialog()
        }

        catatanEditText = findViewById(R.id.catatanEditText)
        jumlahEditText = findViewById(R.id.jumlahEditText)

        db = Room.databaseBuilder(applicationContext, AppDatabase::class.java, "app-database").build()
        categoryDao = db.kategoriDao()

        val textInputLayout: TextInputLayout = findViewById(R.id.pilihKategori)
        autoCompleteTextView = findViewById(R.id.itemKategori)

        lifecycleScope.launch {
            val categories = withContext(Dispatchers.IO) {
                val dbCategories = categoryDao.getAll()
                dbCategories.map { it.namaKategori } + "Add Category"
            }

            val adapter = ArrayAdapter(this@AddActivity, android.R.layout.simple_dropdown_item_1line, categories)
            autoCompleteTextView.setAdapter(adapter)

            autoCompleteTextView.setOnItemClickListener { parent, view, position, id ->
                if (position == categories.size - 1) {
                    navigateToAddCategoryFragment()
                } else {
                    val selectedCategory = categories[position]
                    Toast.makeText(applicationContext, "Selected: $selectedCategory", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun navigateToAddCategoryFragment() {
        val intent = Intent(this, SettingActivity::class.java)
        startActivity(intent)
    }

    private fun showDatePickerDialog() {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog = DatePickerDialog(
            this,
            { _, year1, monthOfYear, dayOfMonth1 ->
                val selectedDate = "$dayOfMonth1 ${getMonthName(monthOfYear)} $year1"
                tanggalTextView.text = selectedDate
            },
            year, month, dayOfMonth
        )

        datePickerDialog.show()
    }

    private fun getMonthName(month: Int): String {
        val monthNames = arrayOf(
            "Januari", "Februari", "Maret", "April", "Mei", "Juni",
            "Juli", "Agustus", "September", "Oktober", "November", "Desember"
        )
        return monthNames[month]
    }
}
