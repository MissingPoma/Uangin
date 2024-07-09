package com.example.uangin

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.uangin.database.AppDatabase
import com.example.uangin.database.entity.Transactions
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.*

class HomeFragment : Fragment() {

    private lateinit var transactionAdapter: TransactionAdapter
    private val transactionList = mutableListOf<Transactions>()

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
        setupRecyclerView(view)
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

            val combinedList = mutableListOf<Transactions>()

            pengeluaranList.forEach {
                combinedList.add(
                    Transactions(
                        it.id,
                        it.tanggal,
                        it.kategori,
                        it.catatan,
                        it.jumlah,
                        true
                    )
                )
            }

            pemasukanList.forEach {
                combinedList.add(
                    Transactions(
                        it.id,
                        it.tanggal,
                        it.kategori,
                        it.catatan,
                        it.jumlah,
                        false
                    )
                )
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
