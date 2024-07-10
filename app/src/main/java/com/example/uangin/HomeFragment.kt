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
        updateFinanceInfo()
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
        // Panggil updateFinanceInfo untuk pertama kali
        updateFinanceInfo()
        setupRecyclerView(view)
        loadTransactions("semua") // Load semua transaksi saat awal
        return view
    }

    private fun updateFinanceInfo() {
        val db = AppDatabase.getDatabase(requireContext())
        val pengeluaranDao = db.pengeluaranDao()
        val pemasukanDao = db.pemasukanDao()

        CoroutineScope(Dispatchers.IO).launch {
            // Hitung total pemasukan
            val totalPemasukan = pemasukanDao.getTotalAmount()

            // Hitung total pengeluaran
            val totalPengeluaran = pengeluaranDao.getTotalAmount()

            // Hitung saldo
            val saldo = totalPemasukan - totalPengeluaran

            // Update UI di main thread
            withContext(Dispatchers.Main) {
                // Update TextView Pemasukan
                view?.findViewById<TextView>(R.id.textPemasukan)?.text = "Rp ${totalPemasukan}"

                // Update TextView Pengeluaran
                view?.findViewById<TextView>(R.id.textPengeluaran)?.text = "Rp ${totalPengeluaran}"

                // Update TextView Saldo
                view?.findViewById<TextView>(R.id.textSaldo)?.text = "Rp ${saldo}"
            }
        }
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

    private fun loadTransactions(filter: String = "semua") {
        val db = AppDatabase.getDatabase(requireContext())
        val pengeluaranDao = db.pengeluaranDao()
        val pemasukanDao = db.pemasukanDao()

        CoroutineScope(Dispatchers.IO).launch {
            val combinedList = when (filter) {
                "pemasukan" -> pemasukanDao.getAllOrderedByDateDescAndIdDesc().map { it.toTransaction() }
                "pengeluaran" -> pengeluaranDao.getAllOrderedByDateDescAndIdDesc().map { it.toTransaction() }
                else -> { // "semua" atau filter tidak valid
                    val pengeluaranList = pengeluaranDao.getAllOrderedByDateDescAndIdDesc()
                    val pemasukanList = pemasukanDao.getAllOrderedByDateDescAndIdDesc()
                    (pengeluaranList.map { it.toTransaction() } + pemasukanList.map { it.toTransaction() }).sortedByDescending { it.tanggal }.toMutableList()
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