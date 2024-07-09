package com.example.uangin.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.example.uangin.database.DateConverter
import java.util.Date



@Entity(tableName = "Transactions")
@TypeConverters(DateConverter::class)
data class Transactions(

    @PrimaryKey(autoGenerate = true) val id: Int? = null,
    @ColumnInfo(name = "tanggal") val tanggal: Date,
    @ColumnInfo(name = "kategori") val kategori: String?,
    @ColumnInfo(name = "catatan") val catatan: String?,
    @ColumnInfo(name = "jumlah") val jumlah: Double?,
    @ColumnInfo(name = "isPengeluaran") val isPengeluaran: Boolean
){
    fun toPengeluaran(): Pengeluaran {
        return Pengeluaran(id,  kategori, jumlah, catatan, tanggal)
    }

    fun toPemasukan(): Pemasukan {
        return Pemasukan(id,  kategori, jumlah, catatan, tanggal)
    }
}
