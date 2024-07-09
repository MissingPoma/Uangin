package com.example.uangin.database

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.uangin.database.entity.Pengeluaran
import com.example.uangin.database.entity.Pemasukan
import com.example.uangin.database.entity.Transactions
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class TransactionViewModel(application: Application) : AndroidViewModel(application) {

    private val db = AppDatabase.getDatabase(application)
    private val pengeluaranDao = db.pengeluaranDao()
    private val pemasukanDao = db.pemasukanDao()

    fun update(transaction: Transactions) {
        viewModelScope.launch(Dispatchers.IO) {
            if (transaction.isPengeluaran) {
                pengeluaranDao.update(transaction.toPengeluaran())
            } else {
                pemasukanDao.update(transaction.toPemasukan())
            }
        }
    }

    fun delete(transactionId: Int, isPengeluaran: Boolean) {
        viewModelScope.launch(Dispatchers.IO) {
            if (isPengeluaran) {
                pengeluaranDao.delete(transactionId)
            } else {
                pemasukanDao.delete(transactionId)
            }
        }
    }

    suspend fun getAllPemasukan(): List<Pemasukan> {
        return withContext(Dispatchers.IO) {
            pemasukanDao.getAllOrderedByDate()
        }
    }

    // Method untuk mengambil semua pengeluaran, diurutkan berdasarkan tanggal dan ID terbesar
    suspend fun getAllPengeluaran(): List<Pengeluaran> {
        return withContext(Dispatchers.IO) {
            pengeluaranDao.getAllOrderedByDate()
        }
    }
}
