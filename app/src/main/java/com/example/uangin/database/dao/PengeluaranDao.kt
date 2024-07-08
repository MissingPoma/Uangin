package com.example.uangin.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.example.uangin.database.entity.Pengeluaran

@Dao
interface PengeluaranDao {
    @Query("SELECT * FROM pengeluaran")
    fun getAll(): List<Pengeluaran>

    @Query("SELECT * FROM pengeluaran WHERE id IN (:pengeluaranIds)")
    fun loadAllByIds(pengeluaranIds: IntArray): List<Pengeluaran>

    @Query("SELECT * FROM pengeluaran WHERE kategori LIKE :kategori LIMIT 1")
    fun findByKategori(kategori: String): Pengeluaran

    @Query("DELETE FROM pengeluaran")
    suspend fun deleteAll()

    @Insert
    fun insertAll(vararg pengeluaran: Pengeluaran)

    @Delete
    fun delete(pengeluaran: Pengeluaran)
}