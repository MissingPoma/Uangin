package com.example.uangin // Sesuaikan package

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.uangin.R
import com.example.uangin.database.entity.Kategori // Sesuaikan import

class KategoriAdapter(
    private var kategoriList: MutableList<Kategori>,
    private val onEditClickListener: (Kategori) -> Unit,
    private val onDeleteClickListener: (Kategori) -> Unit
) : RecyclerView.Adapter<KategoriAdapter.KategoriViewHolder>() {

    class KategoriViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val categoryName: TextView = itemView.findViewById(R.id.categoryNameList)
        val editButton: ImageButton = itemView.findViewById(R.id.editButton)
        val deleteButton: ImageButton = itemView.findViewById(R.id.deleteButton)
    }

    fun updateData(newKategoriList: List<Kategori>) {
        kategoriList.clear() // Bersihkan list yang ada
        kategoriList.addAll(newKategoriList) // Tambahkan data baru
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): KategoriViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.item_list_category, parent, false)
        return KategoriViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: KategoriViewHolder, position: Int) {
        val currentItem = kategoriList[position]
        holder.categoryName.text = currentItem.namaKategori

        holder.editButton.setOnClickListener {
            onEditClickListener(currentItem)
        }

        holder.deleteButton.setOnClickListener {
            onDeleteClickListener(currentItem)
        }
    }

    override fun getItemCount(): Int = kategoriList.size
}
