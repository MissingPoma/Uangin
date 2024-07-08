package com.example.uangin.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.example.uangin.database.entity.Pemasukan


@Dao
interface PemasukanDao {
    @Query("SELECT * FROM pemasukan")
    fun getAll(): List<Pemasukan>

    @Query("SELECT * FROM pemasukan WHERE id IN (:pemasukanIds)")
    fun loadAllByIds(pemasukanIds: IntArray): List<Pemasukan>

    @Query("SELECT * FROM pemasukan WHERE kategori LIKE :kategori LIMIT 1")
    fun findByKategori(kategori: String): Pemasukan

    @Insert
    fun insertAll(vararg pemasukan: Pemasukan)

    @Delete
    fun delete(pemasukan: Pemasukan)

    @Query("SELECT * FROM pemasukan ORDER BY tanggal DESC")
    fun getAllOrderedByDate(): List<Pemasukan>
}