package com.example.uangin.database.dao
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.uangin.database.entity.Pengeluaran

@Dao
interface PengeluaranDao {
    @Insert
    suspend fun insert(pengeluaran: Pengeluaran)

    @Query("SELECT * FROM pengeluaran")
    suspend fun getAllPengeluaran(): List<Pengeluaran>
}