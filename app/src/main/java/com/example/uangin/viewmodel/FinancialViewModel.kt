package com.example.uangin.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.uangin.repository.FinancialRepository
import com.example.uangin.database.entity.Pemasukan
import com.example.uangin.database.entity.Pengeluaran
import kotlinx.coroutines.launch

class FinancialViewModel(private val repository: FinancialRepository) : ViewModel() {

    fun insertPemasukan(pemasukan: Pemasukan) = viewModelScope.launch {
        repository.insertPemasukan(pemasukan)
    }

    fun getAllPemasukan() = viewModelScope.launch {
        repository.getAllPemasukan()
    }

    fun insertPengeluaran(pengeluaran: Pengeluaran) = viewModelScope.launch {
        repository.insertPengeluaran(pengeluaran)
    }

    fun getAllPengeluaran() = viewModelScope.launch {
        repository.getAllPengeluaran()
    }
}
