package com.example.uangin.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.example.uangin.database.entity.Kategori

@Dao
interface KategoriDao {
    @Query("SELECT * FROM kategori")
    fun getAll(): List<Kategori>

    @Query("SELECT * FROM kategori WHERE id IN (:kategoriIds)")
    fun loadAllByIds(kategoriIds: IntArray): List<Kategori>

    @Query("SELECT * FROM kategori WHERE nama LIKE :nama LIMIT 1")
    fun findByName(nama: String): Kategori

    @Insert
    fun insertAll(vararg kategori: Kategori)

    @Delete
    fun delete(kategori: Kategori)

}