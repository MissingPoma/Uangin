package com.example.uangin

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.example.uangin.database.AppDatabase
import com.example.uangin.database.dao.KategoriDao
import com.example.uangin.database.dao.PemasukanDao
import com.example.uangin.database.dao.PengeluaranDao
import com.example.uangin.database.entity.Pemasukan
import com.example.uangin.database.entity.Pengeluaran
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.formatter.PercentFormatter
import com.github.mikephil.charting.utils.ColorTemplate
import com.google.android.material.button.MaterialButtonToggleGroup
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.NumberFormat
import java.util.*

class ChartFragment : Fragment() {

    private lateinit var pieChart: PieChart
    private lateinit var toggleButton: MaterialButtonToggleGroup
    private lateinit var totalValueTextView: TextView
    private lateinit var pemasukanDao: PemasukanDao
    private lateinit var pengeluaranDao: PengeluaranDao
    private lateinit var kategoriDao: KategoriDao

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_chart, container, false)

        // Inisialisasi view
        pieChart = view.findViewById(R.id.pieChart)
        toggleButton = view.findViewById(R.id.toggleButton)
        totalValueTextView = view.findViewById(R.id.totalValue)

        // Inisialisasi DAO
        val db = AppDatabase.getDatabase(requireContext())
        pemasukanDao = db.pemasukanDao()
        pengeluaranDao = db.pengeluaranDao()
        kategoriDao = db.kategoriDao()

        // Set default tampilan awal (misalnya, pengeluaran)
        toggleButton.check(R.id.buttonPengeluaran)
        loadDataForPieChart(isPengeluaran = true)

        // Menangani perubahan pada toggle button
        toggleButton.addOnButtonCheckedListener { _, checkedId, isChecked ->
            if (isChecked) {
                when (checkedId) {
                    R.id.buttonPemasukan -> loadDataForPieChart(isPengeluaran = false)
                    R.id.buttonPengeluaran -> loadDataForPieChart(isPengeluaran = true)
                }
            }
        }
        return view
    }

    private fun loadDataForPieChart(isPengeluaran: Boolean = true) {
        lifecycleScope.launch {
            try {
                val entries = if (isPengeluaran) {
                    withContext(Dispatchers.IO) {
                        pengeluaranDao.getAllOrderedByDateDescAndIdDesc()
                            .groupBy { it.kategori }
                            .map { (kategori, list) ->
                                PieEntry(list.sumOf { it.jumlah ?: 0.0 }.toFloat(), kategori ?: "Tidak Diketahui")
                            }
                    }
                } else {
                    withContext(Dispatchers.IO) {
                        pemasukanDao.getAllOrderedByDateDescAndIdDesc()
                            .groupBy { it.kategori }
                            .map { (kategori, list) ->
                                PieEntry(list.sumOf { it.jumlah ?: 0.0 }.toFloat(), kategori ?: "Tidak Diketahui")
                            }
                    }
                }

                // Jika tidak ada data, tampilkan pesan dan keluar
                if (entries.isEmpty()) {
                    withContext(Dispatchers.Main) {
                        Toast.makeText(requireContext(), "Tidak ada data untuk ditampilkan", Toast.LENGTH_SHORT).show()
                        pieChart.clear()
                        totalValueTextView.text = formatRupiah(0.0)
                    }
                    return@launch
                }
                val customColors = listOf(
                    Color.parseColor("#3A4AA1"),
                    Color.parseColor("#485ED5"),
                    Color.parseColor("#6E83F2"),
                    Color.parseColor("#8797E9"),
                    Color.parseColor("#B2BDF5"),
                    Color.parseColor("#D4DBFF"),
                )

                val dataSet = PieDataSet(entries, "Data Transaksi")
                dataSet.colors = customColors
                dataSet.valueTextColor = Color.WHITE
                dataSet.valueTextSize = 12f

                val pieData = PieData(dataSet)
                withContext(Dispatchers.Main) {
                    pieChart.data = pieData
                    pieChart.description.isEnabled = false
                    pieChart.centerText = if (isPengeluaran) "Pengeluaran" else "Pemasukan"
                    pieChart.setEntryLabelColor(Color.WHITE)
                    pieChart.animateY(1000)
                    pieChart.invalidate()

                    // Hitung dan tampilkan total
                    val total = entries.sumOf { it.value.toDouble() }
                    totalValueTextView.text = formatRupiah(total)
                }
            } catch (e: Exception) {
                Log.e("ChartFragment", "Error loading data: ${e.message}")
                withContext(Dispatchers.Main) {
                    Toast.makeText(requireContext(), "Terjadi kesalahan saat memuat data", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
    private fun formatRupiah(number: Double): String {
        val localeID = Locale("in", "ID") // Locale Indonesia
        val numberFormat = NumberFormat.getCurrencyInstance(localeID)
        return numberFormat.format(number).replace("Rp", "Rp ") // Tambahkan spasi setelah "Rp"
    }
}