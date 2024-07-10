package com.example.uangin.database.entity
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date


@Entity(tableName = "Pengeluaran")
data class Pengeluaran(

    @PrimaryKey(autoGenerate = true) val id: Int? = null,
    @ColumnInfo(name = "kategori") val kategori: String?,
    @ColumnInfo(name = "jumlah") val jumlah: Double?,
    @ColumnInfo(name = "catatan") val catatan: String?,
    @ColumnInfo(name = "tanggal") val tanggal: Date
)

fun Pengeluaran.toTransaction(): Transactions = Transactions(
    id = this.id,
    tanggal = this.tanggal,
    kategori = this.kategori,
    catatan = this.catatan,
    jumlah = this.jumlah,
    isPengeluaran = true // Pengeluaran selalu true
)
