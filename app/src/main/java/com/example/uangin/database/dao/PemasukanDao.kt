package com.example.uangin.database.da

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.uangin.database.entity.Pemasukan

@Dao
interface PemasukanDao {
    @Insert
    suspend fun insert(pemasukan: Pemasukan)

    @Query("SELECT * FROM pemasukan")
    suspend fun getAllPemasukan(): List<Pemasukan>
}