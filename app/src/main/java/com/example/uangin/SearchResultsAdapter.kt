package com.example.uangin

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.uangin.database.entity.Pemasukan
import com.example.uangin.database.entity.Pengeluaran
import java.text.SimpleDateFormat
import java.util.Locale

class SearchResultsAdapter(private val data: List<Any>) : RecyclerView.Adapter<SearchResultsAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.card_view_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = data[position]
        holder.bind(item)
    }

    override fun getItemCount(): Int = data.size

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val dateText: TextView = itemView.findViewById(R.id.dateText)
        private val dayText: TextView = itemView.findViewById(R.id.dayText)
        private val dayName: TextView = itemView.findViewById(R.id.dayName)
        private val categoryText: TextView = itemView.findViewById(R.id.categoryText)
        private val noteText: TextView = itemView.findViewById(R.id.noteText)
        private val amountText: TextView = itemView.findViewById(R.id.amountText)
        private val incomeText: TextView = itemView.findViewById(R.id.incomeText)
        private val idData: TextView = itemView.findViewById(R.id.idData)

        private val dateFormatDay = SimpleDateFormat("dd", Locale.getDefault())
        private val dateFormatMonthYear = SimpleDateFormat("MM/yyyy", Locale.getDefault())
        private val dateFormatDayName = SimpleDateFormat("EEEE", Locale.getDefault())

        fun bind(item: Any) {
            when (item) {
                is Pemasukan -> {
                    dateText.text = dateFormatDay.format(item.tanggal)
                    dayText.text = dateFormatMonthYear.format(item.tanggal)
                    dayName.text = dateFormatDayName.format(item.tanggal)

                    categoryText.text = item.kategori
                    noteText.text = item.catatan
                    amountText.text = "Rp ${item.jumlah}"
                    incomeText.text = "Rp ${item.jumlah}"
                    idData.text = item.id.toString()

                    amountText.setTextColor(itemView.context.getColor(R.color.greenIncome))
                    incomeText.setTextColor(itemView.context.getColor(R.color.greenIncome))
                }
                is Pengeluaran -> {
                    dateText.text = dateFormatDay.format(item.tanggal)
                    dayText.text = dateFormatMonthYear.format(item.tanggal)
                    dayName.text = dateFormatDayName.format(item.tanggal)

                    categoryText.text = item.kategori
                    noteText.text = item.catatan
                    amountText.text = "Rp ${item.jumlah}"
                    incomeText.text = "Rp ${item.jumlah}"
                    idData.text = item.id.toString()

                    amountText.setTextColor(itemView.context.getColor(R.color.redOutcome))
                    incomeText.setTextColor(itemView.context.getColor(R.color.redOutcome))
                }
            }
        }
    }
}
