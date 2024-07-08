package com.example.uangin

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.example.uangin.database.AppDatabase
import com.example.uangin.database.entity.Kategori
import com.google.android.material.textfield.TextInputEditText
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class AddingNewCategoryFragment : Fragment() {

    private lateinit var categoryNameEditText: TextInputEditText
    private lateinit var saveButton: Button
    private lateinit var cancelButton: Button

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_adding_new_category, container, false)

        // Initialize views
        categoryNameEditText = view.findViewById(R.id.category_name)
        saveButton = view.findViewById(R.id.save_button)
        cancelButton = view.findViewById(R.id.cancel_button)

        // Set click listeners
        saveButton.setOnClickListener { saveCategory() }
        cancelButton.setOnClickListener { cancel() }

        return view
    }

    private fun saveCategory() {
        val categoryName = categoryNameEditText.text.toString().trim()

        if (categoryName.isEmpty()) {
            Toast.makeText(requireContext(), "Nama kategori tidak boleh kosong", Toast.LENGTH_SHORT).show()
            return
        }

        lifecycleScope.launch {
            withContext(Dispatchers.IO) {
                val db = AppDatabase.getDatabase(requireContext())
                db.kategoriDao().insert(Kategori(namaKategori = categoryName))
            }
            withContext(Dispatchers.Main) {
                Toast.makeText(requireContext(), "Kategori berhasil disimpan", Toast.LENGTH_SHORT).show()

                // Update the dropdown in AddActivity
                (activity as? AddActivity)?.updateCategoryDropdown()

                // Close the fragment
                parentFragmentManager.popBackStack()
            }
        }
    }

    private fun cancel() {
        parentFragmentManager.popBackStack()
    }
}
