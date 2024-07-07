package com.example.uangin.database.entity
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity
data class Pengeluaran(

    @PrimaryKey(autoGenerate = true) val id: Int? = null,
    @ColumnInfo(name = "kategori") val kategori: String?,
    @ColumnInfo(name = "jumlah") val jumlah: Double?,
    @ColumnInfo(name = "catatan") val catatan: String?,
    @ColumnInfo(name = "tanggal") val tanggal: String?
)
