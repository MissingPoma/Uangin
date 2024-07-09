package com.example.uangin

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.example.uangin.R
import com.example.uangin.database.entity.Transaction
import java.text.SimpleDateFormat
import java.util.*

class TransactionAdapter(private val transactionList: List<Transaction>) :
    RecyclerView.Adapter<TransactionAdapter.TransactionViewHolder>() {

    class TransactionViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val cardView: CardView = view.findViewById(R.id.card)
        val dateText: TextView = view.findViewById(R.id.dateText)
        val categoryText: TextView = view.findViewById(R.id.categoryText)
        val noteText: TextView = view.findViewById(R.id.noteText)
        val amountText: TextView = view.findViewById(R.id.amountText)
        val incomeText: TextView = view.findViewById(R.id.incomeText)
        val dayName: TextView = view.findViewById(R.id.dayName)
        val dayText: TextView = view.findViewById(R.id.dayText)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TransactionViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.card_view_item, parent, false)
        return TransactionViewHolder(view)
    }

    override fun onBindViewHolder(holder: TransactionViewHolder, position: Int) {
        val transaction = transactionList[position]
        val dateFormatDay = SimpleDateFormat("dd", Locale.getDefault())
        val dateFormatMonthYear = SimpleDateFormat("MM/yyyy", Locale.getDefault())
        val dateFormatDayName = SimpleDateFormat("EEEE", Locale.getDefault())

        holder.dateText.text = dateFormatDay.format(transaction.tanggal)
        holder.dayText.text = dateFormatMonthYear.format(transaction.tanggal)
        holder.dayName.text = dateFormatDayName.format(transaction.tanggal)

        holder.categoryText.text = transaction.kategori
        holder.noteText.text = transaction.catatan
        holder.amountText.text = "Rp ${transaction.jumlah}"
        holder.incomeText.text = "Rp ${transaction.jumlah}"

        if (transaction.isPengeluaran) {
            holder.amountText.setTextColor(holder.itemView.context.getColor(R.color.redOutcome))
            holder.incomeText.setTextColor(holder.itemView.context.getColor(R.color.redOutcome))
        } else {
            holder.amountText.setTextColor(holder.itemView.context.getColor(R.color.greenIncome))
            holder.incomeText.setTextColor(holder.itemView.context.getColor(R.color.greenIncome))
        }

        holder.cardView.setOnClickListener {
            // Handle click event
        }
    }

    override fun getItemCount(): Int {
        return transactionList.size
    }
}
