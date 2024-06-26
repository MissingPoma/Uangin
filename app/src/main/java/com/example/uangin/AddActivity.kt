package com.example.uangin

import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.example.uangin.database.AppDatabase
import com.example.uangin.database.entity.Pemasukan
import com.example.uangin.database.entity.Pengeluaran
import com.example.uangin.repository.FinancialRepository
import com.example.uangin.viewmodel.FinancialViewModel
import com.example.uangin.viewmodel.FinancialViewModelFactory
import java.util.*

class AddActivity : AppCompatActivity() {

    private lateinit var financialViewModel: FinancialViewModel
    private lateinit var tanggalTextView: TextView
    private lateinit var autoCompleteTextView: AutoCompleteTextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_adding_data)

        Log.d("AddActivity", "onCreate called")

        try {
            // Setup ViewModel
            val db = AppDatabase.getDatabase(this)
            val repository = FinancialRepository(db.pemasukanDao(), db.pengeluaranDao())
            val viewModelFactory = FinancialViewModelFactory(repository)
            financialViewModel = ViewModelProvider(this, viewModelFactory).get(FinancialViewModel::class.java)
        } catch (e: Exception) {
            Log.e("AddActivity", "Error initializing ViewModel", e)
            Toast.makeText(this, "Error initializing ViewModel", Toast.LENGTH_SHORT).show()
            return
        }

        try {
            val leftArrowButton = findViewById<ImageButton>(R.id.leftArrow)
            leftArrowButton.setOnClickListener {
                val intent = Intent(this, HomeActivity::class.java)
                startActivity(intent)
            }

            val items = listOf("Makanan", "Minuman", "Gaji", "Lain-Lainnya", "Buku")
            val adapter = ArrayAdapter(this, R.layout.dropdown_item, items)
            autoCompleteTextView = findViewById(R.id.itemKategori)
            autoCompleteTextView.setAdapter(adapter)

            tanggalTextView = findViewById(R.id.tanggalTextView)
            val calendarButton: ImageButton = findViewById(R.id.calendarButton)

            calendarButton.setOnClickListener {
                showDatePickerDialog()
            }

            val saveButton: Button = findViewById(R.id.saveButton)
            saveButton.setOnClickListener {
                saveDataToDatabase()
            }
        } catch (e: Exception) {
            Log.e("AddActivity", "Error setting up UI components", e)
            Toast.makeText(this, "Error setting up UI components", Toast.LENGTH_SHORT).show()
        }
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

    private fun saveDataToDatabase() {
        try {
            val radioGroupType = findViewById<RadioGroup>(R.id.radioGroupType)
            val selectedType = findViewById<RadioButton>(radioGroupType.checkedRadioButtonId)?.text.toString()
            val jumlahEditText = findViewById<EditText>(R.id.jumlahEditText).text.toString().toDoubleOrNull()
            val catatanEditText = findViewById<EditText>(R.id.catatanEditText).text.toString()
            val date = tanggalTextView.text.toString()
            val category = autoCompleteTextView.text.toString()

            if (jumlahEditText != null && catatanEditText.isNotEmpty() && date.isNotEmpty() && category.isNotEmpty()) {
                if (selectedType == "Pemasukan") {
                    val pemasukan = Pemasukan(category = category, amount = jumlahEditText, note = catatanEditText, date = date)
                    financialViewModel.insertPemasukan(pemasukan)
                } else {
                    val pengeluaran = Pengeluaran(category = category, amount = jumlahEditText, note = catatanEditText, date = date)
                    financialViewModel.insertPengeluaran(pengeluaran)
                }
                Toast.makeText(this, "Data saved successfully", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show()
            }
        } catch (e: Exception) {
            Log.e("AddActivity", "Error saving data to database", e)
            Toast.makeText(this, "Error saving data to database", Toast.LENGTH_SHORT).show()
        }
    }
}
