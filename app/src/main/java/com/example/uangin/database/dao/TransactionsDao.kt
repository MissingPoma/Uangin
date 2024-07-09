package com.example.uangin.database.dao


import androidx.room.*
import com.example.uangin.database.DateConverter
import com.example.uangin.database.entity.Transactions
import com.example.uangin.database.entity.Pemasukan
import com.example.uangin.database.entity.Pengeluaran

@Dao
@TypeConverters(DateConverter::class)
interface TransactionsDao {
    // Existing methods...

    @Query("SELECT * FROM Transactions WHERE id = :transactionId")
    suspend fun getTransactionById(transactionId: Int): Transactions?

    @Query("SELECT * FROM Pemasukan WHERE id = :pemasukanId")
    suspend fun getPemasukanById(pemasukanId: Int): Pemasukan?

    @Query("SELECT * FROM Pengeluaran WHERE id = :pengeluaranId")
    suspend fun getPengeluaranById(pengeluaranId: Int): Pengeluaran?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPemasukan(pemasukan: Pemasukan)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPengeluaran(pengeluaran: Pengeluaran)

    @Update
    suspend fun updateTransaction(transaction: Transactions)

    @Update
    suspend fun updatePemasukan(pemasukan: Pemasukan)

    @Update
    suspend fun updatePengeluaran(pengeluaran: Pengeluaran)

    @Delete
    suspend fun deleteTransaction(transaction: Transactions)

    @Delete
    suspend fun deletePemasukan(pemasukan: Pemasukan)

    @Delete
    suspend fun deletePengeluaran(pengeluaran: Pengeluaran)

    @Transaction
    suspend fun deleteTransactionWithLinkedData(transaction: Transactions) {
        deleteTransaction(transaction)
        if (transaction.isPengeluaran) {
            transaction.id?.let {
                val pengeluaran = getPengeluaranById(it)
                if (pengeluaran != null) {
                    deletePengeluaran(pengeluaran)
                }
            }
        } else {
            transaction.id?.let {
                val pemasukan = getPemasukanById(it)
                if (pemasukan != null) {
                    deletePemasukan(pemasukan)
                }
            }
        }
    }

    @Transaction
    suspend fun updateTransactionWithLinkedData(transaction: Transactions) {
        updateTransaction(transaction)
        if (transaction.isPengeluaran) {
            val pengeluaran = Pengeluaran(transaction.id, transaction.kategori, transaction.jumlah, transaction.catatan, transaction.tanggal)
            updatePengeluaran(pengeluaran)
        } else {
            val pemasukan = Pemasukan(transaction.id, transaction.kategori, transaction.jumlah, transaction.catatan, transaction.tanggal)
            updatePemasukan(pemasukan)
        }
    }
}
