package com.example.uangin

import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.room.Room
import com.example.uangin.database.AppDatabase
import com.example.uangin.database.dao.KategoriDao
import com.example.uangin.database.entity.Kategori
import com.google.android.material.textfield.TextInputEditText
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
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }

        tanggalTextView = findViewById(R.id.tanggalTextView)
        val calendarButton: ImageButton = findViewById(R.id.calendarButton)

        calendarButton.setOnClickListener {
            showDatePickerDialog()
        }

        catatanEditText = findViewById(R.id.catatanEditText)
        jumlahEditText = findViewById(R.id.jumlahEditText)


        db = AppDatabase.getDatabase(applicationContext)
        categoryDao = db.kategoriDao()

        autoCompleteTextView = findViewById(R.id.itemKategori)

        lifecycleScope.launch {
            val categories = withContext(Dispatchers.IO) {
                categoryDao.getAll().map { it.namaKategori }
            }

            if (categories.isEmpty()) {
                // Jika tidak ada kategori ditemukan, tambahkan kategori default ke database
                categoryDao.insertAll(
                    Kategori(namaKategori = "Makanan"),
                    Kategori(namaKategori = "Transportasi"),
                    Kategori(namaKategori = "Belanja")
                    // Tambahkan kategori lainnya sesuai kebutuhan
                )
                // Setelah menambahkan kategori default, ambil kembali semua kategori
                val updatedCategories = categoryDao.getAll().map { it.namaKategori }
                val adapter = ArrayAdapter(this@AddActivity, android.R.layout.simple_dropdown_item_1line, updatedCategories)
                autoCompleteTextView.setAdapter(adapter)
            } else {
                val adapter = ArrayAdapter(this@AddActivity, android.R.layout.simple_dropdown_item_1line, categories)
                autoCompleteTextView.setAdapter(adapter)
            }

            val categoriesWithAddOption = categories.toMutableList()
            categoriesWithAddOption.add("Add Category")

            val adapter = ArrayAdapter(this@AddActivity, android.R.layout.simple_dropdown_item_1line, categoriesWithAddOption)
            autoCompleteTextView.setAdapter(adapter)

            autoCompleteTextView.setOnItemClickListener { parent, view, position, id ->
                val selectedCategory = categoriesWithAddOption[position]
                if (selectedCategory == "Add Category") {
                    navigateToAddCategoryFragment()
                } else {
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

