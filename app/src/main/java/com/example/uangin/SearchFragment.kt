import android.os.Bundle
import android.view.*
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
                    categoryDao.getAll().sortedBy { it.namaKategori }.map { it.namaKategori }
                }

                val categoriesWithAddOption = updatedCategories.toMutableList()
                categoriesWithAddOption.add("Add Category")

                val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, categoriesWithAddOption)
                // Set adapter to autoCompleteTextView
            } else {
                val categoriesWithAddOption = categories.toMutableList()
                categoriesWithAddOption.add("Add Category")

                val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, categoriesWithAddOption)
                // Set adapter to autoCompleteTextView
            }
        }
    }

    private fun setupSearchView() {
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                query?.let { searchTransactionsByNote(it) }
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
            val selectedCategory = "" // Ganti dengan nilai yang sesuai dari autoCompleteTextView jika perlu
            val selectedNote = searchView.query.toString().trim()

            if (selectedCategory.isNotBlank() && selectedCategory != "Add Category") {
                if (selectedNote.isBlank()) {
                    searchTransactionsByCategory(selectedCategory)
                } else {
                    searchTransactionsByCategoryAndNote(selectedCategory, selectedNote)
                }
            } else if (selectedNote.isNotBlank()) {
                searchTransactionsByNote(selectedNote)
            } else {
                Toast.makeText(requireContext(), "Masukkan catatan untuk pencarian", Toast.LENGTH_SHORT).show()
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

    private fun searchTransactionsByNote(note: String) {
        lifecycleScope.launch {
            val pemasukanList = withContext(Dispatchers.IO) {
                pemasukanDao.searchByNote(note)
            }
            val pengeluaranList = withContext(Dispatchers.IO) {
                pengeluaranDao.searchByNote(note)
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
