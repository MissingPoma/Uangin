package com.example.uangin

import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.room.Room
import com.example.uangin.database.AppDatabase
import com.example.uangin.database.dao.KategoriDao
import com.example.uangin.database.dao.PemasukanDao
import com.example.uangin.database.dao.PengeluaranDao
import com.example.uangin.database.entity.Kategori
import com.example.uangin.database.entity.Pemasukan
import com.example.uangin.database.entity.Pengeluaran
import com.google.android.material.textfield.TextInputEditText
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.*

class AddActivity : AppCompatActivity() {

    private lateinit var db: AppDatabase
    private lateinit var categoryDao: KategoriDao
    private lateinit var pemasukanDao: PemasukanDao
    private lateinit var pengeluaranDao: PengeluaranDao
    private lateinit var tanggalTextView: TextView
    private lateinit var autoCompleteTextView: AutoCompleteTextView
    private lateinit var catatanEditText: TextInputEditText
    private lateinit var jumlahEditText: TextInputEditText
    private lateinit var radioGroup: RadioGroup
    private lateinit var radioPemasukan: RadioButton
    private lateinit var radioPengeluaran: RadioButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_adding_data)

        // Toolbar and Back Button
        val leftArrowButton = findViewById<ImageButton>(R.id.leftArrow)
        leftArrowButton.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }

        // Initialize Views
        tanggalTextView = findViewById(R.id.tanggalTextView)
        val calendarButton: ImageButton = findViewById(R.id.calendarButton)
        catatanEditText = findViewById(R.id.catatanEditText)
        jumlahEditText = findViewById(R.id.jumlahEditText)
        radioGroup = findViewById(R.id.radioGroupType)
        radioPemasukan = findViewById(R.id.radioButtonIncome)
        radioPengeluaran = findViewById(R.id.radioButtonOutcome)

        // Set default date
        val currentDate = Calendar.getInstance()
        val dayOfMonth = currentDate.get(Calendar.DAY_OF_MONTH)
        val monthOfYear = currentDate.get(Calendar.MONTH)
        val year = currentDate.get(Calendar.YEAR)
        tanggalTextView.text = "$dayOfMonth ${getMonthName(monthOfYear)} $year"

        // Calendar Button Listener
        calendarButton.setOnClickListener {
            showDatePickerDialog()
        }

        // Database Initialization
        db = AppDatabase.getDatabase(applicationContext)
        categoryDao = db.kategoriDao()
        pemasukanDao = db.pemasukanDao()
        pengeluaranDao = db.pengeluaranDao()

        autoCompleteTextView = findViewById(R.id.itemKategori)

        // Populate Categories in AutoCompleteTextView
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
                    Toast.makeText(applicationContext, "$selectedCategory", Toast.LENGTH_SHORT).show()
                }
            }
        }

        // Save Button Listener
        val saveButton = findViewById<Button>(R.id.saveButton)
        saveButton.setOnClickListener {
            saveData()
        }
    }

    private fun navigateToAddCategoryFragment() {
        // Buat instance dari AddNewCategoryFragment
        val fragment = AddingNewCategoryFragment()

        // Menggunakan supportFragmentManager untuk memulai transaksi fragment
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment) // Menempatkan fragment di dalam fragment_container
            .addToBackStack(null) // Agar fragment bisa dikembalikan jika diperlukan
            .commit()
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

    private fun saveData() {
        val kategori = autoCompleteTextView.text.toString()
        val jumlah = jumlahEditText.text.toString().toDoubleOrNull()
        val catatan = catatanEditText.text.toString()
        val tanggal = tanggalTextView.text.toString()

        if (kategori.isEmpty() || jumlah == null || catatan.isEmpty() || tanggal.isEmpty()) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show()
            return
        }

        lifecycleScope.launch {
            if (radioPemasukan.isChecked) {
                val pemasukan = Pemasukan(
                    kategori = kategori,
                    jumlah = jumlah,
                    catatan = catatan,
                    tanggal = tanggal
                )
                withContext(Dispatchers.IO) {
                    pemasukanDao.insertAll(pemasukan)
                }
                Toast.makeText(this@AddActivity, "Pemasukan saved successfully", Toast.LENGTH_SHORT).show()
            } else if (radioPengeluaran.isChecked) {
                val pengeluaran = Pengeluaran(
                    kategori = kategori,
                    jumlah = jumlah,
                    catatan = catatan,
                    tanggal = tanggal
                )
                withContext(Dispatchers.IO) {
                    pengeluaranDao.insertAll(pengeluaran)
                }
                Toast.makeText(this@AddActivity, "Pengeluaran saved successfully", Toast.LENGTH_SHORT).show()
            }
        }
    }


}
