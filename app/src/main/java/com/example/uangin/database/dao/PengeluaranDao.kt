package com.example.uangin.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.uangin.database.entity.Pengeluaran
import java.util.Date

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
    suspend fun delete(pengeluaran: Pengeluaran)

    @Query("SELECT * FROM pengeluaran ORDER BY tanggal DESC")
    fun getAllOrderedByDate(): List<Pengeluaran>

    @Update
    suspend fun update(pengeluaran: Pengeluaran)

    @Query("DELETE FROM pengeluaran WHERE id = :transactionId")
    suspend fun delete(transactionId: Int)

    @Query("SELECT * FROM Pengeluaran ORDER BY tanggal DESC, id DESC")
    fun getAllOrderedByDateDescAndIdDesc(): List<Pengeluaran>

    @Query("SELECT COALESCE(SUM(jumlah), 0) FROM pengeluaran")
    suspend fun getTotalAmount(): Long

    @Query("SELECT * FROM Pengeluaran WHERE kategori = :category")
    suspend fun searchByCategory(category: String): List<Pengeluaran>

    @Query("SELECT * FROM Pengeluaran WHERE catatan LIKE '%' || :note || '%'")
    suspend fun searchByNote(note: String): List<Pengeluaran>

    @Query("SELECT * FROM Pengeluaran WHERE kategori = :category AND catatan LIKE '%' || :note || '%'")
    suspend fun searchByCategoryAndNote(category: String, note: String): List<Pengeluaran>

    @Query("SELECT * FROM pengeluaran WHERE catatan LIKE '%' || :query || '%' OR kategori LIKE :query")
    suspend fun searchByNoteOrCategory(query: String): List<Pengeluaran>

    @Query("""
        SELECT * FROM Pengeluaran WHERE
        (:kategori IS NULL OR kategori LIKE '%' || :kategori || '%') AND
        (:catatan IS NULL OR catatan LIKE '%' || :catatan || '%') AND
        (:minJumlah IS NULL OR jumlah >= :minJumlah) AND
        (:maxJumlah IS NULL OR jumlah <= :maxJumlah) AND
        (:startDate IS NULL OR tanggal >= :startDate) AND
        (:endDate IS NULL OR tanggal <= :endDate)
    """)
    suspend fun searchTransactions(
        kategori: String?,
        catatan: String?,
        minJumlah: Double?,
        maxJumlah: Double?,
        startDate: Date?,
        endDate: Date?
    ): List<Pengeluaran>
}