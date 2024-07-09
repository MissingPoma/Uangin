package com.example.uangin

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.ImageButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.uangin.R
import com.example.uangin.database.AppDatabase
import com.example.uangin.database.dao.KategoriDao
import com.example.uangin.database.entity.Kategori
import kotlinx.coroutines.launch

class EditCategory : AppCompatActivity() {

    private lateinit var leftArrow: ImageButton
    private lateinit var addCategory: ImageButton
    private lateinit var recyclerView: RecyclerView
    private lateinit var kategoriAdapter: KategoriAdapter
    private lateinit var kategoriDao: KategoriDao

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_category)

        leftArrow = findViewById(R.id.leftArrow)
        recyclerView = findViewById(R.id.recyclerView)
        addCategory = findViewById(R.id.addCategory)
        kategoriDao = AppDatabase.getDatabase(this).kategoriDao()

        leftArrow.setOnClickListener {
            onBackPressed()
        }

        addCategory.setOnClickListener {
            val intent = Intent(this, AddNewActivity::class.java)
            startActivity(intent)
        }

        setupRecyclerView()
    }

    private fun setupRecyclerView() {
        lifecycleScope.launch {
            val kategoriList = kategoriDao.getAllKategori().toMutableList()
            kategoriAdapter = KategoriAdapter(
                kategoriList,
                onEditClickListener = { kategori ->
                    val intent = Intent(this@EditCategory, EditSpesificCategory::class.java)
                    intent.putExtra("kategoriId", kategori.id)
                    startActivityForResult(intent, EDIT_KATEGORI_REQUEST_CODE) // Menggunakan startActivityForResult
                },
                onDeleteClickListener = { kategori ->
                    deleteKategori(kategori)
                }
            )
            recyclerView.adapter = kategoriAdapter
            recyclerView.layoutManager = LinearLayoutManager(this@EditCategory)
        }
    }

    private fun deleteKategori(kategori: Kategori) {
        lifecycleScope.launch {
            try {
                kategoriDao.delete(kategori)
                val updatedKategoriList = kategoriDao.getAllKategori()
                kategoriAdapter.updateData(updatedKategoriList) // Perbarui RecyclerView
                Toast.makeText(this@EditCategory, "Kategori berhasil dihapus", Toast.LENGTH_SHORT).show()
            } catch (e: Exception) {
                Toast.makeText(this@EditCategory, "Gagal menghapus kategori", Toast.LENGTH_SHORT).show()
            }
        }
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == EDIT_KATEGORI_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            setupRecyclerView() // Refresh RecyclerView setelah edit
        }
    }

    companion object {
        private const val EDIT_KATEGORI_REQUEST_CODE = 1
    }

    override fun onResume() {
        super.onResume()
        setupRecyclerView() // Refresh RecyclerView saat kembali ke activity
    }
}