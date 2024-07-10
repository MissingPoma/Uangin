import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.uangin.R
import com.example.uangin.SearchResultsAdapter
import com.example.uangin.database.AppDatabase
import com.example.uangin.database.dao.KategoriDao
import com.example.uangin.database.dao.PemasukanDao
import com.example.uangin.database.dao.PengeluaranDao
import com.example.uangin.database.entity.Kategori
import com.example.uangin.database.entity.Pemasukan
import com.example.uangin.database.entity.Pengeluaran
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class SearchFragment : Fragment() {

    private lateinit var searchView: SearchView
    private lateinit var searchButton: Button
    private lateinit var recyclerView: RecyclerView
    private lateinit var autoCompleteTextView: AutoCompleteTextView
    private lateinit var categoryDao: KategoriDao
    private lateinit var pemasukanDao: PemasukanDao
    private lateinit var pengeluaranDao: PengeluaranDao

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

        updateCategoryDropdown()
        setupSearchView()
        setupSearchButton()
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
                query?.let { searchTransactionsByNoteOrCategory(it) }
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                // Implement if needed
                return true
            }
        })
    }

    private fun setupSearchButton() {
        searchButton.setOnClickListener {
            val selectedCategory = autoCompleteTextView.text.toString()
            val selectedNote = searchView.query.toString().trim()

            if (selectedCategory.isNotBlank()) {
                if (selectedNote.isBlank()) {
                    searchTransactionsByCategory(selectedCategory)
                } else {
                    searchTransactionsByCategoryAndNote(selectedCategory, selectedNote)
                }
            } else if (selectedNote.isNotBlank()) {
                searchTransactionsByNoteOrCategory(selectedNote)
            } else {
                Toast.makeText(requireContext(), "Masukkan kategori atau catatan untuk pencarian", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun searchTransactionsByCategory(category: String) {
        lifecycleScope.launch {
            val pemasukanList = withContext(Dispatchers.IO) {
                pemasukanDao.searchByCategory(category)
            }
            val pengeluaranList = withContext(Dispatchers.IO) {
                pengeluaranDao.searchByCategory(category)
            }

            val combinedList = pemasukanList + pengeluaranList
            updateRecyclerView(combinedList)
        }
    }

    private fun searchTransactionsByNoteOrCategory(query: String) {
        lifecycleScope.launch {
            val pemasukanList = withContext(Dispatchers.IO) {
                pemasukanDao.searchByNoteOrCategory(query)
            }
            val pengeluaranList = withContext(Dispatchers.IO) {
                pengeluaranDao.searchByNoteOrCategory(query)
            }

            val combinedList = pemasukanList + pengeluaranList
            updateRecyclerView(combinedList)
        }
    }

    private fun searchTransactionsByCategoryAndNote(category: String, note: String) {
        lifecycleScope.launch {
            val pemasukanList = withContext(Dispatchers.IO) {
                pemasukanDao.searchByCategoryAndNote(category, note)
            }
            val pengeluaranList = withContext(Dispatchers.IO) {
                pengeluaranDao.searchByCategoryAndNote(category, note)
            }

            val combinedList = pemasukanList + pengeluaranList
            updateRecyclerView(combinedList)
        }
    }

    private fun updateRecyclerView(data: List<Any>) {
        val adapter = SearchResultsAdapter(data)
        recyclerView.adapter = adapter
    }
}

//tra