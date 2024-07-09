package com.example.uangin.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.uangin.database.entity.Pengeluaran

@Dao
interface PengeluaranDao {
    @Query("SELECT * FROM pengeluaran")
    fun getAll(): List<Pengeluaran>

    @Query("SELECT * FROM pengeluaran WHERE id IN (:pengeluaranIds)")
    fun loadAllByIds(pengeluaranIds: IntArray): List<Pengeluaran>

    @Query("SELECT * FROM pengeluaran WHERE kategori LIKE :kategori LIMIT 1")
    fun findByKategori(kategori: String): Pengeluaran

    @Query("SELECT * FROM pengeluaran WHERE id = :id")
    suspend fun getPengeluaranById(id: Int): Pengeluaran?

    @Update
    suspend fun update(pengeluaran: Pengeluaran) // Tambahkan fungsi update

    @Query("DELETE FROM pengeluaran")
    suspend fun deleteAll()

    @Insert
    fun insertAll(vararg pengeluaran: Pengeluaran)

    @Delete
    suspend fun delete(pengeluaran: Pengeluaran)

    @Query("SELECT * FROM pengeluaran ORDER BY tanggal DESC")
    fun getAllOrderedByDate(): List<Pengeluaran>

    @Update
    suspend fun update(pengeluaran: Pengeluaran)

    @Query("DELETE FROM pengeluaran WHERE id = :transactionId")
    suspend fun delete(transactionId: Int)


}