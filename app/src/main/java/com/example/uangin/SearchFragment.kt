package com.example.uangin

import android.app.DatePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.uangin.database.AppDatabase
import com.example.uangin.database.dao.KategoriDao
import com.example.uangin.database.dao.PemasukanDao
import com.example.uangin.database.dao.PengeluaranDao
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

class SearchFragment : Fragment() {

    private lateinit var searchView: SearchView
    private lateinit var searchButton: Button
    private lateinit var recyclerView: RecyclerView
    private lateinit var autoCompleteTextView: AutoCompleteTextView
    private lateinit var categoryDao: KategoriDao
    private lateinit var pemasukanDao: PemasukanDao
    private lateinit var pengeluaranDao: PengeluaranDao
    private lateinit var calendarButton: ImageButton
    private lateinit var tanggalTextView: TextView
    private lateinit var calendarButtonSampai: ImageButton
    private lateinit var tanggalSampaiTextView: TextView
    private lateinit var jumlahMinEditText: EditText
    private lateinit var jumlahMaxEditText: EditText

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_search, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        searchView = view.findViewById(R.id.searchView)
        searchButton = view.findViewById(R.id.buttonCari)
        recyclerView = view.findViewById(R.id.recyclerView)
        autoCompleteTextView = view.findViewById(R.id.itemKategori)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        categoryDao = AppDatabase.getDatabase(requireContext()).kategoriDao()
        pemasukanDao = AppDatabase.getDatabase(requireContext()).pemasukanDao()
        pengeluaranDao = AppDatabase.getDatabase(requireContext()).pengeluaranDao()

        calendarButton = view.findViewById(R.id.calendarButton)
        tanggalTextView = view.findViewById(R.id.tanggalTextView)
        calendarButtonSampai = view.findViewById(R.id.calendarButtonSampai)
        tanggalSampaiTextView = view.findViewById(R.id.tanggalSampaiTextView)
        jumlahMinEditText = view.findViewById(R.id.jumlahMin)
        jumlahMaxEditText = view.findViewById(R.id.jumlahMax)

        updateCategoryDropdown()
        setupSearchView()
        setupSearchButton()

        calendarButton.setOnClickListener {
            showDatePickerDialog(tanggalTextView)
        }

        calendarButtonSampai.setOnClickListener {
            showDatePickerDialog(tanggalSampaiTextView)
        }
    }

    private fun showDatePickerDialog(textView: TextView) {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog = DatePickerDialog(
            requireContext(),
            { _, selectedYear, selectedMonth, selectedDay ->
                val formattedDate = String.format("%02d/%02d/%04d", selectedDay, selectedMonth + 1, selectedYear)
                textView.text = formattedDate
            },
            year,
            month,
            day
        )
        datePickerDialog.show()
    }

    private fun updateCategoryDropdown() {
        lifecycleScope.launch {
            val categories = withContext(Dispatchers.IO) {
                categoryDao.getAll().sortedBy { it.namaKategori }.map { it.namaKategori }
            }

            val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, categories)
            autoCompleteTextView.setAdapter(adapter)
        }
    }

    private fun setupSearchView() {
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                query?.let { performSearch() }
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                return true
            }
        })
    }

    private fun setupSearchButton() {
        searchButton.setOnClickListener {
            performSearch()
        }
    }

    private fun performSearch() {
        val selectedCategory = autoCompleteTextView.text.toString().ifBlank { null }
        val selectedNote = searchView.query.toString().trim().ifBlank { null }
        val minAmount = jumlahMinEditText.text.toString().toDoubleOrNull()
        val maxAmount = jumlahMaxEditText.text.toString().toDoubleOrNull()
        val startDate = parseDate(tanggalTextView.text.toString())
        val endDate = parseDate(tanggalSampaiTextView.text.toString())

        lifecycleScope.launch {
            val pemasukanList = withContext(Dispatchers.IO) {
                pemasukanDao.searchTransactions(
                    kategori = selectedCategory,
                    catatan = selectedNote,
                    minJumlah = minAmount,
                    maxJumlah = maxAmount,
                    startDate = startDate,
                    endDate = endDate
                )
            }
            val pengeluaranList = withContext(Dispatchers.IO) {
                pengeluaranDao.searchTransactions(
                    kategori = selectedCategory,
                    catatan = selectedNote,
                    minJumlah = minAmount,
                    maxJumlah = maxAmount,
                    startDate = startDate,
                    endDate = endDate
                )
            }

            val combinedList = pemasukanList + pengeluaranList
            updateRecyclerView(combinedList)
        }
    }

    private fun parseDate(dateStr: String): Date? {
        return if (dateStr.isNotEmpty() && dateStr != "Date Start" && dateStr != "Date End") {
            try {
                SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).parse(dateStr)
            } catch (e: ParseException) {
                null
            }
        } else {
            null
        }
    }

    private fun updateRecyclerView(data: List<Any>) {
        val adapter = SearchResultsAdapter(data)
        recyclerView.adapter = adapter
    }
}

//tra