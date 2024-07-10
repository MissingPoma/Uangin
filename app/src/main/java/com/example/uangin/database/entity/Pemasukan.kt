package com.example.uangin.database.entity
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date


@Entity(tableName = "Pemasukan")
data class Pemasukan(
    @PrimaryKey(autoGenerate = true) val id: Int? = null,
    @ColumnInfo(name = "kategori") val kategori: String?,
    @ColumnInfo(name = "jumlah") val jumlah: Double?,
    @ColumnInfo(name = "catatan") val catatan: String?,
    @ColumnInfo(name = "tanggal") val tanggal: Date
)

fun Pemasukan.toTransaction(): Transactions = Transactions(
    id = this.id,
    tanggal = this.tanggal,
    kategori = this.kategori,
    catatan = this.catatan,
    jumlah = this.jumlah,
    isPengeluaran = false // Pengeluaran selalu true
)
