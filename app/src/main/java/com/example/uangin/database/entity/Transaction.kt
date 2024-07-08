package com.example.uangin.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date


@Entity(tableName = "Transaction")
data class Transaction(

    @PrimaryKey(autoGenerate = true) val id: Int? = null,
    @ColumnInfo(name = "tanggal") val tanggal: Date,
    @ColumnInfo(name = "kategori") val kategori: String?,
    @ColumnInfo(name = "catatan") val catatan: String?,
    @ColumnInfo(name = "jumlah") val jumlah: Double?,
    @ColumnInfo(name = "isPengeluaran") val isPengeluaran: Boolean
)
