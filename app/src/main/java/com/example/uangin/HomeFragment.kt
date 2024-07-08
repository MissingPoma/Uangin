package com.example.uangin

import TransactionAdapter
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.uangin.MainActivity
import com.example.uangin.R
import com.example.uangin.database.AppDatabase
import com.example.uangin.database.entity.Transaction
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.*

class HomeFragment : Fragment() {

    private lateinit var transactionAdapter: TransactionAdapter
    private val transactionList = mutableListOf<Transaction>()
    private val dateFormat = SimpleDateFormat("d MMMM yyyy", Locale("id", "ID"))

    override fun onResume() {
        super.onResume()
        (activity as MainActivity).showFab()
    }

    override fun onPause() {
        super.onPause()
        (activity as MainActivity).hideFab()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_home, container, false)
        setupRecyclerView(view)
        loadTransactions()
        return view
    }

    private fun setupRecyclerView(view: View) {
        transactionAdapter = TransactionAdapter(transactionList)
        val recyclerView = view.findViewById<RecyclerView>(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.adapter = transactionAdapter
    }

    private fun loadTransactions() {
        val db = AppDatabase.getDatabase(requireContext())
        val pengeluaranDao = db.pengeluaranDao()
        val pemasukanDao = db.pemasukanDao()

        CoroutineScope(Dispatchers.IO).launch {
            val pengeluaranList = pengeluaranDao.getAllOrderedByDate()
            val pemasukanList = pemasukanDao.getAllOrderedByDate()

            val combinedList = mutableListOf<Transaction>()

            pengeluaranList.forEach {
                try {
                    val date = dateFormat.parse(it.tanggal)
                    combinedList.add(Transaction(it.id, date, it.kategori, it.catatan, it.jumlah, true))
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }

            pemasukanList.forEach {
                try {
                    val date = dateFormat.parse(it.tanggal)
                    combinedList.add(Transaction(it.id, date, it.kategori, it.catatan, it.jumlah, false))
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }

            combinedList.sortByDescending { it.tanggal }

            withContext(Dispatchers.Main) {
                transactionList.clear()
                transactionList.addAll(combinedList)
                transactionAdapter.notifyDataSetChanged()
            }
        }
    }
}
