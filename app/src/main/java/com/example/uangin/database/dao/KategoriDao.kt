package com.example.uangin.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.uangin.database.entity.Kategori

@Dao
interface KategoriDao {
    @Query("SELECT * FROM kategori ORDER BY namaKategori ASC")
    suspend fun getAll(): List<Kategori>

    @Insert
    suspend fun insert(kategori: Kategori)

    @Query("SELECT * FROM Kategori") // Tambahkan query untuk mengambil semua kategori
    suspend fun getAllKategori(): List<Kategori>

    @Query("SELECT * FROM kategori WHERE id IN (:kategoriIds)")
    suspend fun loadAllByIds(kategoriIds: IntArray): List<Kategori>

    @Query("SELECT * FROM kategori WHERE namaKategori LIKE :nama LIMIT 1")
    suspend fun findByName(nama: String): Kategori?

    @Query("SELECT * FROM Kategori WHERE id = :kategoriId") // getKategoriById
    suspend fun getKategoriById(kategoriId: Int): Kategori?

    @Update
    suspend fun update(kategori: Kategori)

    @Insert
    suspend fun insertAll(vararg kategori: Kategori)

    @Query("DELETE FROM kategori")
    suspend fun deleteAll()

    @Delete
    suspend fun delete(kategori: Kategori)
}
