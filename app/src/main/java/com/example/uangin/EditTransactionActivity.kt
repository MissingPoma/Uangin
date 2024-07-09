package com.example.uangin

import android.os.Bundle
import android.widget.AutoCompleteTextView
import android.widget.Button
import android.widget.ImageButton
import android.widget.RadioButton
import android.widget.RadioGroup
import androidx.appcompat.app.AppCompatActivity
import android.widget.TextView
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import java.text.SimpleDateFormat
import java.util.*

class EditTransactionActivity : AppCompatActivity() {

    private lateinit var radioGroupType: RadioGroup
    private lateinit var radioButtonIncome: RadioButton
    private lateinit var radioButtonOutcome: RadioButton
    private lateinit var categoryInput: TextInputLayout
    private lateinit var categoryAutoComplete: AutoCompleteTextView
    private lateinit var amountEditText: TextInputEditText
    private lateinit var noteEditText: TextInputEditText
    private lateinit var dateTextView: TextView
    private lateinit var saveButton: Button
    private lateinit var trashIcon: ImageButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_transaction)

        // Bind views
        radioGroupType = findViewById(R.id.radioGroupType)
        radioButtonIncome = findViewById(R.id.radioButtonIncome)
        radioButtonOutcome = findViewById(R.id.radioButtonOutcome)
        categoryInput = findViewById(R.id.pilihKategori)
        categoryAutoComplete = findViewById(R.id.itemKategori)  // Use AutoCompleteTextView here
        amountEditText = findViewById(R.id.jumlahEditText)
        noteEditText = findViewById(R.id.catatanEditText)
        dateTextView = findViewById(R.id.tanggalTextView)
        saveButton = findViewById(R.id.saveButton)
        trashIcon = findViewById(R.id.trashIcon)

        // Get transaction data from intent
        val transactionId = intent.getIntExtra("transaction_id", -1)
        val transactionType = intent.getBooleanExtra("transaction_type", true)
        val transactionAmount = intent.getFloatExtra("transaction_amount", 0f)
        val transactionCategory = intent.getStringExtra("transaction_category")
        val transactionNote = intent.getStringExtra("transaction_note")
        val transactionDate = intent.getLongExtra("transaction_date", 0L)

        // Populate the UI with transaction data
        if (transactionId != -1) {
            if (transactionType) {
                radioButtonOutcome.isChecked = true
            } else {
                radioButtonIncome.isChecked = true
            }
            categoryAutoComplete.setText(transactionCategory)
            amountEditText.setText(transactionAmount.toString())
            noteEditText.setText(transactionNote)
            val sdf = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
            dateTextView.text = sdf.format(Date(transactionDate))
        }

        // Handle save button click
        saveButton.setOnClickListener {
            // Save changes to the transaction
        }

        // Handle trash icon click
        trashIcon.setOnClickListener {
            // Delete the transaction
        }
    }
}
