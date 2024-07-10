package com.example.uangin

import android.app.DatePickerDialog
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.example.uangin.database.AppDatabase
import com.example.uangin.database.TransactionViewModel
import com.example.uangin.database.dao.KategoriDao
import com.example.uangin.database.entity.Kategori
import com.example.uangin.database.entity.Transactions
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.*

class UpdateActivity : AppCompatActivity() {

    private lateinit var radioButtonIncome: RadioButton
    private lateinit var radioButtonOutcome: RadioButton
    private lateinit var jumlahEditText: TextInputEditText
    private lateinit var catatanEditText: TextInputEditText
    private lateinit var tanggalTextView: TextView
    private lateinit var kategoriTextView: TextInputLayout
    private lateinit var autoCompleteTextView: AutoCompleteTextView
    private lateinit var saveButton: Button
    private lateinit var trashIcon: ImageButton
    private lateinit var leftArrow: ImageButton
    private lateinit var calendarButton: ImageButton
    private lateinit var transactionViewModel: TransactionViewModel
    private lateinit var categoryDao: KategoriDao
    private lateinit var db: AppDatabase
    private var transactionId: Int = 0

    private val inputDateFormat = SimpleDateFormat("d MMMM yyyy", Locale("id", "ID"))
    private val outputDateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_update)

        radioButtonIncome = findViewById(R.id.radioButtonIncome)
        radioButtonOutcome = findViewById(R.id.radioButtonOutcome)
        jumlahEditText = findViewById(R.id.jumlahEditText)
        catatanEditText = findViewById(R.id.catatanEditText)
        tanggalTextView = findViewById(R.id.tanggalTextView)
        kategoriTextView = findViewById(R.id.pilihKategori)
        autoCompleteTextView = findViewById(R.id.itemKategori)
        saveButton = findViewById(R.id.saveButton)
        trashIcon = findViewById(R.id.trashIcon)
        leftArrow = findViewById(R.id.leftArrow)
        calendarButton = findViewById(R.id.calendarButton)

        transactionViewModel = ViewModelProvider(this).get(TransactionViewModel::class.java)

        db = AppDatabase.getDatabase(applicationContext)
        categoryDao = db.kategoriDao()

        val transactionType = intent.getBooleanExtra("IS_PENGELUARAN", true)
        val transactionAmount = intent.getDoubleExtra("TRANSACTION_AMOUNT", 0.0)
        val transactionCategory = intent.getStringExtra("TRANSACTION_CATEGORY")
        val transactionNote = intent.getStringExtra("TRANSACTION_NOTE")
        val transactionDateStr = intent.getStringExtra("TRANSACTION_DATE")
        transactionId = intent.getIntExtra("TRANSACTION_ID", 0)

        radioButtonIncome.isChecked = !transactionType
        radioButtonOutcome.isChecked = transactionType
        jumlahEditText.setText(transactionAmount.toString())
        catatanEditText.setText(transactionNote)
        tanggalTextView.text = transactionDateStr
        updateCategoryDropdown(transactionCategory) // Panggil fungsi untuk mengisi kategori dan set kategori terpilih

        saveButton.setOnClickListener {
            val isPengeluaran = radioButtonOutcome.isChecked
            val jumlah = jumlahEditText.text.toString().toDoubleOrNull() ?: 0.0
            val catatan = catatanEditText.text.toString()
            val kategori = autoCompleteTextView.text.toString()
            val tanggalStr = tanggalTextView.text.toString()

            if (jumlah <= 0) {
                Toast.makeText(this, "Jumlah tidak boleh kosong", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Mengonversi tanggal ke tipe Date
            val tanggal: Date = try {
                outputDateFormat.parse(tanggalStr) ?: Date()
            } catch (e: Exception) {
                e.printStackTrace()
                Date() // Gunakan tanggal sekarang jika ada kesalahan
            }

            // Gunakan Date untuk menyimpan tanggal di Transactions
            val transaction = Transactions(
                transactionId,
                tanggal,
                kategori,
                catatan,
                jumlah,
                isPengeluaran
            )

            transactionViewModel.update(transaction)
            finish()
        }

        trashIcon.setOnClickListener {
            transactionViewModel.delete(transactionId, transactionType)
            finish()
        }

        leftArrow.setOnClickListener {
            finish()
        }

        calendarButton.setOnClickListener {
            showDatePickerDialog()
        }
    }

    private fun updateCategoryDropdown(selectedCategory: String?) {
        lifecycleScope.launch {
            val categories = withContext(Dispatchers.IO) {
                categoryDao.getAll().map { it.namaKategori }
            }

            if (categories.isEmpty()) {
                // Jika tidak ada kategori ditemukan, tambahkan kategori default ke database
                withContext(Dispatchers.IO) {
                    categoryDao.insertAll(
                        Kategori(namaKategori = "Makanan"),
                        Kategori(namaKategori = "Transportasi"),
                        Kategori(namaKategori = "Belanja")
                        // Tambahkan kategori lainnya sesuai kebutuhan
                    )
                }

                // Setelah menambahkan kategori default, ambil kembali semua kategori
                val updatedCategories = withContext(Dispatchers.IO) {
                    categoryDao.getAll().map { it.namaKategori }
                }
                val categoriesWithAddOption = updatedCategories.toMutableList()
                categoriesWithAddOption.add("Add Category")

                val adapter = ArrayAdapter(this@UpdateActivity, android.R.layout.simple_dropdown_item_1line, categoriesWithAddOption)
                autoCompleteTextView.setAdapter(adapter)
            } else {
                val categoriesWithAddOption = categories.toMutableList()
                categoriesWithAddOption.add("Add Category")

                val adapter = ArrayAdapter(this@UpdateActivity, android.R.layout.simple_dropdown_item_1line, categoriesWithAddOption)
                autoCompleteTextView.setAdapter(adapter)
                autoCompleteTextView.setText(selectedCategory, false) // Set kategori terpilih
            }
        }

        autoCompleteTextView.setOnItemClickListener { parent, view, position, id ->
            val selectedCategory = parent.getItemAtPosition(position) as String
            if (selectedCategory == "Add Category") {
                navigateToAddCategoryFragment()
            } else {
                Toast.makeText(applicationContext, "Selected: $selectedCategory", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun navigateToAddCategoryFragment() {
        val fragment = AddingNewCategoryFragment()

        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .addToBackStack(null)
            .commit()
    }

    private fun showDatePickerDialog() {
        val calendar = Calendar.getInstance()
        val dateSetListener = DatePickerDialog.OnDateSetListener { _, year, month, dayOfMonth ->
            calendar.set(Calendar.YEAR, year)
            calendar.set(Calendar.MONTH, month)
            calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)
            val selectedDate = calendar.time
            tanggalTextView.text = outputDateFormat.format(selectedDate)
        }

        DatePickerDialog(
            this,
            dateSetListener,
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        ).show()
    }
}
