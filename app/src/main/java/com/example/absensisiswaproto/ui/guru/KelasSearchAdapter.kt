package com.example.absensisiswaproto.ui.guru

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.absensisiswaproto.R

data class KelasSearchItem(
    val nama: String,
    val jumlahSiswa: Int
)

class KelasSearchAdapter(
    private var list: List<KelasSearchItem>,
    private val onClick: (KelasSearchItem) -> Unit
) : RecyclerView.Adapter<KelasSearchAdapter.ViewHolder>() {

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvNamaKelasSearch: TextView = view.findViewById(R.id.tvNamaKelasSearch)
        val tvJumlahSiswa: TextView = view.findViewById(R.id.tvJumlahSiswa)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_kelas_search, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = list[position]
        holder.tvNamaKelasSearch.text = item.nama
        holder.tvJumlahSiswa.text = "${item.jumlahSiswa} Siswa"
        holder.itemView.setOnClickListener { onClick(item) }
    }

    override fun getItemCount(): Int = list.size

    fun updateData(newList: List<KelasSearchItem>) {
        list = newList
        notifyDataSetChanged()
    }
}