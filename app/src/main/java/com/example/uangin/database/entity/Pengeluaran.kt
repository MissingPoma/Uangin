package com.example.uangin.database.entity
import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "pengeluaran")
data class Pengeluaran(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val category: String,
    val amount: Double,
    val note: String,
    val date: String
)
