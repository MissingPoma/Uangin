package com.example.uangin

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.PopupMenu
import android.widget.TextView
import com.example.uangin.database.entity.toTransaction
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.uangin.database.AppDatabase
import com.example.uangin.database.entity.Transactions
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.*

class HomeFragment : Fragment() {

    private lateinit var transactionAdapter: TransactionAdapter
    private val transactionList = mutableListOf<Transactions>()
    private lateinit var buttonMenu: ImageButton
    private lateinit var toolbarTitle: TextView

    override fun onResume() {
        super.onResume()
        (activity as MainActivity).showFab()
        loadTransactions()
    }

    override fun onPause() {
        super.onPause()
        (activity as MainActivity).hideFab()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_home, container, false)

        buttonMenu = view.findViewById(R.id.buttonMenu)
        toolbarTitle = view.findViewById(R.id.toolbarTitle)

        buttonMenu.setOnClickListener {
            showFilterPopupMenu(it)
        }
        setupRecyclerView(view)
        loadTransactions("semua") // Load semua transaksi saat awal
        return view
    }

    private fun showFilterPopupMenu(view: View) {
        val popupMenu = PopupMenu(requireContext(), view)
        popupMenu.inflate(R.menu.menu_filter) // Ganti "menu_filter" dengan ID menu Anda yang sesuai

        popupMenu.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.semuaCatatan -> {
                    loadTransactions("semua")
                    toolbarTitle.text = "Semua Catatan" // Update judul Toolbar
                }
                R.id.pemasukan -> {
                    loadTransactions("pemasukan")
                    toolbarTitle.text = "Pemasukan" // Update judul Toolbar
                }
                R.id.pengeluaran -> {
                    loadTransactions("pengeluaran")
                    toolbarTitle.text = "Pengeluaran" // Update judul Toolbar
                }
            }
            true
        }
        popupMenu.show()
    }

    private fun setupRecyclerView(view: View) {
        transactionAdapter = TransactionAdapter(transactionList)
        val recyclerView = view.findViewById<RecyclerView>(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.adapter = transactionAdapter
    }

    private fun loadTransactions(filter: String = "semua") { // Tambahkan parameter filter
        val db = AppDatabase.getDatabase(requireContext())
        val pengeluaranDao = db.pengeluaranDao()
        val pemasukanDao = db.pemasukanDao()

        CoroutineScope(Dispatchers.IO).launch {
            val combinedList = when (filter) {
                "pemasukan" -> pemasukanDao.getAllOrderedByDate().map { it.toTransaction() }
                "pengeluaran" -> pengeluaranDao.getAllOrderedByDate().map { it.toTransaction() }
                else -> { // "semua" atau filter tidak valid
                    val pengeluaranList = pengeluaranDao.getAllOrderedByDate()
                    val pemasukanList = pemasukanDao.getAllOrderedByDate()
                    (pengeluaranList.map { it.toTransaction() } + pemasukanList.map { it.toTransaction() }).toMutableList()
                }
            }


            withContext(Dispatchers.Main) {
                transactionList.clear()
                transactionList.addAll(combinedList)
                transactionAdapter.notifyDataSetChanged()
            }
        }
    }

}