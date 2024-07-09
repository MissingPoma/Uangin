package com.example.uangin

import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton
import android.widget.RadioButton
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.example.uangin.database.TransactionViewModel
import com.example.uangin.database.entity.Transactions
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import java.text.SimpleDateFormat
import java.util.*

class UpdateActivity : AppCompatActivity() {

    private lateinit var radioButtonIncome: RadioButton
    private lateinit var radioButtonOutcome: RadioButton
    private lateinit var jumlahEditText: TextInputEditText
    private lateinit var catatanEditText: TextInputEditText
    private lateinit var tanggalTextView: TextView
    private lateinit var kategoriTextView: TextInputLayout
    private lateinit var saveButton: Button
    private lateinit var trashIcon: ImageButton
    private lateinit var leftArrow: ImageButton
    private lateinit var transactionViewModel: TransactionViewModel
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
        saveButton = findViewById(R.id.saveButton)
        trashIcon = findViewById(R.id.trashIcon)
        leftArrow = findViewById(R.id.leftArrow)

        transactionViewModel = ViewModelProvider(this).get(TransactionViewModel::class.java)

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
        kategoriTextView.editText?.setText(transactionCategory)

        saveButton.setOnClickListener {
            val isPengeluaran = radioButtonOutcome.isChecked
            val jumlah = jumlahEditText.text.toString().toDoubleOrNull() ?: 0.0
            val catatan = catatanEditText.text.toString()
            val kategori = kategoriTextView.editText?.text.toString()
            val tanggalStr = tanggalTextView.text.toString()

            if (jumlah <= 0) {
                Toast.makeText(this, "Jumlah tidak boleh kosong", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Mengonversi tanggal ke tipe Date
            val tanggal: Date = try {
                inputDateFormat.parse(tanggalStr) ?: Date()
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
    }
}
