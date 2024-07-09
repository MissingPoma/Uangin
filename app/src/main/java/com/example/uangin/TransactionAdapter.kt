package com.example.uangin

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.example.uangin.database.entity.Transactions
import java.text.SimpleDateFormat
import java.util.*

class TransactionAdapter(private val transactionList: List<Transactions>) :
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
        val idData: TextView = view.findViewById(R.id.idData)
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

        holder.idData.text = transaction.id.toString()
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
            val context = holder.itemView.context
            val intent = Intent(context, UpdateActivity::class.java).apply {
                putExtra("TRANSACTION_ID", transaction.id)
                putExtra("IS_PENGELUARAN", transaction.isPengeluaran)
                putExtra("TRANSACTION_AMOUNT", transaction.jumlah)
                putExtra("TRANSACTION_CATEGORY", transaction.kategori)
                putExtra("TRANSACTION_NOTE", transaction.catatan)
                putExtra("TRANSACTION_DATE", dateFormatMonthYear.format(transaction.tanggal))
            }
            context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int {
        return transactionList.size
    }
}