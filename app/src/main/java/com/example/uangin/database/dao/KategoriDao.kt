package com.example.uangin.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.example.uangin.database.entity.Kategori

@Dao
interface KategoriDao {
    @Query("SELECT * FROM kategori ORDER BY namaKategori ASC")
    suspend fun getAll(): List<Kategori>

    @Insert
    suspend fun insert(kategori: Kategori)

    @Query("SELECT * FROM kategori WHERE id IN (:kategoriIds)")
    suspend fun loadAllByIds(kategoriIds: IntArray): List<Kategori>

    @Query("SELECT * FROM kategori WHERE namaKategori LIKE :nama LIMIT 1")
    suspend fun findByName(nama: String): Kategori?

    @Insert
    suspend fun insertAll(vararg kategori: Kategori)

    @Query("DELETE FROM kategori")
    suspend fun deleteAll()

    @Delete
    suspend fun delete(kategori: Kategori)
}
