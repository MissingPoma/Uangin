package com.example.uangin.database.entity
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "pemasukan")
data class Pemasukan(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val category: String,
    val amount: Double,
    val note: String,
    val date: String
)
