package com.example.uangin.repository



import com.example.uangin.database.dao.PemasukanDao
import com.example.uangin.database.dao.PengeluaranDao
import com.example.uangin.database.entity.Pemasukan
import com.example.uangin.database.entity.Pengeluaran

class FinancialRepository(private val pemasukanDao: PemasukanDao, private val pengeluaranDao: PengeluaranDao) {
    suspend fun insertPemasukan(pemasukan: Pemasukan) {
        pemasukanDao.insert(pemasukan)
    }

    suspend fun getAllPemasukan(): List<Pemasukan> {
        return pemasukanDao.getAllPemasukan()
    }

    suspend fun insertPengeluaran(pengeluaran: Pengeluaran) {
        pengeluaranDao.insert(pengeluaran)
    }

    suspend fun getAllPengeluaran(): List<Pengeluaran> {
        return pengeluaranDao.getAllPengeluaran()
    }
}
