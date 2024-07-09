package com.example.uangin // Sesuaikan package

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.example.uangin.R
import com.example.uangin.database.AppDatabase
import com.example.uangin.database.dao.KategoriDao
import com.example.uangin.database.entity.Kategori
import kotlinx.coroutines.launch

class EditSpesificCategory : AppCompatActivity() {

    private lateinit var kategoriDao: KategoriDao
    private lateinit var categoryNameEditText: EditText
    private lateinit var saveButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // ... (Pengaturan Edge-to-Edge dan padding)

        setContentView(R.layout.activity_edit_spesific_category)

        kategoriDao = AppDatabase.getDatabase(this).kategoriDao()
        categoryNameEditText = findViewById(R.id.category_name)
        saveButton = findViewById(R.id.save_button)
        val cancelButton: Button = findViewById(R.id.cancel_button)

        // Ambil data kategori dari Intent
        val kategoriId = intent.getIntExtra("kategoriId", -1)
        if (kategoriId != -1) {
            loadKategori(kategoriId)
        }

        cancelButton.setOnClickListener {
            finish() // Menutup activity saat tombol batal ditekan
        }

        saveButton.setOnClickListener {
            saveKategori(kategoriId)
        }
    }

    private fun loadKategori(kategoriId: Int) {
        lifecycleScope.launch {
            val kategori = kategoriDao.getKategoriById(kategoriId)
            categoryNameEditText.setText(kategori?.namaKategori)
        }
    }

    private fun saveKategori(kategoriId: Int) {
        val newCategoryName = categoryNameEditText.text.toString()
        if (newCategoryName.isNotBlank()) {
            lifecycleScope.launch {
                try {
                    val kategoriToUpdate = Kategori(kategoriId, newCategoryName)
                    kategoriDao.update(kategoriToUpdate)
                    Toast.makeText(this@EditSpesificCategory, "Kategori berhasil diperbarui", Toast.LENGTH_SHORT).show()
                    finish() // Kembali ke EditCategory setelah menyimpan
                } catch (e: Exception) {
                    Toast.makeText(this@EditSpesificCategory, "Gagal memperbarui kategori", Toast.LENGTH_SHORT).show()
                }
            }
        } else {
            Toast.makeText(this@EditSpesificCategory, "Nama kategori tidak boleh kosong", Toast.LENGTH_SHORT).show()
        }
    }
}
