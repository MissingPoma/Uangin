package com.example.uangin.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Kategori(
    @PrimaryKey(autoGenerate = true) val id: Int? = null,
    @ColumnInfo(name = "namaKategori") val namaKategori: String?
)
