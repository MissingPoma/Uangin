package com.example.uangin.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.uangin.database.entity.Pemasukan


@Dao
interface PemasukanDao {
    @Query("SELECT * FROM pemasukan")
    fun getAll(): List<Pemasukan>

    @Query("SELECT * FROM pemasukan WHERE id IN (:pemasukanIds)")
    fun loadAllByIds(pemasukanIds: IntArray): List<Pemasukan>

    @Query("SELECT * FROM pemasukan WHERE kategori LIKE :kategori LIMIT 1")
    fun findByKategori(kategori: String): Pemasukan

    @Query("DELETE FROM pemasukan")
    suspend fun deleteAll()

    @Insert
    fun insertAll(vararg pemasukan: Pemasukan)

    @Delete
    suspend fun delete(pemasukan: Pemasukan)

    @Query("SELECT * FROM pemasukan ORDER BY tanggal DESC")
    fun getAllOrderedByDate(): List<Pemasukan>

    @Update
    suspend fun update(pemasukan: Pemasukan)

    @Query("DELETE FROM pemasukan WHERE id = :transactionId")
    suspend fun delete(transactionId: Int)

    @Query("SELECT * FROM Pemasukan ORDER BY tanggal DESC, id DESC")
    fun getAllOrderedByDateDescAndIdDesc(): List<Pemasukan>

    @Query("SELECT COALESCE(SUM(jumlah), 0) FROM pemasukan")
    suspend fun getTotalAmount(): Long

    @Query("SELECT * FROM Pemasukan WHERE kategori = :category")
    suspend fun searchByCategory(category: String): List<Pemasukan>

    @Query("SELECT * FROM Pemasukan WHERE catatan LIKE '%' || :note || '%'")
    suspend fun searchByNote(note: String): List<Pemasukan>

    @Query("SELECT * FROM Pemasukan WHERE kategori = :category AND catatan LIKE '%' || :note || '%'")
    suspend fun searchByCategoryAndNote(category: String, note: String): List<Pemasukan>
}