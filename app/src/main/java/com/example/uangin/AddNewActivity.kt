package com.example.uangin // Sesuaikan dengan package Anda

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.uangin.R
import com.example.uangin.database.AppDatabase
import com.example.uangin.database.dao.KategoriDao
import com.example.uangin.database.entity.Kategori
import kotlinx.coroutines.launch

class AddNewActivity : AppCompatActivity() {

    private lateinit var categoryNameEditText: EditText
    private lateinit var saveButton: Button
    private lateinit var cancelButton: Button
    private lateinit var kategoriDao: KategoriDao

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_new) // Menggunakan layout activity_add_new

        categoryNameEditText = findViewById(R.id.category_name)
        saveButton = findViewById(R.id.save_button)
        cancelButton = findViewById(R.id.cancel_button)
        kategoriDao = AppDatabase.getDatabase(this).kategoriDao()

        saveButton.setOnClickListener {
            saveKategori()
        }

        cancelButton.setOnClickListener {
            finish() // Kembali ke activity sebelumnya
        }
    }

    private fun saveKategori() {
        val categoryName = categoryNameEditText.text.toString()
        if (categoryName.isNotBlank()) {
            lifecycleScope.launch {
                try {
                    val newKategori = Kategori(namaKategori = categoryName)
                    kategoriDao.insert(newKategori)
                    Toast.makeText(this@AddNewActivity, "Kategori berhasil ditambahkan", Toast.LENGTH_SHORT).show()
                    finish() // Kembali ke EditCategory setelah menyimpan
                } catch (e: Exception) {
                    Toast.makeText(this@AddNewActivity, "Gagal menambahkan kategori", Toast.LENGTH_SHORT).show()
                }
            }
        } else {
            Toast.makeText(this@AddNewActivity, "Nama kategori tidak boleh kosong", Toast.LENGTH_SHORT).show()
        }
    }
}
